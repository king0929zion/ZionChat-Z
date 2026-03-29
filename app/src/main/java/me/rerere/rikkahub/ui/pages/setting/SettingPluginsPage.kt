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
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.ui.components.ui.CardGroup
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingPluginsPage(vm: SettingVM = koinViewModel()) {
    val navController = LocalNavController.current
    val settings = vm.settings.collectAsStateWithLifecycle().value
    val xTools = settings.pluginSettings.xTools

    SettingsPage(
        title = pluginText("插件", "Plugins"),
        onBack = { navController.popBackStack() }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = PageTopBarContentTopPadding, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item("plugin-center") {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { Text(pluginText("插件中心", "Plugin Center")) }
                ) {
                    item(
                        onClick = { navController.navigate(Screen.SettingTwitterPlugin) },
                        overlineContent = { Text(pluginText("内置插件", "Built-in")) },
                        leadingContent = { PluginEntryIcon() },
                        trailingContent = { PluginRowTrailing(enabled = xTools.enabled) },
                        headlineContent = { Text("X Tools", color = ZionTextPrimary) },
                        supportingContent = {
                            Text(
                                pluginText(
                                    "管理内置 X 时间线页面，以及 AI 是否可以调用相关本地能力。",
                                    "Manage the built-in X timeline screen and control which local actions AI may call."
                                ),
                                color = ZionTextSecondary
                            )
                        }
                    )
                }
            }

            item("plugin-capabilities") {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { Text(pluginText("能力说明", "Capabilities")) }
                ) {
                    item(
                        leadingContent = { XToolsBrandIcon() },
                        headlineContent = { Text(pluginText("多个 X tool 独立授权", "Grant X tools individually")) },
                        supportingContent = {
                            Text(
                                pluginText(
                                    "你可以分别控制读取时间线、发帖、回复、点赞、转帖和收藏是否对 AI 开放。",
                                    "You can independently allow AI to read the timeline, publish, reply, like, repost, or bookmark."
                                )
                            )
                        }
                    )
                }
            }

            item("plugin-safety") {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { Text(pluginText("安全建议", "Safety")) }
                ) {
                    item(
                        headlineContent = { Text(pluginText("只开启你需要交给 AI 的动作", "Enable only the actions you need")) },
                        supportingContent = {
                            Text(
                                pluginText(
                                    "如果只是想让 AI 看时间线，先只开启“读取时间线”；只有需要代发或互动时，再单独开启对应动作。",
                                    "If AI only needs to inspect the feed, enable Read Timeline first. Turn on posting or interaction actions only when you actually need them."
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}
