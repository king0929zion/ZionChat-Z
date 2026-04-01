package me.rerere.rikkahub.ui.pages.setting

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.McpServer
import me.rerere.rikkahub.R
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.ui.components.ui.CardGroup
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.Switch
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionAccentBlue
import me.rerere.rikkahub.ui.theme.ZionGrayLighter
import me.rerere.rikkahub.ui.theme.ZionSurface
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingPluginsPage(vm: SettingVM = koinViewModel()) {
    val navController = LocalNavController.current
    val settings by vm.settings.collectAsStateWithLifecycle()
    val xConfig = settings.pluginSettings.x

    SettingsPage(
        title = "插件",
        onBack = { navController.popBackStack() },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = PageTopBarContentTopPadding,
                bottom = 16.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item("pluginOverview") {
                PluginOverviewCard(
                    pluginCount = settings.pluginSettings.enabledPluginCount(),
                    enabledTools = xConfig.enabledToolCount(),
                )
            }

            item("pluginList") {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { Text("已安装插件") },
                ) {
                    item(
                        onClick = { navController.navigate(Screen.SettingXPlugin) },
                        leadingContent = {
                            PluginLogoTile()
                        },
                        supportingContent = {
                            Text(
                                if (xConfig.enabled) {
                                    "已启用 ${xConfig.enabledToolCount()} / 7 个工具，AI 可在对话中主动调用"
                                } else {
                                    "已停用，AI 不会读取或写入 X"
                                }
                            )
                        },
                        trailingContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = if (xConfig.enabled) "开启" else "关闭",
                                    color = if (xConfig.enabled) ZionTextPrimary else ZionTextSecondary,
                                    fontFamily = SourceSans3,
                                    fontSize = 13.sp,
                                )
                                Switch(
                                    checked = xConfig.enabled,
                                    onCheckedChange = { enabled ->
                                        vm.updateSettings(
                                            settings.copy(
                                                pluginSettings = settings.pluginSettings.copy(
                                                    x = xConfig.copy(enabled = enabled)
                                                )
                                            )
                                        )
                                    }
                                )
                            }
                        },
                        headlineContent = { Text("X") },
                    )
                }
            }
        }
    }
}

@Composable
private fun PluginOverviewCard(
    pluginCount: Int,
    enabledTools: Int,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = ZionSurface),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            .size(44.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
                            .background(ZionGrayLighter),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = HugeIcons.McpServer,
                            contentDescription = null,
                            tint = ZionTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "工具插件",
                            color = ZionTextPrimary,
                            fontFamily = SourceSans3,
                            fontSize = 18.sp,
                        )
                        Text(
                            text = "把应用能力安全地暴露给 AI 使用",
                            color = ZionTextSecondary,
                            fontFamily = SourceSans3,
                            fontSize = 14.sp,
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(999.dp))
                        .background(ZionAccentBlue.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "${pluginCount} 个已启用",
                        color = ZionAccentBlue,
                        fontFamily = SourceSans3,
                        fontSize = 13.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "当前先接入 X 插件。启用后，聊天里的 AI 可以读取时间线、查看帖子详情，并在你批准后执行发帖、回复、点赞、转发与收藏。",
                color = ZionTextSecondary,
                fontFamily = SourceSans3,
                fontSize = 15.sp,
                lineHeight = 21.sp,
            )

            Text(
                text = "已开启 $enabledTools 个 X 工具",
                color = ZionTextPrimary,
                fontFamily = SourceSans3,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
internal fun PluginLogoTile(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(9.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.zphone_x_logo),
            contentDescription = "X",
            modifier = Modifier.size(16.dp)
        )
    }
}
