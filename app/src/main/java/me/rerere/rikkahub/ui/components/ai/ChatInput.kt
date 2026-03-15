package me.rerere.rikkahub.ui.components.ai

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.content.MediaType
import androidx.compose.foundation.content.ReceiveContentListener
import androidx.compose.foundation.content.consume
import androidx.compose.foundation.content.contentReceiver
import androidx.compose.foundation.content.hasMediaType
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.dokar.sonner.ToastType
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.rerere.ai.provider.Model
import me.rerere.ai.provider.ModelAbility
import me.rerere.ai.provider.ModelType
import me.rerere.ai.provider.ProviderSetting
import me.rerere.ai.ui.UIMessagePart
import me.rerere.common.android.appTempFolder
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.Book03
import me.rerere.hugeicons.stroke.FullScreen
import me.rerere.hugeicons.stroke.MusicNote03
import me.rerere.hugeicons.stroke.Package01
import me.rerere.hugeicons.stroke.Video01
import me.rerere.rikkahub.R
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.data.ai.mcp.McpManager
import me.rerere.rikkahub.data.datastore.Settings
import me.rerere.rikkahub.data.datastore.findProvider
import me.rerere.rikkahub.data.datastore.getCurrentAssistant
import me.rerere.rikkahub.data.datastore.getCurrentChatModel
import me.rerere.rikkahub.data.files.FilesManager
import me.rerere.rikkahub.data.model.Assistant
import me.rerere.rikkahub.data.model.Conversation
import me.rerere.rikkahub.ui.components.ui.InjectionSelector
import me.rerere.rikkahub.ui.components.ui.KeepScreenOn
import me.rerere.rikkahub.ui.components.ui.permission.PermissionCamera
import me.rerere.rikkahub.ui.components.ui.permission.PermissionManager
import me.rerere.rikkahub.ui.components.ui.permission.rememberPermissionState
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.context.LocalSettings
import me.rerere.rikkahub.ui.context.LocalToaster
import me.rerere.rikkahub.ui.hooks.ChatInputState
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionAccentNeutral
import me.rerere.rikkahub.ui.theme.ZionAccentNeutralBorder
import me.rerere.rikkahub.ui.theme.ZionSectionItem
import me.rerere.rikkahub.ui.theme.ZionSurface
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import org.koin.compose.koinInject
import java.io.File
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid

enum class ExpandState {
    Collapsed, Tools,
}

@Composable
fun ChatInput(
    state: ChatInputState,
    loading: Boolean,
    conversation: Conversation,
    settings: Settings,
    mcpManager: McpManager,
    hazeState: HazeState,
    enableSearch: Boolean,
    onToggleSearch: (Boolean) -> Unit,
    previewMode: Boolean,
    onTogglePreviewMode: () -> Unit,
    modifier: Modifier = Modifier,
    onUpdateChatModel: (Model) -> Unit,
    onUpdateAssistant: (Assistant) -> Unit,
    onUpdateSearchService: (Int) -> Unit,
    onCompressContext: (additionalPrompt: String, targetTokens: Int, keepRecentMessages: Int) -> Job,
    onCancelClick: () -> Unit,
    onSendClick: () -> Unit,
    onLongSendClick: () -> Unit,
) {
    val toaster = LocalToaster.current
    val assistant = settings.getCurrentAssistant()
    val hazeTintColor = ZionSurface.copy(alpha = 0.96f)
    val webSearchEnabledText = stringResource(R.string.web_search_enabled)
    val webSearchDisabledText = stringResource(R.string.web_search_disabled)

    val keyboardController = LocalSoftwareKeyboardController.current

    fun sendMessage() {
        keyboardController?.hide()
        if (loading) onCancelClick() else onSendClick()
    }

    fun sendMessageWithoutAnswer() {
        keyboardController?.hide()
        if (loading) onCancelClick() else onLongSendClick()
    }

    var expand by remember { mutableStateOf(ExpandState.Collapsed) }
    var showInjectionSheet by remember { mutableStateOf(false) }
    var showCompressDialog by remember { mutableStateOf(false) }
    fun dismissExpand() {
        expand = ExpandState.Collapsed
        showInjectionSheet = false
        showCompressDialog = false
    }

    fun expandToggle(type: ExpandState) {
        if (expand == type) {
            dismissExpand()
        } else {
            expand = type
        }
    }

    // Collapse when ime is visible
    val imeVisile = WindowInsets.isImeVisible
    LaunchedEffect(imeVisile, showInjectionSheet, showCompressDialog) {
        if (imeVisile && !showInjectionSheet && !showCompressDialog) {
            dismissExpand()
        }
    }

    Surface(
        color = Color.Transparent,
    ) {
        val hasInputDecorations = state.messageContent.isNotEmpty() || state.isEditing()
        Column(
            modifier = modifier
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(top = 6.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .animateContentSize()
                    .fillMaxWidth()
            ) {
                BackHandler(enabled = expand != ExpandState.Collapsed) {
                    dismissExpand()
                }
                if (expand == ExpandState.Tools) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .then(
                                if (settings.displaySetting.enableBlurEffect) Modifier.hazeEffect(
                                    state = hazeState,
                                    style = HazeMaterials.ultraThin(containerColor = hazeTintColor)
                                ) else Modifier
                            ),
                        shape = RoundedCornerShape(24.dp),
                        tonalElevation = 0.dp,
                        color = if (settings.displaySetting.enableBlurEffect) Color.Transparent else hazeTintColor,
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            ToolControlPanel(
                                state = state,
                                assistant = assistant,
                                settings = settings,
                                mcpManager = mcpManager,
                                enableSearch = enableSearch,
                                previewMode = previewMode,
                                onTogglePreviewMode = onTogglePreviewMode,
                                onToggleSearch = { enabled ->
                                    onToggleSearch(enabled)
                                    toaster.show(
                                        message = if (enabled) webSearchEnabledText else webSearchDisabledText,
                                        duration = 1.seconds,
                                        type = if (enabled) ToastType.Success else ToastType.Normal
                                    )
                                },
                                onUpdateSearchService = onUpdateSearchService,
                                onUpdateChatModel = {
                                    onUpdateChatModel(it)
                                    dismissExpand()
                                },
                                onUpdateAssistant = onUpdateAssistant,
                            )
                            HorizontalDivider(color = ZionSectionItem.copy(alpha = 0.8f))
                            FilesPicker(
                                conversation = conversation,
                                state = state,
                                assistant = assistant,
                                onCompressContext = onCompressContext,
                                onUpdateAssistant = onUpdateAssistant,
                                showInjectionSheet = showInjectionSheet,
                                onShowInjectionSheetChange = { showInjectionSheet = it },
                                showCompressDialog = showCompressDialog,
                                onShowCompressDialogChange = { showCompressDialog = it },
                                onDismiss = { dismissExpand() }
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionIconButton(
                    expanded = expand == ExpandState.Tools,
                    onClick = { expandToggle(ExpandState.Tools) }
                ) {
                    Icon(
                        imageVector = if (expand == ExpandState.Tools) ZionAppIcons.Close else ZionAppIcons.ChatGPTLogo,
                        contentDescription = stringResource(R.string.more_options),
                        tint = if (expand == ExpandState.Tools) ZionTextSecondary else ZionTextPrimary
                    )
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(IntrinsicSize.Min)
                        .clip(RoundedCornerShape(23.dp))
                        .then(
                            if (settings.displaySetting.enableBlurEffect) Modifier.hazeEffect(
                                state = hazeState,
                                style = HazeMaterials.ultraThin(containerColor = hazeTintColor)
                            ) else Modifier
                        ),
                    shape = RoundedCornerShape(23.dp),
                    tonalElevation = 0.dp,
                    color = if (settings.displaySetting.enableBlurEffect) Color.Transparent else hazeTintColor,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 12.dp,
                                end = 6.dp,
                                top = if (hasInputDecorations) 8.dp else 0.dp,
                                bottom = if (hasInputDecorations) 8.dp else 0.dp
                            ),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (state.messageContent.isNotEmpty()) {
                            MediaFileInputRow(state = state)
                        }

                        TextInputRow(
                            state = state,
                            loading = loading,
                            onCancelMessage = {
                                dismissExpand()
                                onCancelClick()
                            },
                            onSendMessage = {
                                dismissExpand()
                                sendMessage()
                            },
                            onLongSendMessage = {
                                dismissExpand()
                                sendMessageWithoutAnswer()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionIconButton(
    expanded: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .background(
                color = if (expanded) ZionSectionItem else ZionSurface,
                shape = CircleShape
            )
            .pressableScale(
                pressedScale = 0.95f,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun ToolControlPanel(
    state: ChatInputState,
    assistant: Assistant,
    settings: Settings,
    mcpManager: McpManager,
    enableSearch: Boolean,
    previewMode: Boolean,
    onTogglePreviewMode: () -> Unit,
    onToggleSearch: (Boolean) -> Unit,
    onUpdateSearchService: (Int) -> Unit,
    onUpdateChatModel: (Model) -> Unit,
    onUpdateAssistant: (Assistant) -> Unit,
) {
    val model = settings.getCurrentChatModel()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PreviewModeChip(
                active = previewMode,
                onClick = onTogglePreviewMode
            )
            if (assistant.quickMessages.isNotEmpty()) {
                QuickMessageButton(
                    assistant = assistant,
                    state = state,
                    modifier = Modifier
                )
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ModelSelector(
                modelId = assistant.chatModelId ?: settings.chatModelId,
                providers = settings.providers,
                onSelect = onUpdateChatModel,
                type = ModelType.CHAT,
            )
            SearchPickerButton(
                enableSearch = enableSearch,
                settings = settings,
                onToggleSearch = onToggleSearch,
                onUpdateSearchService = onUpdateSearchService,
                model = model,
            )
            if (model?.abilities?.contains(ModelAbility.REASONING) == true) {
                ReasoningButton(
                    reasoningTokens = assistant.thinkingBudget ?: 0,
                    onUpdateReasoningTokens = {
                        onUpdateAssistant(assistant.copy(thinkingBudget = it))
                    },
                )
            }
            if (settings.mcpServers.isNotEmpty()) {
                McpPickerButton(
                    assistant = assistant,
                    servers = settings.mcpServers,
                    mcpManager = mcpManager,
                    onUpdateAssistant = onUpdateAssistant,
                )
            }
        }
    }
}

@Composable
private fun PreviewModeChip(
    active: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (active) ZionAccentNeutral else ZionSectionItem,
        modifier = Modifier
            .shadow(
                elevation = if (active) 4.dp else 0.dp,
                shape = RoundedCornerShape(18.dp),
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.06f)
            )
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = ZionAppIcons.Image,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (active) Color.White else ZionTextSecondary
            )
            Text(
                text = stringResource(R.string.code_block_preview),
                color = if (active) Color.White else ZionTextPrimary,
                fontFamily = SourceSans3,
                fontSize = 14.sp,
                fontWeight = if (active) androidx.compose.ui.text.font.FontWeight.SemiBold else androidx.compose.ui.text.font.FontWeight.Medium
            )
        }
    }
}

@Composable
private fun TextInputRow(
    state: ChatInputState,
    loading: Boolean,
    onCancelMessage: () -> Unit,
    onSendMessage: () -> Unit,
    onLongSendMessage: () -> Unit,
) {
    val settings = LocalSettings.current
    val filesManager: FilesManager = koinInject()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (state.isEditing()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = ZionSectionItem,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.editing))
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = ZionAppIcons.Close,
                        contentDescription = stringResource(R.string.cancel_edit),
                        tint = ZionTextSecondary,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { state.clearInput() }
                    )
                }
            }
        }

        val receiveContentListener = remember(
            settings.displaySetting.pasteLongTextAsFile, settings.displaySetting.pasteLongTextThreshold
        ) {
            ReceiveContentListener { transferableContent ->
                when {
                    transferableContent.hasMediaType(MediaType.Image) -> {
                        transferableContent.consume { item ->
                            val uri = item.uri
                            if (uri != null) {
                                state.addImages(
                                    filesManager.createChatFilesByContents(
                                        listOf(uri)
                                    )
                                )
                            }
                            uri != null
                        }
                    }

                    settings.displaySetting.pasteLongTextAsFile && transferableContent.hasMediaType(MediaType.Text) -> {
                        transferableContent.consume { item ->
                            val text = item.text?.toString()
                            if (text != null && text.length > settings.displaySetting.pasteLongTextThreshold) {
                                val document = filesManager.createChatTextFile(text)
                                state.addFiles(listOf(document))
                                true
                            } else {
                                false
                            }
                        }
                    }

                    else -> transferableContent
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                BasicTextField(
                    value = state.textContent.text.toString(),
                    onValueChange = { newValue -> state.setMessageText(newValue) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 46.dp, max = 132.dp)
                        .padding(end = 42.dp)
                        .contentReceiver(receiveContentListener)
                        .onFocusChanged { }
                        .padding(top = 12.dp, bottom = 12.dp),
                    textStyle = TextStyle(
                        fontSize = 17.sp,
                        lineHeight = 22.sp,
                        color = ZionTextPrimary,
                        fontFamily = SourceSans3,
                    ),
                    cursorBrush = SolidColor(ZionTextPrimary),
                    keyboardOptions = KeyboardOptions(
                        imeAction = if (settings.displaySetting.sendOnEnter) ImeAction.Send else ImeAction.Default
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (settings.displaySetting.sendOnEnter && !state.isEmpty()) {
                                onSendMessage()
                            }
                        }
                    ),
                    singleLine = false,
                    maxLines = 6,
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (state.textContent.text.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.chat_input_placeholder),
                                    style = TextStyle(
                                        fontSize = 17.sp,
                                        lineHeight = 22.sp,
                                        color = ZionTextSecondary,
                                        fontFamily = SourceSans3,
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(end = 6.dp, bottom = 4.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .combinedClickable(
                            enabled = loading || !state.isEmpty(),
                            onClick = {
                                if (loading) onCancelMessage() else onSendMessage()
                            },
                            onLongClick = {
                                if (!loading) {
                                    onLongSendMessage()
                                }
                            }
                        )
                        .background(
                            color = when {
                                loading -> ZionAccentNeutral
                                state.isEmpty() -> ZionSectionItem
                                else -> ZionAccentNeutral
                            },
                            shape = CircleShape
                        )
                ) {
                    if (loading) {
                        KeepScreenOn()
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color.White)
                        )
                    } else {
                        Icon(
                            imageVector = ZionAppIcons.Send,
                            contentDescription = stringResource(R.string.send),
                            tint = if (state.isEmpty()) ZionTextSecondary else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickMessageButton(
    assistant: Assistant,
    state: ChatInputState,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = ZionSectionItem,
            modifier = Modifier.clickable {
                expanded = !expanded
            }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = ZionAppIcons.Think,
                    contentDescription = null,
                    tint = ZionTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(R.string.assistant_page_quick_messages),
                    color = ZionTextPrimary,
                    fontFamily = SourceSans3,
                    fontSize = 14.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .widthIn(min = 200.dp)
                .width(IntrinsicSize.Min)
        ) {
            assistant.quickMessages.forEach { quickMessage ->
                Surface(
                    onClick = {
                        state.appendText(quickMessage.content)
                        expanded = false
                    },
                    color = Color.Transparent,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = quickMessage.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = quickMessage.content,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaFileInputRow(
    state: ChatInputState,
) {
    val filesManager: FilesManager = koinInject()
    val managedFiles by filesManager.observe().collectAsState(initial = emptyList())
    val displayNameByRelativePath = remember(managedFiles) {
        managedFiles.associate { it.relativePath to it.displayName }
    }
    val displayNameByFileName = remember(managedFiles) {
        managedFiles.associate { it.relativePath.substringAfterLast('/') to it.displayName }
    }

    fun removePart(part: UIMessagePart, url: String) {
        state.messageContent = state.messageContent.filterNot { it == part }
        if (state.shouldDeleteFileOnRemove(part)) {
            filesManager.deleteChatFiles(listOf(url.toUri()))
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 6.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        state.messageContent.fastForEach { part ->
            when (part) {
                is UIMessagePart.Image -> {
                    AttachmentChip(
                        title = attachmentNameFromUrl(
                            url = part.url,
                            fallback = "image",
                            displayNameByRelativePath = displayNameByRelativePath,
                            displayNameByFileName = displayNameByFileName
                        ),
                        leading = {
                            Surface(
                                modifier = Modifier.size(34.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            ) {
                                AsyncImage(
                                    model = part.url,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        },
                        onRemove = { removePart(part, part.url) }
                    )
                }

                is UIMessagePart.Video -> {
                    AttachmentChip(
                        title = attachmentNameFromUrl(
                            url = part.url,
                            fallback = "video",
                            displayNameByRelativePath = displayNameByRelativePath,
                            displayNameByFileName = displayNameByFileName
                        ),
                        leading = { AttachmentLeadingIcon(icon = HugeIcons.Video01) },
                        onRemove = { removePart(part, part.url) }
                    )
                }

                is UIMessagePart.Audio -> {
                    AttachmentChip(
                        title = attachmentNameFromUrl(
                            url = part.url,
                            fallback = "audio",
                            displayNameByRelativePath = displayNameByRelativePath,
                            displayNameByFileName = displayNameByFileName
                        ),
                        leading = { AttachmentLeadingIcon(icon = HugeIcons.MusicNote03) },
                        onRemove = { removePart(part, part.url) }
                    )
                }

                is UIMessagePart.Document -> {
                    AttachmentChip(
                        title = attachmentNameFromUrl(
                            url = part.url,
                            fallback = part.fileName,
                            displayNameByRelativePath = displayNameByRelativePath,
                            displayNameByFileName = displayNameByFileName
                        ),
                        leading = { AttachmentLeadingIcon(icon = ZionAppIcons.Files) },
                        onRemove = { removePart(part, part.url) }
                    )
                }

                else -> Unit
            }
        }
    }
}

@Composable
private fun AttachmentChip(
    title: String,
    leading: @Composable () -> Unit,
    onRemove: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 1.dp,
        shadowElevation = 0.dp,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .height(44.dp)
                .padding(start = 8.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            leading()
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.widthIn(min = 40.dp, max = 180.dp),
            )
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(26.dp)
                    .clickable(onClick = onRemove),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ZionAppIcons.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AttachmentLeadingIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    Surface(
        modifier = Modifier.size(34.dp),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun attachmentNameFromUrl(
    url: String,
    fallback: String,
    displayNameByRelativePath: Map<String, String>,
    displayNameByFileName: Map<String, String>,
): String {
    val parsed = runCatching { url.toUri() }.getOrNull()
    val relativePath = parsed?.path?.substringAfter("/files/", missingDelimiterValue = "")?.takeIf { it.isNotBlank() }
    if (relativePath != null) {
        displayNameByRelativePath[relativePath]?.let { return it }
    }

    val storedFileName = parsed?.lastPathSegment?.substringAfterLast('/')?.takeIf { it.isNotBlank() }
    if (storedFileName != null) {
        displayNameByFileName[storedFileName]?.let { return it }
        return storedFileName
    }

    return fallback
}

@Composable
private fun FilesPicker(
    conversation: Conversation,
    assistant: Assistant,
    state: ChatInputState,
    onCompressContext: (additionalPrompt: String, targetTokens: Int, keepRecentMessages: Int) -> Job,
    onUpdateAssistant: (Assistant) -> Unit,
    showInjectionSheet: Boolean,
    onShowInjectionSheetChange: (Boolean) -> Unit,
    showCompressDialog: Boolean,
    onShowCompressDialogChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val settings = LocalSettings.current
    val provider = settings.getCurrentChatModel()?.findProvider(providers = settings.providers)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            TakePicButton {
                state.addImages(it)
                onDismiss()
            }

            ImagePickButton {
                state.addImages(it)
                onDismiss()
            }

            if (provider != null && provider is ProviderSetting.Google) {
                VideoPickButton {
                    state.addVideos(it)
                    onDismiss()
                }

                AudioPickButton {
                    state.addAudios(it)
                    onDismiss()
                }
            }

            FilePickButton {
                state.addFiles(it)
                onDismiss()
            }
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth()
        )

        // Prompt Injections
        if (settings.modeInjections.isNotEmpty() || settings.lorebooks.isNotEmpty()) {
            val activeCount = assistant.modeInjectionIds.size + assistant.lorebookIds.size
            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = HugeIcons.Book03,
                        contentDescription = stringResource(R.string.chat_page_prompt_injections),
                    )
                },
                headlineContent = {
                    Text(stringResource(R.string.chat_page_prompt_injections))
                },
                trailingContent = {
                    if (activeCount > 0) {
                        Text(
                            text = activeCount.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large)
                    .clickable {
                        onShowInjectionSheetChange(true)
                    },
            )
        }

        // Compress History Button
        ListItem(
            leadingContent = {
                Icon(
                    imageVector = HugeIcons.Package01,
                    contentDescription = stringResource(R.string.chat_page_compress_context),
                )
            },
            headlineContent = {
                Text(stringResource(R.string.chat_page_compress_context))
            },
            trailingContent = {
                if (conversation.messageNodes.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.chat_page_message_count, conversation.messageNodes.size),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .clickable {
                    onShowCompressDialogChange(true)
                },
        )
    }

    // Injection Bottom Sheet
    if (showInjectionSheet) {
        InjectionQuickConfigSheet(
            assistant = assistant,
            settings = settings,
            onUpdateAssistant = onUpdateAssistant,
            onDismiss = { onShowInjectionSheetChange(false) })
    }

    // Compress Context Dialog
    if (showCompressDialog) {
        CompressContextDialog(onDismiss = {
            onShowCompressDialogChange(false)
            onDismiss()
        }, onConfirm = { additionalPrompt, targetTokens, keepRecentMessages ->
            onCompressContext(additionalPrompt, targetTokens, keepRecentMessages)
        })
    }
}

@Composable
private fun FullScreenEditor(
    state: ChatInputState, onDone: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = {
            onDone()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false, decorFitsSystemWindows = false
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .imePadding(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(max = 800.dp)
                    .fillMaxHeight(0.9f),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row {
                        TextButton(
                            onClick = {
                                onDone()
                            }) {
                            Text(stringResource(R.string.chat_page_save))
                        }
                    }
                    TextField(
                        state = state.textContent,
                        modifier = Modifier
                            .padding(bottom = 2.dp)
                            .fillMaxSize(),
                        shape = RoundedCornerShape(32.dp),
                        placeholder = {
                            Text(stringResource(R.string.chat_input_placeholder))
                        },
                        colors = TextFieldDefaults.colors().copy(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun useCropLauncher(
    onCroppedImageReady: (Uri) -> Unit, onCleanup: (() -> Unit)? = null
): Pair<ActivityResultLauncher<Intent>, (Uri) -> Unit> {
    val context = LocalContext.current
    var cropOutputUri by remember { mutableStateOf<Uri?>(null) }

    val cropActivityLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            cropOutputUri?.let { croppedUri ->
                onCroppedImageReady(croppedUri)
            }
        }
        // Clean up crop output file
        cropOutputUri?.toFile()?.delete()
        cropOutputUri = null
        onCleanup?.invoke()
    }

    val launchCrop: (Uri) -> Unit = { sourceUri ->
        val outputFile = File(context.appTempFolder, "crop_output_${System.currentTimeMillis()}.jpg")
        cropOutputUri = Uri.fromFile(outputFile)

        val cropIntent = UCrop.of(sourceUri, cropOutputUri!!).withOptions(UCrop.Options().apply {
            setFreeStyleCropEnabled(true)
            setAllowedGestures(
                UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.NONE
            )
            setCompressionFormat(Bitmap.CompressFormat.PNG)
        }).withMaxResultSize(4096, 4096).getIntent(context)

        cropActivityLauncher.launch(cropIntent)
    }

    return Pair(cropActivityLauncher, launchCrop)
}

@Composable
private fun ImagePickButton(onAddImages: (List<Uri>) -> Unit = {}) {
    val context = LocalContext.current
    val settings = LocalSettings.current
    val filesManager: FilesManager = koinInject()
    var preCropTempFile by remember { mutableStateOf<File?>(null) }

    val (_, launchCrop) = useCropLauncher(
        onCroppedImageReady = { croppedUri ->
            onAddImages(filesManager.createChatFilesByContents(listOf(croppedUri)))
        },
        onCleanup = {
            preCropTempFile?.delete()
            preCropTempFile = null
        }
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { selectedUris ->
        if (selectedUris.isNotEmpty()) {
            Log.d("ImagePickButton", "Selected URIs: $selectedUris")
            // Check if we should skip crop based on settings
            if (settings.displaySetting.skipCropImage) {
                // Skip crop, directly add images
                onAddImages(filesManager.createChatFilesByContents(selectedUris))
            } else {
                // Show crop interface
                if (selectedUris.size == 1) {
                    // Single image - copy to app temp storage first, then crop
                    val tempFile = File(context.appTempFolder, "pick_temp_${System.currentTimeMillis()}.jpg")
                    runCatching {
                        context.contentResolver.openInputStream(selectedUris.first())?.use { input ->
                            tempFile.outputStream().use { output -> input.copyTo(output) }
                        }
                        preCropTempFile = tempFile
                        launchCrop(tempFile.toUri())
                    }.onFailure {
                        Log.e("ImagePickButton", "Failed to copy image to temp, falling back", it)
                        launchCrop(selectedUris.first())
                    }
                } else {
                    // Multiple images - no crop
                    onAddImages(filesManager.createChatFilesByContents(selectedUris))
                }
            }
        } else {
            Log.d("ImagePickButton", "No images selected")
        }
    }

        BigIconTextButton(icon = {
        Icon(ZionAppIcons.Image, null)
    }, text = {
        Text(stringResource(R.string.photo))
    }) {
        imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}

@Composable
fun TakePicButton(onAddImages: (List<Uri>) -> Unit = {}) {
    val cameraPermission = rememberPermissionState(PermissionCamera)

    val context = LocalContext.current
    val settings = LocalSettings.current
    val filesManager: FilesManager = koinInject()
    var cameraOutputUri by remember { mutableStateOf<Uri?>(null) }
    var cameraOutputFile by remember { mutableStateOf<File?>(null) }

    val (_, launchCrop) = useCropLauncher(onCroppedImageReady = { croppedUri ->
        onAddImages(filesManager.createChatFilesByContents(listOf(croppedUri)))
    }, onCleanup = {
        // Clean up camera temp file after cropping is done
        cameraOutputFile?.delete()
        cameraOutputFile = null
        cameraOutputUri = null
    })

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { captureSuccessful ->
        if (captureSuccessful && cameraOutputUri != null) {
            // Check if we should skip crop based on settings
            if (settings.displaySetting.skipCropImage) {
                // Skip crop, directly add image
                onAddImages(filesManager.createChatFilesByContents(listOf(cameraOutputUri!!)))
                // Clean up camera temp file
                cameraOutputFile?.delete()
                cameraOutputFile = null
                cameraOutputUri = null
            } else {
                // Show crop interface
                launchCrop(cameraOutputUri!!)
            }
        } else {
            // Clean up camera temp file if capture failed
            cameraOutputFile?.delete()
            cameraOutputFile = null
            cameraOutputUri = null
        }
    }

    // 使用权限管理器包装
    PermissionManager(
        permissionState = cameraPermission
    ) {
        BigIconTextButton(icon = {
            Icon(ZionAppIcons.Camera, null)
        }, text = {
            Text(stringResource(R.string.take_picture))
        }) {
            if (cameraPermission.allRequiredPermissionsGranted) {
                // 权限已授权，直接启动相机
                cameraOutputFile = context.cacheDir.resolve("camera_${Uuid.random()}.jpg")
                cameraOutputUri = FileProvider.getUriForFile(
                    context, "${context.packageName}.fileprovider", cameraOutputFile!!
                )
                cameraLauncher.launch(cameraOutputUri!!)
            } else {
                // 请求权限
                cameraPermission.requestPermissions()
            }
        }
    }
}

@Composable
fun VideoPickButton(onAddVideos: (List<Uri>) -> Unit = {}) {
    val context = LocalContext.current
    val filesManager: FilesManager = koinInject()
    val videoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { selectedUris ->
        if (selectedUris.isNotEmpty()) {
            onAddVideos(filesManager.createChatFilesByContents(selectedUris))
        }
    }

    BigIconTextButton(icon = {
        Icon(HugeIcons.Video01, null)
    }, text = {
        Text(stringResource(R.string.video))
    }) {
        videoPickerLauncher.launch("video/*")
    }
}

@Composable
fun AudioPickButton(onAddAudios: (List<Uri>) -> Unit = {}) {
    val context = LocalContext.current
    val filesManager: FilesManager = koinInject()
    val audioPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { selectedUris ->
        if (selectedUris.isNotEmpty()) {
            onAddAudios(filesManager.createChatFilesByContents(selectedUris))
        }
    }

    BigIconTextButton(icon = {
        Icon(HugeIcons.MusicNote03, null)
    }, text = {
        Text(stringResource(R.string.audio))
    }) {
        audioPickerLauncher.launch("audio/*")
    }
}

@Composable
fun FilePickButton(onAddFiles: (List<UIMessagePart.Document>) -> Unit = {}) {
    val context = LocalContext.current
    val toaster = LocalToaster.current
    val filesManager: FilesManager = koinInject()
    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isNotEmpty()) {
            val allowedMimeTypes = setOf(
                "text/plain",
                "text/html",
                "text/css",
                "text/javascript",
                "text/csv",
                "text/xml",
                "application/json",
                "application/javascript",
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.ms-powerpoint",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            )

            val documents = uris.mapNotNull { uri ->
                val fileName = filesManager.getFileNameFromUri(uri) ?: "file"
                val mime = filesManager.getFileMimeType(uri) ?: "text/plain"

                // Filter by MIME type or file extension
                val isAllowed =
                    allowedMimeTypes.contains(mime) || mime.startsWith("text/") || mime == "application/vnd.openxmlformats-officedocument.wordprocessingml.document" || mime == "application/pdf" || fileName.endsWith(
                        ".txt",
                        ignoreCase = true
                    ) || fileName.endsWith(".md", ignoreCase = true) || fileName.endsWith(
                        ".csv",
                        ignoreCase = true
                    ) || fileName.endsWith(".json", ignoreCase = true) || fileName.endsWith(
                        ".js",
                        ignoreCase = true
                    ) || fileName.endsWith(".html", ignoreCase = true) || fileName.endsWith(
                        ".css",
                        ignoreCase = true
                    ) || fileName.endsWith(".xml", ignoreCase = true) || fileName.endsWith(
                        ".py",
                        ignoreCase = true
                    ) || fileName.endsWith(".java", ignoreCase = true) || fileName.endsWith(
                        ".kt",
                        ignoreCase = true
                    ) || fileName.endsWith(".ts", ignoreCase = true) || fileName.endsWith(
                        ".tsx",
                        ignoreCase = true
                    ) || fileName.endsWith(".md", ignoreCase = true) || fileName.endsWith(
                        ".markdown",
                        ignoreCase = true
                    ) || fileName.endsWith(".mdx", ignoreCase = true) || fileName.endsWith(
                        ".yml",
                        ignoreCase = true
                    ) || fileName.endsWith(".yaml", ignoreCase = true)

                if (isAllowed) {
                    val localUri = filesManager.createChatFilesByContents(listOf(uri))[0]
                    UIMessagePart.Document(
                        url = localUri.toString(), fileName = fileName, mime = mime
                    )
                } else {
                    toaster.show("不支持的文件类型: $fileName", type = ToastType.Error)
                    null
                }
            }

            if (documents.isNotEmpty()) {
                onAddFiles(documents)
            }
        }
    }
    BigIconTextButton(icon = {
        Icon(ZionAppIcons.Files, null)
    }, text = {
        Text(stringResource(R.string.upload_file))
    }) {
        pickMedia.launch(arrayOf("*/*"))
    }
}


@Composable
private fun BigIconTextButton(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = interactionSource, indication = LocalIndication.current, onClick = onClick
            )
            .semantics {
                role = Role.Button
            }
            .wrapContentWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = ZionSectionItem,
            border = BorderStroke(1.dp, ZionAccentNeutralBorder),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 14.dp)
            ) {
                icon()
            }
        }
        ProvideTextStyle(
            MaterialTheme.typography.bodySmall.copy(
                color = ZionTextPrimary,
                fontFamily = SourceSans3
            )
        ) {
            text()
        }
    }
}

@Composable
private fun InjectionQuickConfigSheet(
    assistant: Assistant, settings: Settings, onUpdateAssistant: (Assistant) -> Unit, onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .padding(horizontal = 16.dp),
        ) {
            InjectionSelector(
                assistant = assistant,
                settings = settings,
                onUpdate = onUpdateAssistant,
                modifier = Modifier.weight(1f),
                onNavigateToPrompts = {
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                        navController.navigate(Screen.Prompts)
                    }
                })

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BigIconTextButtonPreview() {
    Row(
        modifier = Modifier.padding(16.dp)
    ) {
        BigIconTextButton(icon = {
            Icon(ZionAppIcons.Image, null)
        }, text = {
            Text(stringResource(R.string.photo))
        }) {}
    }
}
