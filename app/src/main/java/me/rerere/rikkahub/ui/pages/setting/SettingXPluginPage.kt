package me.rerere.rikkahub.ui.pages.setting

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.plugin.XPluginConfig
import me.rerere.rikkahub.data.plugin.XPluginTool
import me.rerere.rikkahub.ui.components.ui.CardGroup
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.Switch
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingXPluginPage(vm: SettingVM = koinViewModel()) {
    val navController = LocalNavController.current
    val settings by vm.settings.collectAsStateWithLifecycle()
    val xConfig = settings.pluginSettings.x

    fun updateConfig(transform: (XPluginConfig) -> XPluginConfig) {
        vm.updateSettings(
            settings.copy(
                pluginSettings = settings.pluginSettings.copy(
                    x = transform(xConfig)
                )
            )
        )
    }

    SettingsPage(
        title = stringResource(R.string.plugins_x_page_title),
        onBack = { navController.popBackStack() },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = PageTopBarContentTopPadding,
                bottom = 20.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item("pluginGeneral") {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { androidx.compose.material3.Text(stringResource(R.string.setting_page_general_settings)) },
                ) {
                    item(
                        supportingContent = {
                            androidx.compose.material3.Text(stringResource(R.string.plugins_master_toggle_desc))
                        },
                        trailingContent = {
                            Switch(
                                checked = xConfig.enabled,
                                onCheckedChange = { enabled ->
                                    updateConfig { it.copy(enabled = enabled) }
                                }
                            )
                        },
                        headlineContent = {
                            androidx.compose.material3.Text(stringResource(R.string.plugins_master_toggle_title))
                        },
                    )
                    item(
                        trailingContent = {
                            androidx.compose.material3.Text(
                                text = stringResource(
                                    R.string.plugins_x_enabled_count_value,
                                    xConfig.enabledToolCount(),
                                    XPluginTool.entries.size,
                                ),
                                color = ZionTextPrimary,
                            )
                        },
                        headlineContent = {
                            androidx.compose.material3.Text(stringResource(R.string.plugins_enabled_x_tools_label))
                        },
                    )
                    item(
                        trailingContent = {
                            androidx.compose.material3.Text(
                                text = stringResource(R.string.plugins_write_actions_value),
                                color = ZionTextPrimary,
                            )
                        },
                        headlineContent = {
                            androidx.compose.material3.Text(stringResource(R.string.plugins_write_actions_label))
                        },
                    )
                }
            }

            item("pluginTools") {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { androidx.compose.material3.Text(stringResource(R.string.plugins_available_tools_title)) },
                ) {
                    XPluginTool.entries.forEach { tool ->
                        item(
                            supportingContent = {
                                androidx.compose.material3.Text(stringResource(tool.supportingRes()))
                            },
                            trailingContent = {
                                Switch(
                                    checked = xConfig.rawToolEnabled(tool),
                                    onCheckedChange = { enabled ->
                                        updateConfig { current -> current.toggle(tool, enabled) }
                                    }
                                )
                            },
                            headlineContent = {
                                androidx.compose.material3.Text(stringResource(tool.titleRes()))
                            },
                        )
                    }
                }
            }
        }
    }
}

@StringRes
private fun XPluginTool.titleRes(): Int = when (this) {
    XPluginTool.ReadTimeline -> R.string.plugins_tool_read_timeline_title
    XPluginTool.ReadPostDetail -> R.string.plugins_tool_read_post_detail_title
    XPluginTool.PublishPost -> R.string.plugins_tool_publish_post_title
    XPluginTool.ReplyPost -> R.string.plugins_tool_reply_post_title
    XPluginTool.LikePost -> R.string.plugins_tool_like_post_title
    XPluginTool.RepostPost -> R.string.plugins_tool_repost_post_title
    XPluginTool.BookmarkPost -> R.string.plugins_tool_bookmark_post_title
}

@StringRes
private fun XPluginTool.supportingRes(): Int = when (this) {
    XPluginTool.ReadTimeline -> R.string.plugins_tool_read_timeline_desc
    XPluginTool.ReadPostDetail -> R.string.plugins_tool_read_post_detail_desc
    XPluginTool.PublishPost -> R.string.plugins_tool_publish_post_desc
    XPluginTool.ReplyPost -> R.string.plugins_tool_reply_post_desc
    XPluginTool.LikePost -> R.string.plugins_tool_like_post_desc
    XPluginTool.RepostPost -> R.string.plugins_tool_repost_post_desc
    XPluginTool.BookmarkPost -> R.string.plugins_tool_bookmark_post_desc
}
