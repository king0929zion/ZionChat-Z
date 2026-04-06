package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.ai.provider.Model
import me.rerere.ai.provider.ModelType
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.datastore.findModelById
import me.rerere.rikkahub.data.plugin.TelegramPluginConfig
import me.rerere.rikkahub.ui.components.ai.ModelSelector
import me.rerere.rikkahub.ui.components.ui.CardGroup
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionAccentNeutral
import me.rerere.rikkahub.ui.theme.ZionAccentNeutralBorder
import me.rerere.rikkahub.ui.theme.ZionGrayLighter
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingTelegramPluginPage(vm: SettingVM = koinViewModel()) {
    val navController = LocalNavController.current
    val settings by vm.settings.collectAsStateWithLifecycle()
    val config = settings.pluginSettings.telegram
    val inheritedModelName = settings.findModelById(settings.chatModelId)?.displayName

    fun updateConfig(transform: (TelegramPluginConfig) -> TelegramPluginConfig) {
        vm.updateSettings(
            settings.copy(
                pluginSettings = settings.pluginSettings.copy(
                    telegram = transform(config)
                )
            )
        )
    }

    SettingsPage(
        title = stringResource(R.string.plugins_telegram_page_title),
        onBack = { navController.popBackStack() },
    ) {
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = PageTopBarContentTopPadding,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item("general") {
                CardGroup(
                    title = { Text(stringResource(R.string.setting_page_general_settings)) }
                ) {
                    item(
                        headlineContent = { Text(stringResource(R.string.plugins_telegram_enable_title)) },
                        supportingContent = { Text(stringResource(R.string.plugins_telegram_enable_desc)) },
                        trailingContent = {
                            PluginSystemSwitch(
                                checked = config.enabled,
                                onCheckedChange = { enabled ->
                                    updateConfig { it.copy(enabled = enabled) }
                                }
                            )
                        }
                    )
                    item(
                        headlineContent = { Text(stringResource(R.string.plugins_telegram_allowed_users_title)) },
                        trailingContent = {
                            Text(
                                text = config.allowedIdentityCount().toString(),
                                color = ZionTextPrimary
                            )
                        }
                    )
                    item(
                        headlineContent = { Text(stringResource(R.string.plugins_telegram_sessions_title)) },
                        trailingContent = {
                            Text(
                                text = config.sessions.size.toString(),
                                color = ZionTextPrimary
                            )
                        }
                    )
                }
            }

            item("token") {
                TelegramConfigCard {
                    TelegramFieldLabel(stringResource(R.string.plugins_telegram_token_title))
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = config.botToken,
                        onValueChange = { value ->
                            updateConfig {
                                if (it.botToken == value) {
                                    it
                                } else {
                                    it.copy(
                                        botToken = value,
                                        lastUpdateId = 0L,
                                        sessions = emptyList(),
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(stringResource(R.string.plugins_telegram_token_placeholder))
                        },
                        singleLine = true,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                        colors = telegramTextFieldColors(),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.plugins_telegram_token_desc),
                        fontFamily = SourceSans3,
                        fontSize = 13.sp,
                        color = ZionTextSecondary,
                    )
                }
            }

            item("allowedUsers") {
                TelegramConfigCard {
                    TelegramFieldLabel(stringResource(R.string.plugins_telegram_allowed_users_title))
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = config.allowedUsersRaw,
                        onValueChange = { value ->
                            updateConfig { it.copy(allowedUsersRaw = value) }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 136.dp),
                        placeholder = {
                            Text(stringResource(R.string.plugins_telegram_allowed_users_placeholder))
                        },
                        minLines = 4,
                        maxLines = 7,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                        colors = telegramTextFieldColors(),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.plugins_telegram_allowed_users_desc),
                        fontFamily = SourceSans3,
                        fontSize = 13.sp,
                        color = ZionTextSecondary,
                    )
                }
            }

            item("model") {
                TelegramConfigCard {
                    TelegramFieldLabel(stringResource(R.string.plugins_telegram_model_title))
                    if (config.modelId == null && !inheritedModelName.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(
                                R.string.plugins_telegram_model_inherited,
                                inheritedModelName
                            ),
                            fontFamily = SourceSans3,
                            fontSize = 13.sp,
                            color = ZionTextSecondary,
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    ModelSelector(
                        modelId = config.modelId ?: settings.chatModelId,
                        providers = settings.providers,
                        type = ModelType.CHAT,
                        allowClear = true,
                        modifier = Modifier.fillMaxWidth(),
                    ) { model ->
                        updateConfig {
                            it.copy(modelId = model.asTelegramModelId())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TelegramConfigCard(
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun TelegramFieldLabel(text: String) {
    Text(
        text = text,
        fontFamily = SourceSans3,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = ZionTextSecondary
    )
}

@Composable
private fun telegramTextFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = ZionGrayLighter,
    unfocusedContainerColor = ZionGrayLighter,
    focusedIndicatorColor = ZionAccentNeutral,
    unfocusedIndicatorColor = ZionAccentNeutralBorder,
    focusedTextColor = ZionTextPrimary,
    unfocusedTextColor = ZionTextPrimary,
    cursorColor = ZionTextPrimary,
    focusedPlaceholderColor = ZionTextSecondary,
    unfocusedPlaceholderColor = ZionTextSecondary
)

private fun Model.asTelegramModelId() = id.takeIf {
    modelId.isNotBlank() || displayName.isNotBlank()
}
