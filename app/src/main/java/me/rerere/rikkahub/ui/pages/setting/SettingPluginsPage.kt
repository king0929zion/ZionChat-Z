package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.rikkahub.R
import me.rerere.rikkahub.Screen
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
fun SettingPluginsPage(vm: SettingVM = koinViewModel()) {
    val navController = LocalNavController.current
    val settings by vm.settings.collectAsStateWithLifecycle()
    val xConfig = settings.pluginSettings.x
    val totalTools = XPluginTool.entries.size

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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item("pluginOverview") {
                PluginOverviewCard(
                    pluginCount = settings.pluginSettings.enabledPluginCount(),
                    enabledTools = xConfig.enabledToolCount(),
                    totalTools = totalTools,
                )
            }

            item("pluginSectionLabel") {
                Text(
                    text = stringResource(R.string.plugins_installed_title),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = ZionTextSecondary,
                    fontFamily = SourceSans3,
                    fontSize = 13.sp,
                )
            }

            item("pluginList") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = ZionSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ZionGrayLight),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        PluginLogoTile()

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = "X",
                                color = ZionTextPrimary,
                                fontFamily = SourceSans3,
                                fontSize = 16.sp,
                            )
                            Text(
                                text = if (xConfig.enabled) {
                                    stringResource(
                                        R.string.plugins_x_enabled_summary,
                                        xConfig.enabledToolCount(),
                                        totalTools,
                                    )
                                } else {
                                    stringResource(R.string.plugins_x_disabled_summary)
                                },
                                color = ZionTextSecondary,
                                fontFamily = SourceSans3,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                            )
                        }

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
                            },
                            trackColor = ZionTextPrimary,
                            trackColorUnchecked = ZionGrayLight,
                        )
                    }

                    HorizontalDivider(color = ZionGrayLight)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pressableScale(pressedScale = 0.99f) {
                                navController.navigate(Screen.SettingXPlugin)
                            }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.plugins_manage_x_title),
                                color = ZionTextPrimary,
                                fontFamily = SourceSans3,
                                fontSize = 15.sp,
                            )
                            Text(
                                text = stringResource(R.string.plugins_manage_x_desc),
                                color = ZionTextSecondary,
                                fontFamily = SourceSans3,
                                fontSize = 13.sp,
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.plugins_open_button),
                                color = ZionTextPrimary,
                                fontFamily = SourceSans3,
                                fontSize = 14.sp,
                            )
                            Icon(
                                imageVector = ZionAppIcons.ChevronRight,
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PluginOverviewCard(
    pluginCount: Int,
    enabledTools: Int,
    totalTools: Int,
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
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                        .background(ZionGrayLighter),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = ZionAppIcons.PluginSystem,
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(20.dp),
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = stringResource(R.string.plugins_overview_title),
                        color = ZionTextPrimary,
                        fontFamily = SourceSans3,
                        fontSize = 18.sp,
                    )
                    Text(
                        text = stringResource(R.string.plugins_overview_desc),
                        color = ZionTextSecondary,
                        fontFamily = SourceSans3,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                    )
                }
            }

            HorizontalDivider(color = ZionGrayLight)

            PluginStatRow(
                label = stringResource(R.string.plugins_enabled_count_label),
                value = pluginCount.toString(),
            )
            PluginStatRow(
                label = stringResource(R.string.plugins_enabled_x_tools_label),
                value = "$enabledTools/$totalTools",
            )
        }
    }
}

@Composable
private fun PluginStatRow(
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
            fontSize = 15.sp,
        )
    }
}

@Composable
internal fun PluginLogoTile(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(30.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(R.drawable.zphone_x_logo),
            contentDescription = "X",
            modifier = Modifier.size(16.dp)
        )
    }
}
