package me.rerere.rikkahub.ui.pages.setting

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.plugin.XPluginConfig
import me.rerere.rikkahub.data.plugin.XPluginTool
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.Switch
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionGrayLight
import me.rerere.rikkahub.ui.theme.ZionGrayLighter
import me.rerere.rikkahub.ui.theme.ZionSurface
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingXPluginPage(vm: SettingVM = koinViewModel()) {
    val navController = LocalNavController.current
    val settings by vm.settings.collectAsStateWithLifecycle()
    val xConfig = settings.pluginSettings.x
    var expandedTool by rememberSaveable { mutableStateOf<String?>(null) }

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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item("summary") {
                XPluginSummaryCard(config = xConfig)
            }

            item("pluginToggle") {
                SimplePluginCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.plugins_master_toggle_title),
                                color = ZionTextPrimary,
                                fontFamily = SourceSans3,
                                fontSize = 16.sp,
                            )
                            Text(
                                text = stringResource(R.string.plugins_master_toggle_desc),
                                color = ZionTextSecondary,
                                fontFamily = SourceSans3,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                            )
                        }

                        Switch(
                            checked = xConfig.enabled,
                            onCheckedChange = { enabled ->
                                updateConfig { it.copy(enabled = enabled) }
                            },
                            trackColor = ZionTextPrimary,
                            trackColorUnchecked = ZionGrayLight,
                        )
                    }
                }
            }

            item("toolsLabel") {
                Text(
                    text = stringResource(R.string.plugins_available_tools_title),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = ZionTextSecondary,
                    fontFamily = SourceSans3,
                    fontSize = 13.sp,
                )
            }

            item("toolList") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = ZionSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ZionGrayLight),
                ) {
                    Column {
                        XPluginTool.entries.forEachIndexed { index, tool ->
                            XPluginToolRow(
                                tool = tool,
                                enabled = xConfig.rawToolEnabled(tool),
                                expanded = expandedTool == tool.toolName,
                                pluginEnabled = xConfig.enabled,
                                onRowClick = {
                                    expandedTool = if (expandedTool == tool.toolName) {
                                        null
                                    } else {
                                        tool.toolName
                                    }
                                },
                                onToggle = { enabled ->
                                    updateConfig { current -> current.toggle(tool, enabled) }
                                }
                            )
                            if (index != XPluginTool.entries.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = ZionGrayLight
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun XPluginSummaryCard(
    config: XPluginConfig,
) {
    SimplePluginCard {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PluginLogoTile()
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.plugins_x_page_title),
                    color = ZionTextPrimary,
                    fontFamily = SourceSans3,
                    fontSize = 18.sp,
                )
                Text(
                    text = stringResource(R.string.plugins_x_page_desc),
                    color = ZionTextSecondary,
                    fontFamily = SourceSans3,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
            }
        }

        HorizontalDivider(color = ZionGrayLight)

        PluginInfoRow(
            label = stringResource(R.string.plugins_enabled_x_tools_label),
            value = stringResource(
                R.string.plugins_x_enabled_count_value,
                config.enabledToolCount(),
                XPluginTool.entries.size,
            ),
        )
        PluginInfoRow(
            label = stringResource(R.string.plugins_write_actions_label),
            value = stringResource(R.string.plugins_write_actions_value),
        )
    }
}

@Composable
private fun SimplePluginCard(
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = ZionSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, ZionGrayLight),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content,
        )
    }
}

@Composable
private fun PluginInfoRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = ZionTextSecondary,
            fontFamily = SourceSans3,
            fontSize = 14.sp,
        )
        Text(
            text = value,
            color = ZionTextPrimary,
            fontFamily = SourceSans3,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun XPluginToolRow(
    tool: XPluginTool,
    enabled: Boolean,
    expanded: Boolean,
    pluginEnabled: Boolean,
    onRowClick: () -> Unit,
    onToggle: (Boolean) -> Unit,
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "toolChevronRotation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
            )
            .pressableScale(pressedScale = 0.98f, onClick = onRowClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = stringResource(tool.titleRes()),
                    color = ZionTextPrimary,
                    fontFamily = SourceSans3,
                    fontSize = 16.sp,
                )
                Text(
                    text = stringResource(tool.descriptionRes()),
                    color = if (pluginEnabled) ZionTextSecondary else ZionTextSecondary.copy(alpha = 0.72f),
                    fontFamily = SourceSans3,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                )
            }

            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                trackColor = ZionTextPrimary,
                trackColorUnchecked = ZionGrayLight,
            )

            Icon(
                imageVector = ZionAppIcons.ChevronRight,
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(16.dp)
                    .graphicsLayer {
                        rotationZ = rotation
                    }
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = stringResource(tool.detailRes()),
                    color = ZionTextSecondary,
                    fontFamily = SourceSans3,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    tool.parameterTags.forEach { tag ->
                        XStatusTag(text = tag)
                    }
                }
            }
        }
    }
}

@Composable
private fun XStatusTag(
    text: String,
) {
    Box(
        modifier = Modifier
            .border(1.dp, ZionGrayLight, RoundedCornerShape(999.dp))
            .background(ZionGrayLighter, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = ZionTextSecondary,
            fontFamily = SourceSans3,
            fontSize = 12.sp,
        )
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
private fun XPluginTool.descriptionRes(): Int = when (this) {
    XPluginTool.ReadTimeline -> R.string.plugins_tool_read_timeline_desc
    XPluginTool.ReadPostDetail -> R.string.plugins_tool_read_post_detail_desc
    XPluginTool.PublishPost -> R.string.plugins_tool_publish_post_desc
    XPluginTool.ReplyPost -> R.string.plugins_tool_reply_post_desc
    XPluginTool.LikePost -> R.string.plugins_tool_like_post_desc
    XPluginTool.RepostPost -> R.string.plugins_tool_repost_post_desc
    XPluginTool.BookmarkPost -> R.string.plugins_tool_bookmark_post_desc
}

@StringRes
private fun XPluginTool.detailRes(): Int = when (this) {
    XPluginTool.ReadTimeline -> R.string.plugins_tool_read_timeline_detail
    XPluginTool.ReadPostDetail -> R.string.plugins_tool_read_post_detail_detail
    XPluginTool.PublishPost -> R.string.plugins_tool_publish_post_detail
    XPluginTool.ReplyPost -> R.string.plugins_tool_reply_post_detail
    XPluginTool.LikePost -> R.string.plugins_tool_like_post_detail
    XPluginTool.RepostPost -> R.string.plugins_tool_repost_post_detail
    XPluginTool.BookmarkPost -> R.string.plugins_tool_bookmark_post_detail
}
