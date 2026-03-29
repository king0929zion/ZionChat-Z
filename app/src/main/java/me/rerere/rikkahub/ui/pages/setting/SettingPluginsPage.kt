package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.rikkahub.data.datastore.PluginSettings
import me.rerere.rikkahub.data.datastore.Settings
import me.rerere.rikkahub.data.datastore.XToolPluginSettings
import me.rerere.rikkahub.ui.components.ui.CardGroup
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
            item {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { Text("已安装插件") }
                ) {
                    item(
                        onClick = {
                            updateXTools(vm, settings) { copy(enabled = !enabled) }
                        },
                        leadingContent = { androidx.compose.material3.Icon(ZionAppIcons.Blocks, null) },
                        supportingContent = { Text("控制 AI 是否可以调用内置的 X 时间线工具") },
                        trailingContent = {
                            Switch(
                                checked = xTools.enabled,
                                onCheckedChange = {
                                    updateXTools(vm, settings) { copy(enabled = it) }
                                },
                                size = SwitchSize.Medium
                            )
                        },
                        headlineContent = { Text("X 插件") }
                    )
                }
            }

            item {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { Text("X tools 权限") }
                ) {
                    xToolToggleItem(
                        title = "读取时间线",
                        description = "允许 AI 读取最新帖子和回复，用于理解上下文",
                        checked = xTools.allowReadTimeline,
                        enabled = xTools.enabled,
                        onToggle = { checked ->
                            updateXTools(vm, settings) { copy(allowReadTimeline = checked) }
                        }
                    )
                    xToolToggleItem(
                        title = "发布帖子",
                        description = "允许 AI 在 X 页面直接发布新的帖子",
                        checked = xTools.allowPublishPost,
                        enabled = xTools.enabled,
                        onToggle = { checked ->
                            updateXTools(vm, settings) { copy(allowPublishPost = checked) }
                        }
                    )
                    xToolToggleItem(
                        title = "回复帖子",
                        description = "允许 AI 对指定帖子发送回复",
                        checked = xTools.allowReplyPost,
                        enabled = xTools.enabled,
                        onToggle = { checked ->
                            updateXTools(vm, settings) { copy(allowReplyPost = checked) }
                        }
                    )
                    xToolToggleItem(
                        title = "点赞帖子",
                        description = "允许 AI 点赞或取消点赞帖子",
                        checked = xTools.allowLikePost,
                        enabled = xTools.enabled,
                        onToggle = { checked ->
                            updateXTools(vm, settings) { copy(allowLikePost = checked) }
                        }
                    )
                    xToolToggleItem(
                        title = "转帖帖子",
                        description = "允许 AI 转帖或撤销转帖",
                        checked = xTools.allowRepostPost,
                        enabled = xTools.enabled,
                        onToggle = { checked ->
                            updateXTools(vm, settings) { copy(allowRepostPost = checked) }
                        }
                    )
                    xToolToggleItem(
                        title = "收藏帖子",
                        description = "允许 AI 收藏或取消收藏帖子",
                        checked = xTools.allowBookmarkPost,
                        enabled = xTools.enabled,
                        onToggle = { checked ->
                            updateXTools(vm, settings) { copy(allowBookmarkPost = checked) }
                        }
                    )
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

private fun me.rerere.rikkahub.ui.components.ui.CardGroupScope.xToolToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    item(
        onClick = if (enabled) {
            { onToggle(!checked) }
        } else {
            null
        },
        leadingContent = { androidx.compose.material3.Icon(ZionAppIcons.Blocks, null) },
        supportingContent = { Text(description) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onToggle,
                enabled = enabled,
                size = SwitchSize.Medium
            )
        },
        headlineContent = { Text(title) }
    )
}
