package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.util.fastFilter
import me.rerere.ai.provider.Model
import me.rerere.ai.provider.ModelType
import me.rerere.ai.provider.ProviderSetting
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.ai.prompts.DEFAULT_COMPRESS_PROMPT
import me.rerere.rikkahub.data.ai.prompts.DEFAULT_OCR_PROMPT
import me.rerere.rikkahub.data.ai.prompts.DEFAULT_SUGGESTION_PROMPT
import me.rerere.rikkahub.data.ai.prompts.DEFAULT_TITLE_PROMPT
import me.rerere.rikkahub.data.ai.prompts.DEFAULT_TRANSLATION_PROMPT
import me.rerere.rikkahub.data.datastore.findModelById
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.AutoAIIcon
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionAccentNeutral
import me.rerere.rikkahub.ui.theme.ZionAccentNeutralBorder
import me.rerere.rikkahub.ui.theme.ZionGrayLight
import me.rerere.rikkahub.ui.theme.ZionGrayLighter
import me.rerere.rikkahub.ui.theme.ZionSectionItem
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.Uuid

private data class ModelSectionConfig(
    val key: String,
    val title: String,
    val required: Boolean,
    val modelId: Uuid,
    val type: ModelType,
    val prompt: String? = null,
    val promptVariablesLabel: String? = null,
    val resetPrompt: String? = null,
    val onSelect: (Model?) -> Unit,
    val onPromptChange: ((String) -> Unit)? = null,
)

@Composable
fun SettingModelPage(vm: SettingVM = koinViewModel()) {
    val navController = LocalNavController.current
    val settings by vm.settings.collectAsStateWithLifecycle()
    var selectorTarget by remember { mutableStateOf<ModelSectionConfig?>(null) }
    var promptTarget by remember { mutableStateOf<ModelSectionConfig?>(null) }

    val sections = listOf(
        ModelSectionConfig(
            key = "title",
            title = stringResource(R.string.setting_model_page_title_model).uppercase(),
            required = false,
            modelId = settings.titleModelId,
            type = ModelType.CHAT,
            prompt = settings.titlePrompt,
            promptVariablesLabel = stringResource(R.string.setting_model_page_suggestion_prompt_vars),
            resetPrompt = DEFAULT_TITLE_PROMPT,
            onSelect = { model ->
                vm.updateSettings(settings.copy(titleModelId = model?.id ?: Uuid.random()))
            },
            onPromptChange = { prompt ->
                vm.updateSettings(settings.copy(titlePrompt = prompt))
            }
        ),
        ModelSectionConfig(
            key = "suggestion",
            title = stringResource(R.string.setting_model_page_suggestion_model).uppercase(),
            required = false,
            modelId = settings.suggestionModelId,
            type = ModelType.CHAT,
            prompt = settings.suggestionPrompt,
            promptVariablesLabel = stringResource(R.string.setting_model_page_suggestion_prompt_vars),
            resetPrompt = DEFAULT_SUGGESTION_PROMPT,
            onSelect = { model ->
                vm.updateSettings(settings.copy(suggestionModelId = model?.id ?: Uuid.random()))
            },
            onPromptChange = { prompt ->
                vm.updateSettings(settings.copy(suggestionPrompt = prompt))
            }
        ),
        ModelSectionConfig(
            key = "translation",
            title = stringResource(R.string.setting_model_page_translate_model).uppercase(),
            required = false,
            modelId = settings.translateModeId,
            type = ModelType.CHAT,
            prompt = settings.translatePrompt,
            promptVariablesLabel = stringResource(R.string.setting_model_page_translate_prompt_vars),
            resetPrompt = DEFAULT_TRANSLATION_PROMPT,
            onSelect = { model ->
                vm.updateSettings(settings.copy(translateModeId = model?.id ?: Uuid.random()))
            },
            onPromptChange = { prompt ->
                vm.updateSettings(settings.copy(translatePrompt = prompt))
            }
        ),
        ModelSectionConfig(
            key = "ocr",
            title = stringResource(R.string.setting_model_page_ocr_model).uppercase(),
            required = false,
            modelId = settings.ocrModelId,
            type = ModelType.CHAT,
            prompt = settings.ocrPrompt,
            promptVariablesLabel = stringResource(R.string.setting_model_page_ocr_prompt_vars),
            resetPrompt = DEFAULT_OCR_PROMPT,
            onSelect = { model ->
                vm.updateSettings(settings.copy(ocrModelId = model?.id ?: Uuid.random()))
            },
            onPromptChange = { prompt ->
                vm.updateSettings(settings.copy(ocrPrompt = prompt))
            }
        ),
        ModelSectionConfig(
            key = "compress",
            title = stringResource(R.string.setting_model_page_compress_model).uppercase(),
            required = false,
            modelId = settings.compressModelId,
            type = ModelType.CHAT,
            prompt = settings.compressPrompt,
            promptVariablesLabel = stringResource(R.string.setting_model_page_compress_prompt_vars),
            resetPrompt = DEFAULT_COMPRESS_PROMPT,
            onSelect = { model ->
                vm.updateSettings(settings.copy(compressModelId = model?.id ?: Uuid.random()))
            },
            onPromptChange = { prompt ->
                vm.updateSettings(settings.copy(compressPrompt = prompt))
            }
        )
    )

    SettingsPage(
        title = stringResource(R.string.setting_model_page_title),
        onBack = { navController.popBackStack() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = PageTopBarContentTopPadding)
                .padding(horizontal = 16.dp)
        ) {
            sections.forEachIndexed { index, section ->
                DefaultModelSection(
                    title = section.title,
                    required = section.required,
                    selectedName = settings.providers.findModelById(section.modelId)?.displayName,
                    onOpenSelector = { selectorTarget = section },
                    onOpenPrompt = if (section.onPromptChange != null) {
                        { promptTarget = section }
                    } else {
                        null
                    }
                )

                if (index != sections.lastIndex) {
                    Box(modifier = Modifier.height(12.dp))
                }
            }

            Text(
                text = stringResource(R.string.default_model_screen_note),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontFamily = SourceSans3,
                color = ZionTextSecondary,
                modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 28.dp)
            )
        }
    }

    selectorTarget?.let { section ->
        DefaultModelSelectorSheet(
            title = section.title,
            selectedModelId = section.modelId,
            required = section.required,
            type = section.type,
            providers = settings.providers,
            onSelect = { model ->
                section.onSelect(model)
                selectorTarget = null
            },
            onDismiss = { selectorTarget = null }
        )
    }

    promptTarget?.let { section ->
        ModelPromptSheet(
            title = section.title,
            prompt = section.prompt.orEmpty(),
            helper = section.promptVariablesLabel.orEmpty(),
            resetPrompt = section.resetPrompt.orEmpty(),
            onPromptChange = {
                section.onPromptChange?.invoke(it)
            },
            onDismiss = { promptTarget = null }
        )
    }
}

@Composable
private fun DefaultModelSection(
    title: String,
    required: Boolean,
    selectedName: String?,
    onOpenSelector: () -> Unit,
    onOpenPrompt: (() -> Unit)?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = SourceSans3,
            color = ZionTextSecondary,
            modifier = Modifier.weight(1f)
        )
        if (required) {
            Text(
                text = "*",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = SourceSans3,
                color = ZionTextSecondary
            )
        }
        if (onOpenPrompt != null) {
            TextButton(onClick = onOpenPrompt) {
                Text(
                    text = stringResource(R.string.setting_model_page_prompt),
                    color = ZionTextPrimary,
                    fontFamily = SourceSans3
                )
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = ZionSectionItem)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenSelector)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedName ?: stringResource(R.string.not_set),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = SourceSans3,
                color = if (selectedName.isNullOrBlank()) Color(0xFFC7C7CC) else ZionTextPrimary,
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

@Composable
private fun DefaultModelSelectorSheet(
    title: String,
    selectedModelId: Uuid,
    required: Boolean,
    type: ModelType,
    providers: List<ProviderSetting>,
    onSelect: (Model?) -> Unit,
    onDismiss: () -> Unit,
) {
    val enabledProviders = remember(providers, type) {
        providers.fastFilter { provider ->
            provider.enabled && provider.models.any { model -> model.type == type }
        }
    }
    val selectedModel = remember(providers, selectedModelId) {
        providers.findModelById(selectedModelId)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = ZionSectionItem,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .heightIn(max = 640.dp)
                .navigationBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(ZionGrayLight, RoundedCornerShape(2.dp))
                )
            }

            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = SourceSans3,
                color = ZionTextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!required) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(null) }
                                .padding(horizontal = 16.dp, vertical = 15.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.not_set),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = SourceSans3,
                                color = if (selectedModel == null) ZionTextPrimary else ZionTextSecondary,
                                modifier = Modifier.weight(1f)
                            )
                            if (selectedModel == null) {
                                Icon(
                                    imageVector = ZionAppIcons.Check,
                                    contentDescription = null,
                                    tint = ZionTextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                enabledProviders.forEach { provider ->
                    val availableModels = provider.models.fastFilter { it.type == type }
                    if (availableModels.isEmpty()) return@forEach

                    Text(
                        text = provider.name.uppercase(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = SourceSans3,
                        color = ZionTextSecondary,
                        modifier = Modifier.padding(start = 8.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        availableModels.forEachIndexed { index, model ->
                            val selected = model.id == selectedModelId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelect(model) }
                                    .padding(horizontal = 16.dp, vertical = 15.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AutoAIIcon(
                                    name = model.modelId,
                                    modifier = Modifier.size(20.dp),
                                    color = Color.Transparent
                                )
                                Text(
                                    text = model.displayName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = SourceSans3,
                                    color = ZionTextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                if (selected) {
                                    Icon(
                                        imageVector = ZionAppIcons.Check,
                                        contentDescription = null,
                                        tint = ZionTextPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            if (index != availableModels.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = ZionGrayLight.copy(alpha = 0.55f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModelPromptSheet(
    title: String,
    prompt: String,
    helper: String,
    resetPrompt: String,
    onPromptChange: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = ZionSectionItem
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(ZionGrayLight, RoundedCornerShape(2.dp))
                )
            }

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = SourceSans3,
                color = ZionTextPrimary
            )

            Text(
                text = helper,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontFamily = SourceSans3,
                color = ZionTextSecondary
            )

            OutlinedTextField(
                value = prompt,
                onValueChange = onPromptChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp),
                maxLines = 12,
                shape = RoundedCornerShape(18.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = ZionGrayLighter,
                    unfocusedContainerColor = ZionGrayLighter,
                    focusedIndicatorColor = ZionAccentNeutral,
                    unfocusedIndicatorColor = ZionAccentNeutralBorder,
                    focusedTextColor = ZionTextPrimary,
                    unfocusedTextColor = ZionTextPrimary,
                    cursorColor = ZionTextPrimary
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { onPromptChange(resetPrompt) }) {
                    Text(
                        text = stringResource(R.string.setting_model_page_reset_to_default),
                        color = ZionTextPrimary,
                        fontFamily = SourceSans3
                    )
                }
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.chat_page_save),
                        color = ZionTextPrimary,
                        fontFamily = SourceSans3
                    )
                }
            }
        }
    }
}
