package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.rikkahub.R
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.data.plugin.XPluginTool
import me.rerere.rikkahub.ui.components.ui.CardGroup
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.Switch
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingPluginsPage(vm: SettingVM = koinViewModel()) {
    val navController = LocalNavController.current
    val settings by vm.settings.collectAsStateWithLifecycle()
    val xConfig = settings.pluginSettings.x

    SettingsPage(
        title = stringResource(R.string.plugins_page_title),
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
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { androidx.compose.material3.Text(stringResource(R.string.setting_page_general_settings)) },
                ) {
                    item(
                        leadingContent = {
                            androidx.compose.material3.Icon(
                                painter = painterResource(R.drawable.ic_plugin_system),
                                contentDescription = null,
                                tint = ZionTextPrimary,
                            )
                        },
                        trailingContent = {
                            androidx.compose.material3.Text(
                                text = settings.pluginSettings.enabledPluginCount().toString(),
                                color = ZionTextPrimary,
                            )
                        },
                        headlineContent = {
                            androidx.compose.material3.Text(stringResource(R.string.plugins_enabled_count_label))
                        },
                    )
                    item(
                        leadingContent = {
                            androidx.compose.material3.Icon(
                                painter = painterResource(R.drawable.ic_plugin_system),
                                contentDescription = null,
                                tint = ZionTextPrimary,
                            )
                        },
                        trailingContent = {
                            androidx.compose.material3.Text(
                                text = "${xConfig.enabledToolCount()}/${XPluginTool.entries.size}",
                                color = ZionTextPrimary,
                            )
                        },
                        headlineContent = {
                            androidx.compose.material3.Text(stringResource(R.string.plugins_enabled_x_tools_label))
                        },
                    )
                }
            }

            item("pluginList") {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { androidx.compose.material3.Text(stringResource(R.string.plugins_installed_title)) },
                ) {
                    item(
                        onClick = { navController.navigate(Screen.SettingXPlugin) },
                        leadingContent = { PluginLogoTile() },
                        supportingContent = {
                            androidx.compose.material3.Text(
                                text = if (xConfig.enabled) {
                                    stringResource(
                                        R.string.plugins_x_enabled_summary,
                                        xConfig.enabledToolCount(),
                                        XPluginTool.entries.size,
                                    )
                                } else {
                                    stringResource(R.string.plugins_x_disabled_summary)
                                }
                            )
                        },
                        trailingContent = {
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
                        },
                        headlineContent = {
                            androidx.compose.material3.Text(stringResource(R.string.plugins_x_page_title))
                        },
                    )
                }
            }
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
            .clip(RoundedCornerShape(9.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.zphone_x_logo),
            contentDescription = "X",
            modifier = Modifier.size(15.dp)
        )
    }
}
