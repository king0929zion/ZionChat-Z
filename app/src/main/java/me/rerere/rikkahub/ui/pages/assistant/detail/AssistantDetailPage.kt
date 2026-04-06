package me.rerere.rikkahub.ui.pages.assistant.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.ai.provider.Model
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.datastore.findModelById
import me.rerere.rikkahub.ui.components.ai.ChatModelPickerSheet
import me.rerere.rikkahub.ui.components.ui.HeaderActionButton
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.UIAvatar
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionSectionItem
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
    val untitledBotLabel = stringResource(R.string.assistant_page_default_assistant)
    val displayName = assistant.name.ifBlank { untitledBotLabel }
    val selectedModelName = settings.findModelById(assistant.chatModelId ?: settings.chatModelId)?.displayName
    var showModelSelector by remember { mutableStateOf(false) }

    SettingsPage(
        title = displayName,
        onBack = { navController.popBackStack() },
        trailing = {
            HeaderActionButton(
                onClick = { navController.popBackStack() },
                icon = ZionAppIcons.Check,
                contentDescription = stringResource(R.string.assistant_page_save)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = PageTopBarContentTopPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UIAvatar(
                    name = displayName,
                    value = assistant.avatar,
                    onUpdate = { avatar ->
                        vm.update(assistant.copy(avatar = avatar))
                    },
                    modifier = Modifier.size(72.dp)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BotFieldLabel(stringResource(R.string.assistant_page_name))
                    BotInputCard {
                        BasicTextField(
                            value = assistant.name,
                            onValueChange = { vm.update(assistant.copy(name = it)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 15.sp,
                                color = ZionTextPrimary
                            ),
                            cursorBrush = SolidColor(ZionTextPrimary),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (assistant.name.isBlank()) {
                                        Text(
                                            text = untitledBotLabel,
                                            fontSize = 15.sp,
                                            color = Color(0xFFC7C7CC)
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BotFieldLabel(stringResource(R.string.setting_model_page_chat_model))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ZionSectionItem)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { showModelSelector = true }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isEmpty = selectedModelName.isNullOrBlank()
                        Text(
                            text = if (isEmpty) stringResource(R.string.not_set) else selectedModelName.orEmpty(),
                            fontSize = 16.sp,
                            color = if (isEmpty) Color(0xFFC7C7CC) else ZionTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = ZionAppIcons.ChevronRight,
                            contentDescription = null,
                            tint = ZionTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BotFieldLabel(stringResource(R.string.setting_model_page_prompt))
                BotInputCard {
                    BasicTextField(
                        value = assistant.systemPrompt,
                        onValueChange = { vm.update(assistant.copy(systemPrompt = it)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 6,
                        maxLines = 10,
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            color = ZionTextPrimary,
                            lineHeight = 22.sp
                        ),
                        cursorBrush = SolidColor(ZionTextPrimary),
                        decorationBox = { innerTextField ->
                            Box {
                                if (assistant.systemPrompt.isBlank()) {
                                    Text(
                                        text = stringResource(R.string.assistant_page_prompt_placeholder),
                                        fontSize = 15.sp,
                                        lineHeight = 22.sp,
                                        color = Color(0xFFC7C7CC)
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showModelSelector) {
        ChatModelPickerSheet(
            selectedModelId = assistant.chatModelId ?: settings.chatModelId,
            providers = providers,
            allowClear = true,
            onSelect = { model ->
                vm.update(
                    assistant.copy(
                        chatModelId = model.asAssistantModelId()
                    )
                )
            },
            onDismiss = { showModelSelector = false }
        )
    }
}

@Composable
private fun BotFieldLabel(text: String) {
    Text(
        text = text,
        fontFamily = SourceSans3,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = ZionTextSecondary,
        modifier = Modifier.padding(start = 8.dp)
    )
}

@Composable
private fun BotInputCard(
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ZionSectionItem)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            content()
        }
    }
}

private fun Model.asAssistantModelId() = id.takeIf {
    modelId.isNotBlank() || displayName.isNotBlank()
}
