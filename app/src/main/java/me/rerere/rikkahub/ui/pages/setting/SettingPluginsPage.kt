package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.rikkahub.R
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.data.plugin.AcRemoteConfig
import me.rerere.rikkahub.data.plugin.PluginStore
import me.rerere.rikkahub.data.plugin.StorePluginSpec
import me.rerere.rikkahub.data.plugin.TelegramPluginConfig
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionGrayLight
import me.rerere.rikkahub.ui.theme.ZionSectionItem
import me.rerere.rikkahub.ui.theme.ZionSurface
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import org.koin.androidx.compose.koinViewModel

/**
 * 插件中心
 *
 * 三个分区: Enabled plugins (已启用) / Plugin Store (商店入口) / Installed (已安装 N 个插件)
 */
@Composable
fun SettingPluginsPage(vm: SettingVM = koinViewModel()) {
    val navController = LocalNavController.current
    val settings by vm.settings.collectAsStateWithLifecycle()
    val pluginSettings = settings.pluginSettings

    val installedStorePlugins = remember(pluginSettings) {
        PluginStore.PLUGINS.filter { pluginSettings.store.isInstalled(it.id) }
    }

    SettingsPage(
        title = stringResource(R.string.plugins_page_title),
        onBack = { navController.popBackStack() },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                top = PageTopBarContentTopPadding,
                bottom = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // ---------------- Enabled plugins ----------------
            item("enabledHeader") {
                SectionHeader(text = "Enabled plugins")
            }
            item("enabledTelegram") {
                PluginRow(
                    icon = { PluginTelegramLogo() },
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
                        icon = { PluginAcRemoteLogo() },
                        title = "空调红外遥控",
                        subtitle = acRemoteSubtitle(pluginSettings.acRemote),
                        checked = pluginSettings.acRemote.enabled,
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
            installedStorePlugins
                .filter { pluginSettings.store.configOf(it.id)?.enabled == true }
                .forEach { spec ->
                    item("enabled_${spec.id}") {
                        PluginRow(
                            icon = { PluginLetterLogo(letter = spec.name.firstOrNull()?.toString() ?: "P") },
                            title = spec.name,
                            subtitle = spec.category,
                            checked = true,
                            onCheckedChange = { enabled ->
                                val config = pluginSettings.store.configOf(spec.id) ?: return@PluginRow
                                vm.updateSettings(
                                    me.rerere.rikkahub.data.plugin.StorePluginMcpSync.apply(
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
                SectionHeader(text = "Plugin Store")
            }
            item("storeEntry") {
                PluginStoreEntryRow(
                    onClick = { navController.navigate(Screen.PluginStore) },
                )
            }

            // ---------------- Installed ----------------
            item("installedHeader") {
                SectionHeader(
                    text = "Installed — ${2 + installedStorePlugins.size} plugins"
                )
            }
            item("installedTelegram") {
                PluginRow(
                    icon = { PluginTelegramLogo() },
                    title = "Telegram",
                    subtitle = "Bot / Telegram 插件",
                    onClick = { navController.navigate(Screen.SettingTelegramPlugin) },
                )
            }
            item("installedAcRemote") {
                PluginRow(
                    icon = { PluginAcRemoteLogo() },
                    title = "空调红外遥控",
                    subtitle = "Built-in / 红外遥控插件",
                    onClick = { navController.navigate(Screen.SettingAcRemotePlugin) },
                )
            }
            installedStorePlugins.forEach { spec ->
                item("installed_${spec.id}") {
                    PluginRow(
                        icon = { PluginLetterLogo(letter = spec.name.firstOrNull()?.toString() ?: "P") },
                        title = spec.name,
                        subtitle = "MCP / ${spec.category}",
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

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = SourceSans3,
        color = ZionTextSecondary,
        modifier = Modifier.padding(start = 20.dp, top = 10.dp, bottom = 2.dp)
    )
}

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
            .background(ZionSurface)
            .pressableScale(pressedScale = 0.98f, onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        icon()
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = SourceSans3,
                color = ZionTextPrimary,
                maxLines = 1,
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                fontFamily = SourceSans3,
                color = ZionTextSecondary,
                maxLines = 1,
            )
        }
        if (onCheckedChange != null && checked != null) {
            PluginSystemSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
    }
}

@Composable
private fun PluginStoreEntryRow(
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(60.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(ZionTextPrimary)
            .pressableScale(pressedScale = 0.98f, onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "S",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = SourceSans3,
                color = ZionTextPrimary,
            )
        }
        Text(
            text = "Plugin Store",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = SourceSans3,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = ZionAppIcons.ChevronRight,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
    }
}

// ------------------------- 图标 -------------------------

@Composable
private fun PluginTelegramLogo() {
    PluginIconTile {
        Icon(
            painter = painterResource(R.drawable.ic_plugin_telegram),
            contentDescription = "Telegram",
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun PluginAcRemoteLogo() {
    PluginIconTile {
        Icon(
            imageVector = ZionAppIcons.Sun,
            contentDescription = "AC Remote",
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun PluginLetterLogo(
    letter: String,
    dark: Boolean = false,
) {
    PluginIconTile(dark = dark) {
        Text(
            text = letter,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = SourceSans3,
            color = if (dark) ZionTextPrimary else Color.White,
        )
    }
}

@Composable
private fun PluginIconTile(
    dark: Boolean = false,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (dark) ZionGrayLight else Color.Black),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
