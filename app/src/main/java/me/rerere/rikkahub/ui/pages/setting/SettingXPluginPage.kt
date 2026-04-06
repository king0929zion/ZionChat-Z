package me.rerere.rikkahub.ui.pages.setting

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.plugin.XBotActivityLevel
import me.rerere.rikkahub.data.plugin.XBotInteractionMode
import me.rerere.rikkahub.data.plugin.XPluginConfig
import me.rerere.rikkahub.data.plugin.XPluginTool
import me.rerere.rikkahub.ui.components.ui.CardGroup
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionGrayLight
import me.rerere.rikkahub.ui.theme.ZionSectionItem
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingXPluginPage(vm: SettingVM = koinViewModel()) {
    val navController = me.rerere.rikkahub.ui.context.LocalNavController.current
    val settings by vm.settings.collectAsStateWithLifecycle()
    val xConfig = settings.pluginSettings.x
    var showModeSheet by remember { mutableStateOf(false) }
    var showLevelSheet by remember { mutableStateOf(false) }

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
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = PageTopBarContentTopPadding,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item("general") {
                CardGroup(
                    title = { Text(stringResource(R.string.setting_page_general_settings)) }
                ) {
                    item(
                        headlineContent = { Text(stringResource(R.string.plugins_master_toggle_title)) },
                        supportingContent = { Text(stringResource(R.string.plugins_master_toggle_desc)) },
                        trailingContent = {
                            PluginSystemSwitch(
                                checked = xConfig.enabled,
                                onCheckedChange = { enabled ->
                                    updateConfig { it.copy(enabled = enabled) }
                                }
                            )
                        }
                    )
                    item(
                        headlineContent = { Text(stringResource(R.string.plugins_enabled_x_tools_label)) },
                        trailingContent = {
                            Text(
                                text = stringResource(
                                    R.string.plugins_x_enabled_count_value,
                                    xConfig.enabledToolCount(),
                                    XPluginTool.entries.size
                                ),
                                color = ZionTextPrimary
                            )
                        }
                    )
                    item(
                        headlineContent = { Text(stringResource(R.string.plugins_write_actions_label)) },
                        trailingContent = {
                            Text(
                                text = stringResource(R.string.plugins_write_actions_value),
                                color = ZionTextPrimary
                            )
                        }
                    )
                }
            }

            item("bots") {
                CardGroup(
                    title = { Text(stringResource(R.string.plugins_bots_section_title)) }
                ) {
                    item(
                        headlineContent = { Text(stringResource(R.string.plugins_bots_enable_title)) },
                        supportingContent = { Text(stringResource(R.string.plugins_bots_enable_desc)) },
                        trailingContent = {
                            PluginSystemSwitch(
                                checked = xConfig.botAutomationEnabled,
                                onCheckedChange = { enabled ->
                                    updateConfig { it.copy(botAutomationEnabled = enabled) }
                                }
                            )
                        }
                    )
                    item(
                        headlineContent = { Text(stringResource(R.string.plugins_bots_auto_post_title)) },
                        supportingContent = { Text(stringResource(R.string.plugins_bots_auto_post_desc)) },
                        trailingContent = {
                            PluginSystemSwitch(
                                checked = xConfig.botAutoPostEnabled,
                                onCheckedChange = { enabled ->
                                    updateConfig { it.copy(botAutoPostEnabled = enabled) }
                                }
                            )
                        }
                    )
                    item(
                        headlineContent = { Text(stringResource(R.string.plugins_bots_reply_user_title)) },
                        supportingContent = { Text(stringResource(R.string.plugins_bots_reply_user_desc)) },
                        trailingContent = {
                            PluginSystemSwitch(
                                checked = xConfig.botReplyToUserPosts,
                                onCheckedChange = { enabled ->
                                    updateConfig { it.copy(botReplyToUserPosts = enabled) }
                                }
                            )
                        }
                    )
                    item(
                        headlineContent = { Text(stringResource(R.string.plugins_bots_interact_title)) },
                        supportingContent = { Text(stringResource(R.string.plugins_bots_interact_desc)) },
                        trailingContent = {
                            PluginSystemSwitch(
                                checked = xConfig.botInteractWithOtherBots,
                                onCheckedChange = { enabled ->
                                    updateConfig { it.copy(botInteractWithOtherBots = enabled) }
                                }
                            )
                        }
                    )
                    item(
                        onClick = { showModeSheet = true },
                        headlineContent = { Text(stringResource(R.string.plugins_bots_mode_title)) },
                        supportingContent = { Text(stringResource(R.string.plugins_bots_mode_desc)) },
                        trailingContent = {
                            Text(
                                text = stringResource(xConfig.botInteractionMode.labelRes()),
                                color = ZionTextPrimary
                            )
                        }
                    )
                    item(
                        onClick = { showLevelSheet = true },
                        headlineContent = { Text(stringResource(R.string.plugins_bots_frequency_title)) },
                        supportingContent = { Text(stringResource(R.string.plugins_bots_frequency_desc)) },
                        trailingContent = {
                            Text(
                                text = stringResource(xConfig.botActivityLevel.labelRes()),
                                color = ZionTextPrimary
                            )
                        }
                    )
                }
            }

            item("tools") {
                CardGroup(
                    title = { Text(stringResource(R.string.plugins_available_tools_title)) }
                ) {
                    XPluginTool.entries.forEach { tool ->
                        item(
                            headlineContent = { Text(stringResource(tool.titleRes())) },
                            supportingContent = { Text(stringResource(tool.supportingRes())) },
                            trailingContent = {
                                PluginSystemSwitch(
                                    checked = xConfig.rawToolEnabled(tool),
                                    onCheckedChange = { enabled ->
                                        updateConfig { current -> current.toggle(tool, enabled) }
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (showModeSheet) {
        PluginOptionSheet(
            title = stringResource(R.string.plugins_bots_mode_title),
            options = XBotInteractionMode.entries.map { mode ->
                mode to stringResource(mode.labelRes())
            },
            selected = xConfig.botInteractionMode,
            onSelect = { mode ->
                updateConfig { it.copy(botInteractionMode = mode) }
                showModeSheet = false
            },
            onDismiss = { showModeSheet = false }
        )
    }

    if (showLevelSheet) {
        PluginOptionSheet(
            title = stringResource(R.string.plugins_bots_frequency_title),
            options = XBotActivityLevel.entries.map { level ->
                level to stringResource(level.labelRes())
            },
            selected = xConfig.botActivityLevel,
            onSelect = { level ->
                updateConfig { it.copy(botActivityLevel = level) }
                showLevelSheet = false
            },
            onDismiss = { showLevelSheet = false }
        )
    }
}

@Composable
private fun <T> PluginOptionSheet(
    title: String,
    options: List<Pair<T, String>>,
    selected: T,
    onSelect: (T) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = null,
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .heightIn(max = 480.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(ZionGrayLight, androidx.compose.foundation.shape.RoundedCornerShape(2.dp))
                )
            }

            Text(
                text = title,
                fontFamily = SourceSans3,
                fontWeight = FontWeight.SemiBold,
                color = ZionTextPrimary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ZionSectionItem)
            ) {
                options.forEachIndexed { index, (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(value) }
                            .padding(horizontal = 16.dp, vertical = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            color = ZionTextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        if (value == selected) {
                            androidx.compose.material3.Icon(
                                imageVector = ZionAppIcons.Check,
                                contentDescription = null,
                                tint = ZionTextPrimary
                            )
                        }
                    }
                    if (index != options.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = ZionGrayLight.copy(alpha = 0.55f)
                        )
                    }
                }
            }
        }
    }
}

@StringRes
private fun XBotInteractionMode.labelRes(): Int = when (this) {
    XBotInteractionMode.Reply -> R.string.plugins_bots_mode_reply
    XBotInteractionMode.Quote -> R.string.plugins_bots_mode_quote
    XBotInteractionMode.Mixed -> R.string.plugins_bots_mode_mixed
}

@StringRes
private fun XBotActivityLevel.labelRes(): Int = when (this) {
    XBotActivityLevel.Low -> R.string.plugins_bots_frequency_low
    XBotActivityLevel.Medium -> R.string.plugins_bots_frequency_medium
    XBotActivityLevel.High -> R.string.plugins_bots_frequency_high
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
