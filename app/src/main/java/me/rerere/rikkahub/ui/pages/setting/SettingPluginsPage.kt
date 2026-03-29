package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.rikkahub.data.datastore.PluginSettings
import me.rerere.rikkahub.data.datastore.Settings
import me.rerere.rikkahub.data.datastore.XToolPluginSettings
import me.rerere.rikkahub.ui.components.ui.CardGroup
import me.rerere.rikkahub.ui.components.ui.CardGroupScope
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.Switch
import me.rerere.rikkahub.ui.components.ui.SwitchSize
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingPluginsPage(vm: SettingVM = koinViewModel()) {
    val navController = LocalNavController.current
    val settings = vm.settings.collectAsStateWithLifecycle().value
    val xTools = settings.pluginSettings.xTools

    SettingsPage(
        title = "插件",
        onBack = { navController.popBackStack() }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = PageTopBarContentTopPadding, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item("plugin-summary") {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { Text("已安装插件") }
                ) {
                    item(
                        leadingContent = { Icon(ZionAppIcons.Blocks, null, tint = Color.Unspecified) },
                        headlineContent = { Text("X Tools") },
                        supportingContent = { Text("管理内置 X 应用提供给 AI 的读取、发帖、回复和互动能力。") },
                        trailingContent = {
                            Switch(
                                checked = xTools.enabled,
                                onCheckedChange = {
                                    updateXTools(vm, settings) { copy(enabled = it) }
                                },
                                size = SwitchSize.Medium
                            )
                        },
                        onClick = {
                            updateXTools(vm, settings) { copy(enabled = !enabled) }
                        }
                    )
                }
            }

            item("plugin-permissions") {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { Text("工具权限") }
                ) {
                    xToolToggleItem("读取时间线", "允许 AI 读取本地 X 信息流和回复。", xTools.allowReadTimeline, xTools.enabled) {
                        updateXTools(vm, settings) { copy(allowReadTimeline = it) }
                    }
                    xToolToggleItem("发布帖子", "允许 AI 直接发布新的 X 帖子。", xTools.allowPublishPost, xTools.enabled) {
                        updateXTools(vm, settings) { copy(allowPublishPost = it) }
                    }
                    xToolToggleItem("回复帖子", "允许 AI 对指定帖子发送回复。", xTools.allowReplyPost, xTools.enabled) {
                        updateXTools(vm, settings) { copy(allowReplyPost = it) }
                    }
                    xToolToggleItem("点赞帖子", "允许 AI 点赞或取消点赞。", xTools.allowLikePost, xTools.enabled) {
                        updateXTools(vm, settings) { copy(allowLikePost = it) }
                    }
                    xToolToggleItem("转帖帖子", "允许 AI 转帖或撤销转帖。", xTools.allowRepostPost, xTools.enabled) {
                        updateXTools(vm, settings) { copy(allowRepostPost = it) }
                    }
                    xToolToggleItem("收藏帖子", "允许 AI 收藏或取消收藏。", xTools.allowBookmarkPost, xTools.enabled) {
                        updateXTools(vm, settings) { copy(allowBookmarkPost = it) }
                    }
                }
            }
        }
    }
}

private fun updateXTools(
    vm: SettingVM,
    settings: Settings,
    transform: XToolPluginSettings.() -> XToolPluginSettings,
) {
    vm.updateSettings(
        settings.copy(
            pluginSettings = PluginSettings(
                xTools = settings.pluginSettings.xTools.transform()
            )
        )
    )
}

private fun CardGroupScope.xToolToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    item(
        onClick = if (enabled) ({ onToggle(!checked) }) else null,
        leadingContent = { Icon(ZionAppIcons.Blocks, null, tint = Color.Unspecified) },
        headlineContent = { Text(title) },
        supportingContent = { Text(description) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onToggle,
                enabled = enabled,
                size = SwitchSize.Medium
            )
        }
    )
}
