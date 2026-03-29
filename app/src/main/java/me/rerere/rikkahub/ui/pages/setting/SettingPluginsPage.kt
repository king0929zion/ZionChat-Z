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
import me.rerere.rikkahub.R
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.ui.components.ui.CardGroup
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.context.LocalNavController

@Composable
fun SettingPluginsPage() {
    val navController = LocalNavController.current

    SettingsPage(
        title = stringResource(R.string.setting_plugins_page_title),
        onBack = { navController.popBackStack() }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = PageTopBarContentTopPadding, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item("installed-plugins") {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { Text(stringResource(R.string.setting_plugins_installed_title)) }
                ) {
                    item(
                        onClick = { navController.navigate(Screen.SettingTwitterPlugin) },
                        leadingContent = { TwitterPluginIcon() },
                        headlineContent = { Text(stringResource(R.string.setting_plugins_twitter_title)) },
                        supportingContent = { Text(stringResource(R.string.setting_plugins_twitter_desc)) }
                    )
                }
            }

            item("plugin-center-desc") {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { Text(stringResource(R.string.setting_plugins_center_title)) }
                ) {
                    item(
                        headlineContent = { Text(stringResource(R.string.setting_plugins_center_headline)) },
                        supportingContent = { Text(stringResource(R.string.setting_plugins_center_desc)) }
                    )
                }
            }
        }
    }
}
