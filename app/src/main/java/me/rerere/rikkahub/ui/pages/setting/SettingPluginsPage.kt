package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Wind
import com.composables.icons.lucide.Package


import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Send
import com.composables.icons.lucide.Store
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.data.plugin.AcRemoteConfig
import me.rerere.rikkahub.data.plugin.PluginStore
import me.rerere.rikkahub.data.plugin.StorePluginMcpSync
import me.rerere.rikkahub.data.plugin.TelegramPluginConfig
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionGrayLighter
import me.rerere.rikkahub.ui.theme.ZionSectionItem
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import org.koin.androidx.compose.koinViewModel

private val PluginCardGray = ZionSectionItem

/**
 * 插件中心
 *
 * 三个分区: Enabled plugins (已启用) / Plugin Store (商店入口) / Installed (已安装 N 个插件)。
 * 视觉沿用项目统一列表行: 灰色 20dp 圆角卡片 + 60dp 行高 + Lucide 图标。
 */
@Composable
fun SettingPluginsPage(vm: SettingVM = koinViewModel()) {
    val navController = LocalNavController.current
    val settings by vm.settings.collectAsStateWithLifecycle()
    val pluginSettings = settings.pluginSettings

    val installedStorePlugins = PluginStore.PLUGINS.filter { pluginSettings.store.isInstalled(it.id) }
    val enabledStorePlugins =
        installedStorePlugins.filter { pluginSettings.store.configOf(it.id)?.enabled == true }

    SettingsPage(
        title = "Plugins",
        onBack = { navController.popBackStack() },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = PageTopBarContentTopPadding,
                bottom = 28.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // ---------------- Enabled plugins ----------------
            item("enabledHeader") {
                SectionHeaderText("Enabled plugins")
            }
            item("enabledTelegram") {
                PluginRow(
                    icon = { PluginIcon { Icon(Lucide.Send, null, Modifier.size(18.dp)) } },
                    title = "Telegram",
                    subtitle = telegramSubtitle(pluginSettings.telegram),
                    checked = pluginSettings.telegram.enabled,
                    onCheckedChange = { enabled ->
                        vm.updateSettings(
                            settings.copy(
                                pluginSettings = pluginSettings.copy(
                                    telegram = pluginSettings.telegram.copy(enabled = enabled)
                                )
                            )
                        )
                    },
                    onClick = { navController.navigate(Screen.SettingTelegramPlugin) },
                )
            }
            if (pluginSettings.acRemote.enabled) {
                item("enabledAcRemote") {
                    PluginRow(
                        icon = { PluginIcon { Icon(Lucide.Wind, null, Modifier.size(18.dp)) } },
                        title = "空调红外遥控",
                        subtitle = acRemoteSubtitle(pluginSettings.acRemote),
                        checked = true,
                        onCheckedChange = { enabled ->
                            vm.updateSettings(
                                settings.copy(
                                    pluginSettings = pluginSettings.copy(
                                        acRemote = pluginSettings.acRemote.copy(enabled = enabled)
                                    )
                                )
                            )
                        },
                        onClick = { navController.navigate(Screen.SettingAcRemotePlugin) },
                    )
                }
            }
            enabledStorePlugins.forEach { spec ->
                item("enabled_${spec.id}") {
                    PluginRow(
                        icon = { PluginIcon { Icon(Lucide.Package, null, Modifier.size(18.dp)) } },
                        title = spec.name,
                        subtitle = spec.category,
                        checked = true,
                        onCheckedChange = { enabled ->
                            val config = pluginSettings.store.configOf(spec.id) ?: return@PluginRow
                            vm.updateSettings(
                                StorePluginMcpSync.apply(
                                    settings, spec, config.copy(enabled = enabled)
                                )
                            )
                        },
                        onClick = { navController.navigate(Screen.PluginDetail(spec.id)) },
                    )
                }
            }

            // ---------------- Plugin Store ----------------
            item("storeHeader") {
                SectionHeaderText("Plugin Store")
            }
            item("storeEntry") {
                PluginStoreEntryRow(
                    onClick = { navController.navigate(Screen.PluginStore) },
                )
            }

            // ---------------- Installed ----------------
            item("installedHeader") {
                SectionHeaderText("Installed — ${2 + installedStorePlugins.size} plugins")
            }
            item("installedTelegram") {
                PluginRow(
                    icon = { PluginIcon { Icon(Lucide.Send, null, Modifier.size(18.dp)) } },
                    title = "Telegram",
                    subtitle = "Bot 接入",
                    onClick = { navController.navigate(Screen.SettingTelegramPlugin) },
                )
            }
            item("installedAcRemote") {
                PluginRow(
                    icon = { PluginIcon { Icon(Lucide.Wind, null, Modifier.size(18.dp)) } },
                    title = "空调红外遥控",
                    subtitle = "Built-in 红外遥控",
                    onClick = { navController.navigate(Screen.SettingAcRemotePlugin) },
                )
            }
            installedStorePlugins.forEach { spec ->
                item("installed_${spec.id}") {
                    PluginRow(
                        icon = { PluginIcon { Icon(Lucide.Package, null, Modifier.size(18.dp)) } },
                        title = spec.name,
                        subtitle = "MCP · ${spec.category}",
                        onClick = { navController.navigate(Screen.PluginDetail(spec.id)) },
                    )
                }
            }
        }
    }
}

private fun telegramSubtitle(config: TelegramPluginConfig): String = when {
    !config.enabled -> "未启用"
    !config.hasToken() -> "缺少 Bot Token"
    !config.hasAllowedUsers() -> "未配置允许的用户"
    else -> "${config.allowedIdentityCount()} 个授权用户"
}

private fun acRemoteSubtitle(config: AcRemoteConfig): String = when {
    !config.enabled -> "未启用"
    config.power -> "${config.temperature}°C · 运行中"
    else -> "${config.temperature}°C · 待机"
}

/** 分区小标题 */
@Composable
private fun SectionHeaderText(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontFamily = SourceSans3,
        color = ZionTextSecondary,
        modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 2.dp)
    )
}

/** 插件图标块 (与卡片同灰系, 图形为 Lucide 灰图标) */
@Composable
private fun PluginIcon(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(ZionGrayLighter),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.size(18.dp)) { content() }
    }
}

/** 插件列表行 (项目统一卡片样式) */
@Composable
private fun PluginRow(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    checked: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(60.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(PluginCardGray, RoundedCornerShape(20.dp))
            .pressableScale(pressedScale = 0.98f, onClick = onClick)
            .padding(start = 14.dp, end = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        icon()
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = SourceSans3,
                color = ZionTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                fontFamily = SourceSans3,
                color = ZionTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (onCheckedChange != null && checked != null) {
            PluginSystemSwitch(checked = checked, onCheckedChange = onCheckedChange)
        } else {
            Icon(
                imageVector = Lucide.ChevronRight,
                contentDescription = null,
                tint = ZionTextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/** 商店入口行 (黑色强调卡片) */
@Composable
private fun PluginStoreEntryRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(60.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(ZionTextPrimary, RoundedCornerShape(20.dp))
            .pressableScale(pressedScale = 0.98f, onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Lucide.Store,
                contentDescription = null,
                tint = ZionTextPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = "Plugin Store",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = SourceSans3,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Lucide.ChevronRight,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
    }
}
