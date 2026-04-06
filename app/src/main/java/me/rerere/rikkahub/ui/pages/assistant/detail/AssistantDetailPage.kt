package me.rerere.rikkahub.ui.pages.assistant.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.ai.provider.Model
import me.rerere.ai.provider.ModelType
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.datastore.findModelById
import me.rerere.rikkahub.ui.components.ai.ModelSelector
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.UIAvatar
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionAccentNeutral
import me.rerere.rikkahub.ui.theme.ZionAccentNeutralBorder
import me.rerere.rikkahub.ui.theme.ZionGrayLighter
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AssistantDetailPage(id: String) {
    val vm: AssistantDetailVM = koinViewModel(
        parameters = { parametersOf(id) }
    )
    val navController = LocalNavController.current
    val assistant by vm.assistant.collectAsStateWithLifecycle()
    val providers by vm.providers.collectAsStateWithLifecycle()
    val settings by vm.settings.collectAsStateWithLifecycle()
    val inheritedModelName = settings.findModelById(settings.chatModelId)?.displayName
    val untitledBotLabel = stringResource(R.string.assistant_page_default_assistant)
    val displayName = assistant.name.ifBlank { untitledBotLabel }

    SettingsPage(
        title = displayName,
        onBack = { navController.popBackStack() }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = PageTopBarContentTopPadding,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item("profile") {
                BotConfigCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        UIAvatar(
                            name = displayName,
                            value = assistant.avatar,
                            onUpdate = { avatar ->
                                vm.update(assistant.copy(avatar = avatar))
                            },
                            modifier = Modifier.size(84.dp)
                        )
                        BotFieldLabel(stringResource(R.string.assistant_page_name))
                        OutlinedTextField(
                            value = assistant.name,
                            onValueChange = { vm.update(assistant.copy(name = it)) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(stringResource(R.string.assistant_page_default_assistant))
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(18.dp),
                            colors = botTextFieldColors(),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words
                            )
                        )
                    }
                }
            }

            item("model") {
                BotConfigCard {
                    BotFieldLabel(stringResource(R.string.setting_model_page_chat_model))
                    if (assistant.chatModelId == null && !inheritedModelName.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.assistant_page_inherited_model, inheritedModelName),
                            fontFamily = SourceSans3,
                            fontSize = 13.sp,
                            color = ZionTextSecondary,
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    ModelSelector(
                        modelId = assistant.chatModelId ?: settings.chatModelId,
                        providers = providers,
                        type = ModelType.CHAT,
                        allowClear = true,
                        modifier = Modifier.fillMaxWidth(),
                    ) { model ->
                        vm.update(
                            assistant.copy(
                                chatModelId = model.asAssistantModelId()
                            )
                        )
                    }
                }
            }

            item("prompt") {
                BotConfigCard {
                    BotFieldLabel(stringResource(R.string.setting_model_page_prompt))
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = assistant.systemPrompt,
                        onValueChange = { vm.update(assistant.copy(systemPrompt = it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 220.dp),
                        placeholder = {
                            Text(stringResource(R.string.assistant_page_prompt_placeholder))
                        },
                        minLines = 8,
                        maxLines = 12,
                        shape = RoundedCornerShape(18.dp),
                        colors = botTextFieldColors()
                    )
                }
            }
        }
    }
}

@Composable
private fun BotConfigCard(
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun BotFieldLabel(text: String) {
    Text(
        text = text,
        fontFamily = SourceSans3,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = ZionTextSecondary
    )
}

@Composable
private fun botTextFieldColors() = TextFieldDefaults.colors(
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

private fun Model.asAssistantModelId() = id.takeIf {
    modelId.isNotBlank() || displayName.isNotBlank()
}
