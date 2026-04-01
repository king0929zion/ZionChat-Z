package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.ArrowRight01
import me.rerere.hugeicons.stroke.Book03
import me.rerere.hugeicons.stroke.Favourite
import me.rerere.hugeicons.stroke.Message02
import me.rerere.hugeicons.stroke.Refresh03
import me.rerere.hugeicons.stroke.Search01
import me.rerere.hugeicons.stroke.Share01
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.plugin.XPluginConfig
import me.rerere.rikkahub.data.plugin.XPluginTool
import me.rerere.rikkahub.ui.components.ui.HeaderActionButton
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.Switch
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionAccentBlue
import me.rerere.rikkahub.ui.theme.ZionGrayLight
import me.rerere.rikkahub.ui.theme.ZionGrayLighter
import me.rerere.rikkahub.ui.theme.ZionSectionItem
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
    var showInfo by rememberSaveable { mutableStateOf(false) }

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
        title = "X 插件",
        onBack = { navController.popBackStack() },
        trailing = {
            HeaderActionButton(
                onClick = { showInfo = true },
                icon = me.rerere.rikkahub.ui.icons.ZionAppIcons.Info,
                contentDescription = "X 插件说明"
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = PageTopBarContentTopPadding,
                bottom = 20.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item("hero") {
                XPluginHeroCard(config = xConfig)
            }

            item("pluginToggle") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = ZionSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "启用 X 插件",
                                color = ZionTextPrimary,
                                fontFamily = SourceSans3,
                                fontSize = 18.sp,
                            )
                            Text(
                                text = "关闭后，聊天中的 AI 将不会主动调用任何 X 工具。",
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
                            }
                        )
                    }
                }
            }

            item("toolList") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = ZionSurface)
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

    if (showInfo) {
        AlertDialog(
            onDismissRequest = { showInfo = false },
            title = { Text("关于 X 插件") },
            text = {
                Text(
                    text = "开启后，AI 可以在对话中主动读取 X 时间线、查看帖子详情，并在你审批后执行发帖、回复、点赞、转发与收藏。工具调用会在消息步骤里明确展示，不会静默执行。",
                    lineHeight = 22.sp,
                )
            },
            confirmButton = {
                TextButton(onClick = { showInfo = false }) {
                    Text("知道了")
                }
            }
        )
    }
}

@Composable
private fun XPluginHeroCard(
    config: XPluginConfig,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = ZionSurface)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.zphone_x_logo),
                            contentDescription = "X",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "X 社交流工作流",
                            color = ZionTextPrimary,
                            fontFamily = SourceSans3,
                            fontSize = 18.sp,
                        )
                        Text(
                            text = "为聊天提供读写 X 的完整工具面板",
                            color = ZionTextSecondary,
                            fontFamily = SourceSans3,
                            fontSize = 14.sp,
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            if (config.enabled) {
                                ZionAccentBlue.copy(alpha = 0.12f)
                            } else {
                                ZionGrayLighter
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = if (config.enabled) "运行中" else "已停用",
                        color = if (config.enabled) ZionAccentBlue else ZionTextSecondary,
                        fontFamily = SourceSans3,
                        fontSize = 13.sp,
                    )
                }
            }

            Text(
                text = "已启用 ${config.enabledToolCount()} / ${XPluginTool.entries.size} 个工具，写入型动作会触发审批。",
                color = ZionTextPrimary,
                fontFamily = SourceSans3,
                fontSize = 15.sp,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                XStatusTag(text = "可读时间线")
                XStatusTag(text = "可写帖子")
                XStatusTag(text = "带审批")
            }
        }
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (enabled) ZionAccentBlue.copy(alpha = 0.1f) else ZionSectionItem
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tool.icon(),
                    contentDescription = null,
                    tint = if (enabled) ZionAccentBlue else ZionTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = tool.title,
                    color = ZionTextPrimary,
                    fontFamily = SourceSans3,
                    fontSize = 16.sp,
                )
                Text(
                    text = tool.description,
                    color = if (pluginEnabled) ZionTextSecondary else ZionTextSecondary.copy(alpha = 0.7f),
                    fontFamily = SourceSans3,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                )
            }

            Switch(
                checked = enabled,
                onCheckedChange = onToggle
            )

            Icon(
                imageVector = HugeIcons.ArrowRight01,
                contentDescription = null,
                tint = ZionTextSecondary,
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
                    .padding(start = 50.dp, top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = when (tool) {
                        XPluginTool.ReadTimeline -> "允许 AI 读取当前内置 X 时间线，用于总结动态、发现热点与上下文理解。"
                        XPluginTool.ReadPostDetail -> "允许 AI 深入读取单条帖子及其上下文回复，适合做拆解、摘要与引用。"
                        XPluginTool.PublishPost -> "允许 AI 生成并代你发出新的 X 帖子。执行前会先请求审批。"
                        XPluginTool.ReplyPost -> "允许 AI 对指定帖子发起回复。执行前会先请求审批。"
                        XPluginTool.LikePost -> "允许 AI 对指定帖子执行点赞，适合在你的确认后完成轻量互动。"
                        XPluginTool.RepostPost -> "允许 AI 对指定帖子执行转发，执行前会先请求审批。"
                        XPluginTool.BookmarkPost -> "允许 AI 收藏指定帖子，方便稍后继续阅读与整理。"
                    },
                    color = ZionTextSecondary,
                    fontFamily = SourceSans3,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
            .clip(RoundedCornerShape(999.dp))
            .background(ZionGrayLighter)
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

private fun XPluginTool.icon() = when (this) {
    XPluginTool.ReadTimeline -> HugeIcons.Search01
    XPluginTool.ReadPostDetail -> HugeIcons.Message02
    XPluginTool.PublishPost -> HugeIcons.Share01
    XPluginTool.ReplyPost -> HugeIcons.Message02
    XPluginTool.LikePost -> HugeIcons.Favourite
    XPluginTool.RepostPost -> HugeIcons.Refresh03
    XPluginTool.BookmarkPost -> HugeIcons.Book03
}
