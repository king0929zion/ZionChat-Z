package me.rerere.rikkahub.data.ai.tools

import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import me.rerere.ai.core.Tool
import me.rerere.ai.core.Tool.InputSchema
import me.rerere.ai.ui.UIMessagePart
import me.rerere.rikkahub.data.datastore.SettingsStore
import me.rerere.rikkahub.data.plugin.AcRemoteConfig
import me.rerere.rikkahub.data.plugin.AcRemoteFan
import me.rerere.rikkahub.data.plugin.AcRemoteIrManager
import me.rerere.rikkahub.data.plugin.AcRemoteMode

private fun stringField(description: String) = buildJsonObject {
    put("type", "string")
    put("description", description)
}

private fun integerField(description: String) = buildJsonObject {
    put("type", "integer")
    put("description", description)
}

private fun JsonElement.argString(key: String): String? =
    jsonObject[key]?.jsonPrimitive?.contentOrNull?.trim()?.takeIf { it.isNotEmpty() }

private fun JsonElement.argInt(key: String): Int? =
    jsonObject[key]?.jsonPrimitive?.intOrNull

/**
 * 空调红外遥控本地工具
 *
 * 让 AI 通过手机红外发射器控制空调 (当前支持 Gree/格力协议):
 * 开关机 / 温度加减与设定 / 模式切换 / 风速切换。
 */
class AcRemoteTools(
    private val context: Context,
    private val settingsStore: SettingsStore,
) {
    private val irManager by lazy { AcRemoteIrManager(context.applicationContext) }

    fun getTools(): List<Tool> = listOf(
        Tool(
            name = "air_conditioner_remote",
            description = "Control the air conditioner via the phone's IR blaster. " +
                "Supported actions: power_on, power_off, temperature_up, temperature_down, " +
                "set_temperature, set_mode, set_fan_speed. Currently supports Gree ACs.",
            parameters = {
                InputSchema.Obj(
                    properties = buildJsonObject {
                        put("action", stringField("Required. One of: power_on, power_off, temperature_up, temperature_down, set_temperature, set_mode, set_fan_speed."))
                        put("temperature", integerField("Optional. Target temperature in Celsius (16-30) for set_temperature."))
                        put("mode", stringField("Optional. One of: auto, cool, dry, fan, heat. For set_mode."))
                        put("fan_speed", stringField("Optional. One of: auto, low, medium, high. For set_fan_speed."))
                    },
                    required = listOf("action")
                )
            },
            needsApproval = { false },
            execute = { args -> listOf(UIMessagePart.Text(execute(args))) }
        )
    )

    private suspend fun execute(args: JsonElement): String {
        val settings = settingsStore.settingsFlow.first()
        val current = settings.pluginSettings.acRemote
        if (!current.enabled) {
            return "空调红外遥控插件未启用, 请先在 插件 -> 空调红外遥控 中开启。"
        }

        val action = args.argString("action")?.lowercase()
            ?: return "缺少 action 参数"

        var next = current
        val description: String

        when (action) {
            "power_on" -> {
                next = current.copy(power = true)
                description = "开机"
            }

            "power_off" -> {
                next = current.copy(power = false)
                description = "关机"
            }

            "temperature_up" -> {
                val target = (current.temperature + 1).coerceIn(AcRemoteConfig.MIN_TEMPERATURE, AcRemoteConfig.MAX_TEMPERATURE)
                next = current.copy(temperature = target, power = true)
                description = "温度升高到 ${target}°C"
            }

            "temperature_down" -> {
                val target = (current.temperature - 1).coerceIn(AcRemoteConfig.MIN_TEMPERATURE, AcRemoteConfig.MAX_TEMPERATURE)
                next = current.copy(temperature = target, power = true)
                description = "温度降低到 ${target}°C"
            }

            "set_temperature" -> {
                val target = args.argInt("temperature")
                    ?: return "缺少 temperature 参数"
                val clamped = target.coerceIn(AcRemoteConfig.MIN_TEMPERATURE, AcRemoteConfig.MAX_TEMPERATURE)
                next = current.copy(temperature = clamped, power = true)
                description = "温度设定为 ${clamped}°C"
            }

            "set_mode" -> {
                val mode = args.argString("mode")
                    ?: return "缺少 mode 参数"
                val modeEnum = parseMode(mode) ?: return "不支持的模式: $mode (可选 auto/cool/dry/fan/heat)"
                next = current.copy(mode = modeEnum, power = true)
                description = "模式切换为 ${modeLabel(modeEnum)}"
            }

            "set_fan_speed" -> {
                val fan = args.argString("fan_speed")
                    ?: return "缺少 fan_speed 参数"
                val fanEnum = parseFan(fan) ?: return "不支持的风速: $fan (可选 auto/low/medium/high)"
                next = current.copy(fan = fanEnum, power = true)
                description = "风速切换为 ${fanLabel(fanEnum)}"
            }

            else -> return "不支持的 action: $action"
        }

        // 持久化状态并发射红外
        settingsStore.update(
            settings.copy(
                pluginSettings = settings.pluginSettings.copy(acRemote = next)
            )
        )
        return when (val ir = irManager.transmit(next, repeat = 1)) {
            is AcRemoteIrManager.IrResult.Success ->
                "已发送红外指令: $description。当前状态: ${if (next.power) "开机" else "关机"}, ${next.temperature}°C, ${modeLabel(next.mode)}, ${fanLabel(next.fan)}。"

            is AcRemoteIrManager.IrResult.Error ->
                "红外发送失败: ${ir.message} (指令 $description 已记录)"
        }
    }

    private fun parseMode(value: String): AcRemoteMode? = when (value.lowercase()) {
        "auto" -> AcRemoteMode.AUTO
        "cool" -> AcRemoteMode.COOL
        "dry" -> AcRemoteMode.DRY
        "fan" -> AcRemoteMode.FAN
        "heat" -> AcRemoteMode.HEAT
        else -> null
    }

    private fun parseFan(value: String): AcRemoteFan? = when (value.lowercase()) {
        "auto" -> AcRemoteFan.AUTO
        "low", "min" -> AcRemoteFan.LOW
        "medium", "med" -> AcRemoteFan.MEDIUM
        "high", "max" -> AcRemoteFan.HIGH
        else -> null
    }

    fun modeLabel(mode: AcRemoteMode): String = when (mode) {
        AcRemoteMode.AUTO -> "自动"
        AcRemoteMode.COOL -> "制冷"
        AcRemoteMode.DRY -> "除湿"
        AcRemoteMode.FAN -> "送风"
        AcRemoteMode.HEAT -> "制热"
    }

    fun fanLabel(fan: AcRemoteFan): String = when (fan) {
        AcRemoteFan.AUTO -> "自动风"
        AcRemoteFan.LOW -> "低风"
        AcRemoteFan.MEDIUM -> "中风"
        AcRemoteFan.HIGH -> "高风"
    }
}
