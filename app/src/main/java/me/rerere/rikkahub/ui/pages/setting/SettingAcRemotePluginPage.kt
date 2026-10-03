package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dokar.sonner.ToastType
import me.rerere.rikkahub.data.plugin.AcRemoteFan
import me.rerere.rikkahub.data.plugin.AcRemoteIrManager
import me.rerere.rikkahub.data.plugin.AcRemoteMode
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SectionLabel
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.context.LocalToaster
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionGrayLight
import me.rerere.rikkahub.ui.theme.ZionGrayLighter
import me.rerere.rikkahub.ui.theme.ZionSectionItem
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import org.koin.androidx.compose.koinViewModel

/**
 * 空调红外遥控插件页
 *
 * 遥控面板: 开关 / 温度 +- / 模式 / 风速, 每次操作立即发射红外并持久化状态。
 * 当前支持 Gree (格力) 遥控协议。
 */
@Composable
fun SettingAcRemotePluginPage(vm: SettingVM = koinViewModel()) {
    val navController = LocalNavController.current
    val toaster = LocalToaster.current
    val context = LocalContext.current
    val settings by vm.settings.collectAsStateWithLifecycle()
    val config = settings.pluginSettings.acRemote

    val irAvailable = remember {
        try {
            (context.getSystemService(android.content.Context.CONSUMER_IR_SERVICE)
                as? android.hardware.ConsumerIrManager)?.hasIrEmitter() == true
        } catch (_: Exception) {
            false
        }
    }

    val irManager = remember { AcRemoteIrManager(context.applicationContext) }

    fun update(transform: (me.rerere.rikkahub.data.plugin.AcRemoteConfig) -> me.rerere.rikkahub.data.plugin.AcRemoteConfig) {
        val next = transform(settings.pluginSettings.acRemote)
        vm.updateSettings(
            settings.copy(
                pluginSettings = settings.pluginSettings.copy(acRemote = next)
            )
        )
        when (val result = irManager.transmit(next, repeat = 1)) {
            is AcRemoteIrManager.IrResult.Success -> {}
            is AcRemoteIrManager.IrResult.Error ->
                toaster.show(result.message, type = ToastType.Error)
        }
    }

    SettingsPage(
        title = "空调红外遥控",
        onBack = { navController.popBackStack() },
        trailing = {
            PluginSystemSwitch(
                checked = config.enabled,
                onCheckedChange = { enabled ->
                    vm.updateSettings(
                        settings.copy(
                            pluginSettings = settings.pluginSettings.copy(
                                acRemote = config.copy(enabled = enabled)
                            )
                        )
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = PageTopBarContentTopPadding)
                .padding(horizontal = 16.dp),
        ) {
            if (!irAvailable) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(ZionGrayLighter)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = ZionAppIcons.Info,
                        contentDescription = null,
                        tint = ZionTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "此设备没有红外发射器。AI 工具仍可调用, 但需要在带红外的设备上使用。",
                        fontSize = 12.sp,
                        fontFamily = SourceSans3,
                        color = ZionTextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ---------------- 遥控主面板 ----------------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ZionSectionItem)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 品牌 + 状态
                Text(
                    text = "Gree · 格力",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = SourceSans3,
                    color = ZionTextSecondary
                )

                // 温度显示
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // -
                    RoundRemoteButton(
                        icon = ZionAppIcons.Settings,
                        contentDescription = "降低温度",
                        onClick = {
                            update {
                                it.copy(
                                    power = true,
                                    temperature = (it.temperature - 1)
                                        .coerceIn(me.rerere.rikkahub.data.plugin.AcRemoteConfig.MIN_TEMPERATURE, me.rerere.rikkahub.data.plugin.AcRemoteConfig.MAX_TEMPERATURE)
                                )
                            }
                        }
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${config.temperature}°",
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = SourceSans3,
                            color = ZionTextPrimary
                        )
                        Text(
                            text = if (config.power) {
                                "${modeLabelOf(config.mode)} · ${fanLabelOf(config.fan)}"
                            } else {
                                "待机"
                            },
                            fontSize = 12.sp,
                            fontFamily = SourceSans3,
                            color = ZionTextSecondary
                        )
                    }
                    // +
                    RoundRemoteButton(
                        icon = ZionAppIcons.Plus,
                        contentDescription = "升高温度",
                        onClick = {
                            update {
                                it.copy(
                                    power = true,
                                    temperature = (it.temperature + 1)
                                        .coerceIn(me.rerere.rikkahub.data.plugin.AcRemoteConfig.MIN_TEMPERATURE, me.rerere.rikkahub.data.plugin.AcRemoteConfig.MAX_TEMPERATURE)
                                )
                            }
                        }
                    )
                }

                // 电源
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(if (config.power) ZionTextPrimary else ZionGrayLight)
                        .pressableScale(pressedScale = 0.92f) {
                            update { it.copy(power = !it.power) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (config.power) "ON" else "OFF",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SourceSans3,
                        color = if (config.power) Color.White else ZionTextPrimary
                    )
                }
            }

            // ---------------- 模式 ----------------
            SectionLabel(text = "模式")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AcRemoteMode.entries.forEach { mode ->
                    RemoteChip(
                        text = modeLabelOf(mode),
                        selected = config.mode == mode && config.power,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            update { it.copy(mode = mode, power = true) }
                        }
                    )
                }
            }

            // ---------------- 风速 ----------------
            SectionLabel(text = "风速")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AcRemoteFan.entries.forEach { fan ->
                    RemoteChip(
                        text = fanLabelOf(fan),
                        selected = config.fan == fan && config.power,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            update { it.copy(fan = fan, power = true) }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

private fun modeLabelOf(mode: AcRemoteMode): String = when (mode) {
    AcRemoteMode.AUTO -> "自动"
    AcRemoteMode.COOL -> "制冷"
    AcRemoteMode.DRY -> "除湿"
    AcRemoteMode.FAN -> "送风"
    AcRemoteMode.HEAT -> "制热"
}

private fun fanLabelOf(fan: AcRemoteFan): String = when (fan) {
    AcRemoteFan.AUTO -> "自动"
    AcRemoteFan.LOW -> "低风"
    AcRemoteFan.MEDIUM -> "中风"
    AcRemoteFan.HIGH -> "高风"
}

@Composable
private fun RoundRemoteButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color.White)
            .pressableScale(pressedScale = 0.9f, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = ZionTextPrimary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun RemoteChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) ZionTextPrimary else Color.White)
            .pressableScale(pressedScale = 0.95f, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = SourceSans3,
            color = if (selected) Color.White else ZionTextPrimary,
            maxLines = 1
        )
    }
}
