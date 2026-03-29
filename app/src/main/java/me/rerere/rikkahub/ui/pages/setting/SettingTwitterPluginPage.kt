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
import me.rerere.rikkahub.ui.components.ui.CardGroup
import me.rerere.rikkahub.ui.components.ui.CardGroupScope
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.Switch
import me.rerere.rikkahub.ui.components.ui.SwitchSize
import me.rerere.rikkahub.ui.context.LocalNavController
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingTwitterPluginPage(vm: SettingVM = koinViewModel()) {
    val navController = LocalNavController.current
    val settings = vm.settings.collectAsStateWithLifecycle().value
    val twitterPlugin = settings.pluginSettings.xTools

    SettingsPage(
        title = "X Tools",
        onBack = { navController.popBackStack() }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = PageTopBarContentTopPadding, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item("plugin-overview") {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { Text(pluginText("插件概览", "Overview")) }
                ) {
                    item(
                        overlineContent = { Text(pluginText("本地内置", "Local built-in")) },
                        leadingContent = { XToolsBrandIcon() },
                        trailingContent = { PluginStatePill(enabled = twitterPlugin.enabled) },
                        headlineContent = { Text("X Tools") },
                        supportingContent = {
                            Text(
                                pluginText(
                                    "这套内置插件负责 X 信息流页面，并把受控的本地动作提供给 AI 使用。",
                                    "This built-in plugin powers the X timeline experience and exposes controlled local actions to AI."
                                )
                            )
                        }
                    )
                }
            }

            item("plugin-master-switch") {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { Text(pluginText("总开关", "Master Switch")) }
                ) {
                    item(
                        onClick = {
                            updateTwitterPlugin(vm, settings) { copy(enabled = !enabled) }
                        },
                        leadingContent = { PluginEntryIcon() },
                        headlineContent = { Text(pluginText("允许 AI 调用 X Tools", "Allow AI to use X Tools")) },
                        supportingContent = {
                            Text(
                                pluginText(
                                    "关闭后，下面所有能力都会从 AI 可调用列表里移除。",
                                    "When disabled, all tool permissions below are removed from AI access."
                                )
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = twitterPlugin.enabled,
                                onCheckedChange = {
                                    updateTwitterPlugin(vm, settings) { copy(enabled = it) }
                                },
                                size = SwitchSize.Medium
                            )
                        }
                    )
                }
            }

            item("plugin-tools") {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { Text(pluginText("AI 调用权限", "AI Permissions")) }
                ) {
                    twitterToolToggleItem(
                        title = pluginText("读取时间线", "Read timeline"),
                        description = pluginText("允许 AI 读取本地 X 信息流和回复上下文。", "Allow AI to read the local X feed and reply context."),
                        checked = twitterPlugin.allowReadTimeline,
                        enabled = twitterPlugin.enabled
                    ) {
                        updateTwitterPlugin(vm, settings) { copy(allowReadTimeline = it) }
                    }
                    twitterToolToggleItem(
                        title = pluginText("发布帖子", "Publish post"),
                        description = pluginText("允许 AI 向内置 X 时间线发布新帖子。", "Allow AI to publish new posts to the built-in X timeline."),
                        checked = twitterPlugin.allowPublishPost,
                        enabled = twitterPlugin.enabled
                    ) {
                        updateTwitterPlugin(vm, settings) { copy(allowPublishPost = it) }
                    }
                    twitterToolToggleItem(
                        title = pluginText("回复帖子", "Reply to post"),
                        description = pluginText("允许 AI 对选中的 X 帖子发送回复。", "Allow AI to reply to a selected X post."),
                        checked = twitterPlugin.allowReplyPost,
                        enabled = twitterPlugin.enabled
                    ) {
                        updateTwitterPlugin(vm, settings) { copy(allowReplyPost = it) }
                    }
                    twitterToolToggleItem(
                        title = pluginText("点赞帖子", "Like post"),
                        description = pluginText("允许 AI 对 X 帖子进行点赞或取消点赞。", "Allow AI to like or unlike X posts."),
                        checked = twitterPlugin.allowLikePost,
                        enabled = twitterPlugin.enabled
                    ) {
                        updateTwitterPlugin(vm, settings) { copy(allowLikePost = it) }
                    }
                    twitterToolToggleItem(
                        title = pluginText("转帖帖子", "Repost post"),
                        description = pluginText("允许 AI 对 X 帖子进行转帖或撤销转帖。", "Allow AI to repost or undo repost for X posts."),
                        checked = twitterPlugin.allowRepostPost,
                        enabled = twitterPlugin.enabled
                    ) {
                        updateTwitterPlugin(vm, settings) { copy(allowRepostPost = it) }
                    }
                    twitterToolToggleItem(
                        title = pluginText("收藏帖子", "Bookmark post"),
                        description = pluginText("允许 AI 收藏 X 帖子或取消收藏。", "Allow AI to bookmark or remove bookmarks from X posts."),
                        checked = twitterPlugin.allowBookmarkPost,
                        enabled = twitterPlugin.enabled
                    ) {
                        updateTwitterPlugin(vm, settings) { copy(allowBookmarkPost = it) }
                    }
                }
            }

            item("plugin-guidance") {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { Text(pluginText("建议", "Recommendation")) }
                ) {
                    item(
                        headlineContent = { Text(pluginText("建议最小授权", "Use the smallest permission set")) },
                        supportingContent = {
                            Text(
                                pluginText(
                                    "默认建议只开启读取时间线和发布帖子；点赞、转帖、收藏这类互动能力按需再开。",
                                    "A good default is enabling only Read Timeline and Publish Post, then turning on like, repost, or bookmark only when needed."
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

private fun CardGroupScope.twitterToolToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    item(
        onClick = if (enabled) ({ onToggle(!checked) }) else null,
        leadingContent = { PluginEntryIcon() },
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
