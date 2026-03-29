package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.rikkahub.R
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
        title = stringResource(R.string.setting_plugins_twitter_title),
        onBack = { navController.popBackStack() }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = PageTopBarContentTopPadding, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item("twitter-plugin-overview") {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { Text(stringResource(R.string.setting_plugins_twitter_overview_title)) }
                ) {
                    item(
                        leadingContent = { TwitterPluginIcon() },
                        headlineContent = { Text(stringResource(R.string.setting_plugins_twitter_overview_headline)) },
                        supportingContent = { Text(stringResource(R.string.setting_plugins_twitter_overview_desc)) }
                    )
                }
            }

            item("twitter-plugin-master-switch") {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { Text(stringResource(R.string.setting_plugins_twitter_status_title)) }
                ) {
                    item(
                        onClick = {
                            updateTwitterPlugin(vm, settings) { copy(enabled = !enabled) }
                        },
                        leadingContent = { TwitterPluginIcon() },
                        headlineContent = { Text(stringResource(R.string.setting_plugins_twitter_enable_title)) },
                        supportingContent = { Text(stringResource(R.string.setting_plugins_twitter_enable_desc)) },
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

            item("twitter-plugin-tools") {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { Text(stringResource(R.string.setting_plugins_twitter_tools_title)) }
                ) {
                    twitterToolToggleItem(
                        title = stringResource(R.string.setting_plugins_twitter_read_title),
                        description = stringResource(R.string.setting_plugins_twitter_read_desc),
                        checked = twitterPlugin.allowReadTimeline,
                        enabled = twitterPlugin.enabled
                    ) {
                        updateTwitterPlugin(vm, settings) { copy(allowReadTimeline = it) }
                    }
                    twitterToolToggleItem(
                        title = stringResource(R.string.setting_plugins_twitter_publish_title),
                        description = stringResource(R.string.setting_plugins_twitter_publish_desc),
                        checked = twitterPlugin.allowPublishPost,
                        enabled = twitterPlugin.enabled
                    ) {
                        updateTwitterPlugin(vm, settings) { copy(allowPublishPost = it) }
                    }
                    twitterToolToggleItem(
                        title = stringResource(R.string.setting_plugins_twitter_reply_title),
                        description = stringResource(R.string.setting_plugins_twitter_reply_desc),
                        checked = twitterPlugin.allowReplyPost,
                        enabled = twitterPlugin.enabled
                    ) {
                        updateTwitterPlugin(vm, settings) { copy(allowReplyPost = it) }
                    }
                    twitterToolToggleItem(
                        title = stringResource(R.string.setting_plugins_twitter_like_title),
                        description = stringResource(R.string.setting_plugins_twitter_like_desc),
                        checked = twitterPlugin.allowLikePost,
                        enabled = twitterPlugin.enabled
                    ) {
                        updateTwitterPlugin(vm, settings) { copy(allowLikePost = it) }
                    }
                    twitterToolToggleItem(
                        title = stringResource(R.string.setting_plugins_twitter_repost_title),
                        description = stringResource(R.string.setting_plugins_twitter_repost_desc),
                        checked = twitterPlugin.allowRepostPost,
                        enabled = twitterPlugin.enabled
                    ) {
                        updateTwitterPlugin(vm, settings) { copy(allowRepostPost = it) }
                    }
                    twitterToolToggleItem(
                        title = stringResource(R.string.setting_plugins_twitter_bookmark_title),
                        description = stringResource(R.string.setting_plugins_twitter_bookmark_desc),
                        checked = twitterPlugin.allowBookmarkPost,
                        enabled = twitterPlugin.enabled
                    ) {
                        updateTwitterPlugin(vm, settings) { copy(allowBookmarkPost = it) }
                    }
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
        leadingContent = { TwitterPluginIcon() },
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
