package me.rerere.rikkahub.ui.pages.setting

import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Wind

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.data.plugin.PluginStore
import me.rerere.rikkahub.data.plugin.StorePluginMcpSync
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionGrayLighter
import me.rerere.rikkahub.ui.theme.ZionSurface
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import org.koin.androidx.compose.koinViewModel

/**
 * 插件商店 (设计稿: Plugin Store)
 *
 * 白色圆角卡片列表: 图标 / 名称 / 分类, 右侧 Get ↔ Added 切换。
 * 内置插件 (空调红外遥控) 也上架, Get 即安装启用。
 */
@Composable
fun PluginStorePage(vm: SettingVM = koinViewModel()) {
    val navController = LocalNavController.current
    val settings by vm.settings.collectAsStateWithLifecycle()
    val pluginSettings = settings.pluginSettings

    SettingsPage(
        title = "Plugin Store",
        onBack = { navController.popBackStack() },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = PageTopBarContentTopPadding,
                start = 16.dp,
                end = 16.dp,
                bottom = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            // 空调红外遥控 (内置)
            item("store_ac_remote") {
                StoreCard(
                    iconContent = {
                        androidx.compose.material3.Icon(
                            imageVector = Lucide.Wind,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    },
                    iconDark = true,
                    name = "空调红外遥控",
                    type = "Built-in",
                    added = pluginSettings.acRemote.enabled,
                    onToggle = { install ->
                        vm.updateSettings(
                            settings.copy(
                                pluginSettings = pluginSettings.copy(
                                    acRemote = pluginSettings.acRemote.copy(enabled = install)
                                )
                            )
                        )
                    },
                )
            }

            // MCP 商店插件
            PluginStore.PLUGINS.forEach { spec ->
                val installed = pluginSettings.store.isInstalled(spec.id)
                item("store_${spec.id}") {
                    StoreCard(
                        iconContent = {
                            Text(
                                text = spec.name.firstOrNull()?.toString() ?: "P",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = SourceSans3,
                                color = Color.White,
                            )
                        },
                        iconDark = false,
                        name = spec.name,
                        type = spec.category,
                        added = installed,
                        onToggle = { install ->
                            if (install) {
                                // 安装: 默认端点 + 启用 (MCP Server 注册)
                                val config = me.rerere.rikkahub.data.plugin.StorePluginConfig(
                                    enabled = true,
                                    endpoint = spec.defaultEndpoint,
                                    apiKey = ""
                                )
                                vm.updateSettings(StorePluginMcpSync.apply(settings, spec, config))
                            } else {
                                vm.updateSettings(StorePluginMcpSync.uninstall(settings, spec))
                            }
                        },
                        onClick = { navController.navigate(Screen.PluginDetail(spec.id)) },
                    )
                }
            }
        }
    }
}

/**
 * 商店卡片 (设计稿: .plugin)
 */
@Composable
private fun StoreCard(
    iconContent: @Composable () -> Unit,
    iconDark: Boolean,
    name: String,
    type: String,
    added: Boolean,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ZionSurface)
            .pressableScale(pressedScale = 0.99f, onClick = onClick)
            .padding(horizontal = 9.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 图标
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (iconDark) ZionTextPrimary else ZionGrayLighter),
            contentAlignment = Alignment.Center
        ) {
            iconContent()
        }
        // 名称 + 分类
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = SourceSans3,
                color = ZionTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = type,
                fontSize = 12.sp,
                fontFamily = SourceSans3,
                color = ZionTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        // Get / Added
        Box(
            modifier = Modifier
                .height(26.dp)
                .width(58.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(if (added) ZionGrayLighter else ZionTextPrimary)
                .pressableScale(pressedScale = 0.93f) { onToggle(!added) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (added) "Added" else "Get",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = SourceSans3,
                color = if (added) ZionTextSecondary else Color.White,
            )
        }
    }
}
