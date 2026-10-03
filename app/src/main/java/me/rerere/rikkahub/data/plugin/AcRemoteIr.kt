package me.rerere.rikkahub.data.plugin

import android.content.Context
import android.hardware.ConsumerIrManager
import android.os.Build

/**
 * Gree (格力) 空调红外协议编码
 *
 * 依据 IRremoteESP8266 的 ir_Gree 实现 (YAW1F / YBOFB 系列遥控):
 * - 38kHz 载波, 8 字节状态帧
 * - 结构: 9000/4500 引导码 + 4 字节 (32 bit) + 3 bit 常量 010 + 4 字节 (32 bit) + 尾 620/19980
 * - bit: mark 620us + space 1600us (1) / 540us (0), MSB first
 * - 校验: Kelvinator 块校验 (起始值 10, 前 4 字节取低 4 位求和 + 后 3 字节取高 4 位求和, & 0x0F)
 */
object GreeProtocol {
    const val CARRIER_FREQUENCY = 38000

    private const val HDR_MARK = 9000
    private const val HDR_SPACE = 4500
    private const val BIT_MARK = 620
    private const val ONE_SPACE = 1600
    private const val ZERO_SPACE = 540
    private const val MSG_SPACE = 19980

    /** 模式值: Auto=0, Cool=1, Dry=2, Fan=3, Heat=4 */
    private fun modeValue(mode: AcRemoteMode): Int = when (mode) {
        AcRemoteMode.AUTO -> 0
        AcRemoteMode.COOL -> 1
        AcRemoteMode.DRY -> 2
        AcRemoteMode.FAN -> 3
        AcRemoteMode.HEAT -> 4
    }

    /** 风速值: Auto=0, Min=1, Med=2, Max=3 */
    private fun fanValue(fan: AcRemoteFan): Int = when (fan) {
        AcRemoteFan.AUTO -> 0
        AcRemoteFan.LOW -> 1
        AcRemoteFan.MEDIUM -> 2
        AcRemoteFan.HIGH -> 3
    }

    /**
     * 构造 8 字节状态帧
     *
     * B0: Mode(0-2) | Power(3) | Fan(4-5) | SwingAuto(6) | Sleep(7)
     * B1: Temp-16(0-3) | Timer(4-7) = 0
     * B2: Timer(0-3)=0 | Turbo(4)=0 | Light(5)=1 | ModelA(6)=Power | Xfan(7)=0
     * B3: 0x50 (unknown 常量)
     * B4: SwingV=0 | SwingH=0
     * B5: 0x20 (unknown 常量)
     * B6: 0
     * B7: bit2 Econo=0 | bit4-7 校验和
     */
    fun buildState(config: AcRemoteConfig): ByteArray {
        val state = ByteArray(8)
        val temp = config.temperature.coerceIn(AcRemoteConfig.MIN_TEMPERATURE, AcRemoteConfig.MAX_TEMPERATURE)

        var b0 = modeValue(config.mode) and 0x07
        if (config.power) b0 = b0 or 0x08
        b0 = b0 or ((fanValue(config.fan) and 0x03) shl 4)
        state[0] = b0.toByte()

        state[1] = ((temp - AcRemoteConfig.MIN_TEMPERATURE) and 0x0F).toByte()

        var b2 = 0x20 // Light on
        if (config.power) b2 = b2 or 0x40 // ModelA (YAW1F power bit)
        state[2] = b2.toByte()

        state[3] = 0x50
        state[4] = 0x00
        state[5] = 0x20
        state[6] = 0x00

        val checksum = calcChecksum(state)
        state[7] = (checksum and 0x0F).toByte()
        return state
    }

    /** Kelvinator 块校验: 10 + 低 4 位(前 4 字节) + 高 4 位(后 3 字节), 取低 4 位 */
    private fun calcChecksum(state: ByteArray): Int {
        var sum = 10
        for (i in 0 until 4) {
            sum += state[i].toInt() and 0x0F
        }
        for (i in 4 until 7) {
            sum += (state[i].toInt() and 0xFF) shr 4
        }
        return sum and 0x0F
    }

    /** 编码为 ConsumerIrManager 可用的微秒交替序列 (mark/space 交替, 以 mark 开始) */
    fun toPattern(state: ByteArray, repeat: Int = 1): IntArray {
        val out = ArrayList<Int>(200 * (repeat + 1))
        repeat(repeat) {
            // 引导码
            out.add(HDR_MARK)
            out.add(HDR_SPACE)
            // 前 4 字节
            appendBytes(out, state, 0, 4)
            // 中段 3 bit 常量 010 + 消息间隔
            appendBits(out, 0b010, 3)
            out.add(BIT_MARK)
            out.add(MSG_SPACE)
            // 后 4 字节
            appendBytes(out, state, 4, 4)
            // 结尾
            out.add(BIT_MARK)
            out.add(MSG_SPACE)
        }
        return out.toIntArray()
    }

    private fun appendBytes(out: ArrayList<Int>, state: ByteArray, offset: Int, count: Int) {
        for (i in offset until offset + count) {
            val byte = state[i].toInt() and 0xFF
            for (bit in 7 downTo 0) {
                out.add(BIT_MARK)
                out.add(if ((byte shr bit) and 1 == 1) ONE_SPACE else ZERO_SPACE)
            }
        }
    }

    private fun appendBits(out: ArrayList<Int>, value: Int, bits: Int) {
        for (bit in bits - 1 downTo 0) {
            out.add(BIT_MARK)
            out.add(if ((value shr bit) and 1 == 1) ONE_SPACE else ZERO_SPACE)
        }
    }
}

/**
 * 红外发射管理
 *
 * 使用 ConsumerIrManager 发射; 无红外硬件的设备返回错误信息。
 */
class AcRemoteIrManager(
    private val context: Context,
) {
    private val irManager: ConsumerIrManager? by lazy {
        context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager
    }

    fun hasIrEmitter(): Boolean = try {
        irManager?.hasIrEmitter() == true
    } catch (_: Exception) {
        false
    }

    /** 发射结果 */
    sealed class IrResult {
        data object Success : IrResult()
        data class Error(val message: String) : IrResult()
    }

    fun transmit(config: AcRemoteConfig, repeat: Int = 1): IrResult {
        val manager = irManager
            ?: return IrResult.Error("此设备不支持红外发射 (无 ConsumerIrManager)")
        if (!hasIrEmitter()) {
            return IrResult.Error("此设备没有红外发射器, 无法控制空调")
        }
        return try {
            val state = when (config.brand) {
                AcRemoteBrand.GREE -> GreeProtocol.buildState(config)
            }
            val carrier = when (config.brand) {
                AcRemoteBrand.GREE -> GreeProtocol.CARRIER_FREQUENCY
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val ranges = manager.getCarrierFrequencies()
                if (ranges != null && ranges.isNotEmpty()) {
                    // 硬件不支持 38kHz 时给出提示但仍尝试发射
                    val supported = ranges.any { it.minFrequency <= carrier && carrier <= it.maxFrequency }
                    if (!supported) {
                        // 仍尝试发射, 部分设备载波范围上报不准确
                    }
                }
            }
            manager.transmit(carrier, when (config.brand) {
                AcRemoteBrand.GREE -> GreeProtocol.toPattern(state, repeat)
            })
            IrResult.Success
        } catch (e: Exception) {
            IrResult.Error("红外发射失败: ${e.message ?: "未知错误"}")
        }
    }
}
