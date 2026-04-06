package me.rerere.rikkahub.ui.components.ai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFilter
import me.rerere.ai.provider.Modality
import me.rerere.ai.provider.Model
import me.rerere.ai.provider.ModelAbility
import me.rerere.ai.provider.ModelType
import me.rerere.ai.provider.ProviderSetting
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.ArrowRight01
import me.rerere.hugeicons.stroke.Image03
import me.rerere.hugeicons.stroke.Text
import me.rerere.hugeicons.stroke.Tools
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.datastore.findModelById
import me.rerere.rikkahub.ui.components.ui.AutoAIIcon
import me.rerere.rikkahub.ui.components.ui.Tag
import me.rerere.rikkahub.ui.components.ui.TagType
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionGrayLight
import me.rerere.rikkahub.ui.theme.ZionSectionItem
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import me.rerere.rikkahub.utils.toDp
import kotlin.uuid.Uuid

@Composable
fun ModelSelector(
    modelId: Uuid?,
    providers: List<ProviderSetting>,
    type: ModelType,
    modifier: Modifier = Modifier,
    onlyIcon: Boolean = false,
    allowClear: Boolean = false,
    onSelect: (Model) -> Unit
) {
    var popup by remember { mutableStateOf(false) }
    val selectedModel = remember(modelId, providers) {
        modelId?.let(providers::findModelById)
    }

    if (onlyIcon) {
        Surface(
            modifier = modifier.size(40.dp),
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(1.dp, ZionGrayLight),
            onClick = { popup = true }
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (selectedModel != null) {
                    AutoAIIcon(
                        name = selectedModel.modelId,
                        modifier = Modifier.size(20.dp),
                        color = Color.Transparent
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_model),
                        contentDescription = stringResource(R.string.model_list_select_model),
                        tint = ZionTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { popup = true })
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedModel?.displayName ?: stringResource(R.string.not_set),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (selectedModel == null) Color(0xFFC7C7CC) else ZionTextPrimary,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = SourceSans3,
                        fontWeight = FontWeight.Medium
                    ),
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

    if (popup) {
        ModelPickerSheet(
            title = modelTypeTitle(type),
            selectedModelId = modelId,
            providers = providers,
            modelType = type,
            allowClear = allowClear,
            onSelect = {
                onSelect(it ?: Model())
                popup = false
            },
            onDismiss = { popup = false }
        )
    }
}

@Composable
fun ChatModelPickerSheet(
    selectedModelId: Uuid?,
    providers: List<ProviderSetting>,
    onSelect: (Model) -> Unit,
    onDismiss: () -> Unit,
) {
    ModelPickerSheet(
        title = stringResource(R.string.setting_model_page_chat_model).uppercase(),
        selectedModelId = selectedModelId,
        providers = providers,
        modelType = ModelType.CHAT,
        allowClear = false,
        onSelect = { model ->
            model?.let(onSelect)
            onDismiss()
        },
        onDismiss = onDismiss,
    )
}

@Composable
private fun ModelPickerSheet(
    title: String,
    selectedModelId: Uuid?,
    providers: List<ProviderSetting>,
    modelType: ModelType,
    allowClear: Boolean,
    onSelect: (Model?) -> Unit,
    onDismiss: () -> Unit,
) {
    val enabledProviders = remember(providers, modelType) {
        providers.fastFilter { provider ->
            provider.enabled && provider.models.any { model -> model.type == modelType }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = null,
        containerColor = Color.White,
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
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = SourceSans3,
                    fontWeight = FontWeight.SemiBold
                ),
                color = ZionTextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (allowClear) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = ZionSectionItem)
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
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = SourceSans3,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = if (selectedModelId == null) ZionTextPrimary else ZionTextSecondary,
                                modifier = Modifier.weight(1f)
                            )
                            if (selectedModelId == null) {
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
                    val models = provider.models.fastFilter { it.type == modelType }
                    if (models.isEmpty()) return@forEach

                    Text(
                        text = provider.name.uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = SourceSans3,
                            fontWeight = FontWeight.Medium
                        ),
                        color = ZionTextSecondary,
                        modifier = Modifier.padding(start = 8.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = ZionSectionItem)
                    ) {
                        models.forEachIndexed { index, model ->
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
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = SourceSans3,
                                        fontWeight = FontWeight.Medium
                                    ),
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
                            if (index != models.lastIndex) {
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
private fun modelTypeTitle(type: ModelType): String = stringResource(
    when (type) {
        ModelType.CHAT -> R.string.setting_model_page_chat_model
        ModelType.IMAGE -> R.string.setting_provider_page_image_model
        ModelType.EMBEDDING -> R.string.setting_provider_page_embedding_model
    }
).uppercase()

@Composable
fun ModelTypeTag(model: Model) {
    Tag(type = TagType.INFO) {
        Text(
            text = stringResource(
                when (model.type) {
                    ModelType.CHAT -> R.string.setting_provider_page_chat_model
                    ModelType.EMBEDDING -> R.string.setting_provider_page_embedding_model
                    ModelType.IMAGE -> R.string.setting_provider_page_image_model
                }
            )
        )
    }
}

@Composable
fun ModelModalityTag(model: Model) {
    Tag(type = TagType.SUCCESS) {
        model.inputModalities.forEach { modality ->
            Icon(
                imageVector = when (modality) {
                    Modality.TEXT -> HugeIcons.Text
                    Modality.IMAGE -> HugeIcons.Image03
                },
                contentDescription = null,
                modifier = Modifier
                    .size(LocalTextStyle.current.lineHeight.toDp())
                    .padding(1.dp)
            )
        }
        Icon(
            imageVector = HugeIcons.ArrowRight01,
            contentDescription = null,
            modifier = Modifier.size(LocalTextStyle.current.lineHeight.toDp())
        )
        model.outputModalities.forEach { modality ->
            Icon(
                imageVector = when (modality) {
                    Modality.TEXT -> HugeIcons.Text
                    Modality.IMAGE -> HugeIcons.Image03
                },
                contentDescription = null,
                modifier = Modifier
                    .size(LocalTextStyle.current.lineHeight.toDp())
                    .padding(1.dp)
            )
        }
    }
}

@Composable
fun ModelAbilityTag(model: Model) {
    model.abilities.forEach { ability ->
        when (ability) {
            ModelAbility.TOOL -> {
                Tag(type = TagType.WARNING) {
                    Icon(
                        imageVector = HugeIcons.Tools,
                        contentDescription = null,
                        modifier = Modifier.size(LocalTextStyle.current.lineHeight.toDp())
                    )
                }
            }

            ModelAbility.REASONING -> {
                Tag(type = TagType.INFO) {
                    Icon(
                        painter = painterResource(R.drawable.deepthink),
                        contentDescription = null,
                        modifier = Modifier.size(LocalTextStyle.current.lineHeight.toDp())
                    )
                }
            }
        }
    }
}
