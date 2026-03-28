package me.rerere.rikkahub.ui.pages.chat

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowDpSize
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dokar.sonner.ToastType
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.rerere.ai.provider.Model
import me.rerere.ai.ui.UIMessagePart
import me.rerere.rikkahub.data.datastore.Settings
import me.rerere.rikkahub.data.datastore.getCurrentAssistant
import me.rerere.rikkahub.data.datastore.getPersonalizationAssistant
import me.rerere.rikkahub.data.files.FilesManager
import me.rerere.rikkahub.data.model.Conversation
import me.rerere.rikkahub.service.ChatError
import me.rerere.rikkahub.ui.components.ai.ChatInput
import me.rerere.rikkahub.ui.components.ai.ChatModelPickerSheet
import me.rerere.rikkahub.ui.components.ui.HeaderTranslucentBackdrop
import me.rerere.rikkahub.ui.components.ui.headerActionButtonShadow
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.context.LocalToaster
import me.rerere.rikkahub.ui.context.Navigator
import me.rerere.rikkahub.ui.hooks.ChatInputState
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionBackground
import me.rerere.rikkahub.ui.theme.ZionChatBackground
import me.rerere.rikkahub.ui.theme.ZionSurface
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.utils.base64Decode
import me.rerere.rikkahub.utils.navigateToChatPage
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

@Composable
fun ChatPage(id: Uuid, text: String?, files: List<Uri>, nodeId: Uuid? = null) {
    val vm: ChatVM = koinViewModel(
        parameters = { parametersOf(id.toString()) }
    )
    val filesManager: FilesManager = koinInject()
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()

    val setting by vm.settings.collectAsStateWithLifecycle()
    val conversation by vm.conversation.collectAsStateWithLifecycle()
    val loadingJob by vm.conversationJob.collectAsStateWithLifecycle()
    val currentChatModel by vm.currentChatModel.collectAsStateWithLifecycle()
    val enableWebSearch by vm.enableWebSearch.collectAsStateWithLifecycle()
    val errors by vm.errors.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    LaunchedEffect(drawerState.isOpen) {
        if (drawerState.isOpen) {
            softwareKeyboardController?.hide()
        }
    }

    val windowAdaptiveInfo = currentWindowDpSize()
    val isBigScreen =
        windowAdaptiveInfo.width > windowAdaptiveInfo.height && windowAdaptiveInfo.width >= 1100.dp

    val inputState = vm.inputState

    LaunchedEffect(files, text) {
        if (files.isNotEmpty()) {
            val localFiles = filesManager.createChatFilesByContents(files)
            val contentTypes = files.mapNotNull { file ->
                filesManager.getFileMimeType(file)
            }
            val parts = buildList {
                localFiles.forEachIndexed { index, file ->
                    when {
                        contentTypes.getOrNull(index)?.startsWith("image/") == true -> add(UIMessagePart.Image(url = file.toString()))
                        contentTypes.getOrNull(index)?.startsWith("video/") == true -> add(UIMessagePart.Video(url = file.toString()))
                        contentTypes.getOrNull(index)?.startsWith("audio/") == true -> add(UIMessagePart.Audio(url = file.toString()))
                    }
                }
            }
            inputState.messageContent = parts
        }

        text?.base64Decode()?.let { decodedText ->
            if (decodedText.isNotEmpty()) {
                inputState.setMessageText(decodedText)
            }
        }
    }

    val chatListState = rememberLazyListState()
    LaunchedEffect(vm, nodeId, chatListState.layoutInfo.totalItemsCount) {
        if (nodeId == null && !vm.chatListInitialized && chatListState.layoutInfo.totalItemsCount > 0) {
            chatListState.scrollToItem(chatListState.layoutInfo.totalItemsCount)
            vm.chatListInitialized = true
        }
    }

    LaunchedEffect(nodeId, conversation.messageNodes.size) {
        if (nodeId != null && conversation.messageNodes.isNotEmpty() && !vm.chatListInitialized) {
            val index = conversation.messageNodes.indexOfFirst { it.id == nodeId }
            if (index >= 0) {
                chatListState.scrollToItem(index)
            }
            vm.chatListInitialized = true
        }
    }

    if (isBigScreen) {
        PermanentNavigationDrawer(
            drawerContent = {
                ChatDrawerContent(
                    navController = navController,
                    current = conversation,
                    vm = vm,
                    settings = setting
                )
            }
        ) {
            ChatPageContent(
                inputState = inputState,
                loadingJob = loadingJob,
                setting = setting,
                conversation = conversation,
                drawerState = drawerState,
                navController = navController,
                vm = vm,
                chatListState = chatListState,
                enableWebSearch = enableWebSearch,
                currentChatModel = currentChatModel,
                bigScreen = true,
                errors = errors,
                onDismissError = { vm.dismissError(it) },
                onClearAllErrors = { vm.clearAllErrors() },
            )
        }
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ChatDrawerContent(
                    navController = navController,
                    current = conversation,
                    vm = vm,
                    settings = setting
                )
            }
        ) {
            ChatPageContent(
                inputState = inputState,
                loadingJob = loadingJob,
                setting = setting,
                conversation = conversation,
                drawerState = drawerState,
                navController = navController,
                vm = vm,
                chatListState = chatListState,
                enableWebSearch = enableWebSearch,
                currentChatModel = currentChatModel,
                bigScreen = false,
                errors = errors,
                onDismissError = { vm.dismissError(it) },
                onClearAllErrors = { vm.clearAllErrors() },
            )
        }
    }
}

@Composable
private fun ChatPageContent(
    inputState: ChatInputState,
    loadingJob: Job?,
    setting: Settings,
    bigScreen: Boolean,
    conversation: Conversation,
    drawerState: DrawerState,
    navController: Navigator,
    vm: ChatVM,
    chatListState: LazyListState,
    enableWebSearch: Boolean,
    currentChatModel: Model?,
    errors: List<ChatError>,
    onDismissError: (Uuid) -> Unit,
    onClearAllErrors: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val toaster = LocalToaster.current
    var showModelPicker by remember { mutableStateOf(false) }
    val hazeState = rememberHazeState()
    val personalizationAssistant = remember(setting.assistants) { setting.getPersonalizationAssistant() }

    fun openMainChat() {
        vm.updateSettings(setting.copy(assistantId = personalizationAssistant.id))
        navigateToChatPage(navController)
    }

    TTSAutoPlay(vm = vm, setting = setting, conversation = conversation)

    Surface(
        color = ZionChatBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        AssistantBackground(setting = setting)
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                TopBar(
                    bigScreen = bigScreen,
                    drawerState = drawerState,
                    onOpenModelPicker = { showModelPicker = true },
                    onNewChat = { openMainChat() }
                )
            },
            bottomBar = {
                ChatInput(
                    state = inputState,
                    loading = loadingJob != null,
                    settings = setting,
                    conversation = conversation,
                    mcpManager = vm.mcpManager,
                    hazeState = hazeState,
                    onCancelClick = { loadingJob?.cancel() },
                    enableSearch = enableWebSearch,
                    onToggleSearch = { enabled ->
                        vm.updateSettings(setting.copy(enableWebSearch = enabled))
                    },
                    onSendClick = {
                        if (currentChatModel == null) {
                            toaster.show("请先选择模型", type = ToastType.Error)
                            return@ChatInput
                        }
                        if (inputState.isEditing()) {
                            vm.handleMessageEdit(
                                parts = inputState.getContents(),
                                messageId = inputState.editingMessage!!,
                            )
                        } else {
                            vm.handleMessageSend(inputState.getContents())
                            scope.launch {
                                chatListState.requestScrollToItem(conversation.currentMessages.size + 5)
                            }
                        }
                        inputState.clearInput()
                    },
                    onLongSendClick = {
                        if (inputState.isEditing()) {
                            vm.handleMessageEdit(
                                parts = inputState.getContents(),
                                messageId = inputState.editingMessage!!,
                            )
                        } else {
                            vm.handleMessageSend(content = inputState.getContents(), answer = false)
                            scope.launch {
                                chatListState.requestScrollToItem(conversation.currentMessages.size + 5)
                            }
                        }
                        inputState.clearInput()
                    },
                    onUpdateAssistant = { updatedAssistant ->
                        vm.updateSettings(
                            setting.copy(
                                assistants = setting.assistants.map { assistant ->
                                    if (assistant.id == updatedAssistant.id) updatedAssistant else assistant
                                }
                            )
                        )
                    },
                    onUpdateSearchService = { index ->
                        vm.updateSettings(setting.copy(searchServiceSelected = index))
                    },
                )
            },
            containerColor = Color.Transparent,
        ) { innerPadding ->
            ChatList(
                innerPadding = innerPadding,
                conversation = conversation,
                state = chatListState,
                loading = loadingJob != null,
                settings = setting,
                hazeState = hazeState,
                errors = errors,
                onDismissError = onDismissError,
                onClearAllErrors = onClearAllErrors,
                onRegenerate = { vm.regenerateAtMessage(it) },
                onEdit = {
                    inputState.editingMessage = it.id
                    inputState.setContents(it.parts)
                },
                onForkMessage = {
                    scope.launch {
                        val fork = vm.forkMessage(message = it)
                        navigateToChatPage(navController, chatId = fork.id)
                    }
                },
                onDelete = {
                    if (loadingJob != null) {
                        vm.showDeleteBlockedWhileGeneratingError()
                    } else {
                        vm.deleteMessage(it)
                    }
                },
                onUpdateMessage = { newNode ->
                    vm.updateConversation(
                        conversation.copy(
                            messageNodes = conversation.messageNodes.map { node ->
                                if (node.id == newNode.id) newNode else node
                            }
                        )
                    )
                    vm.saveConversationAsync()
                },
                onClickSuggestion = { suggestion ->
                    inputState.editingMessage = null
                    inputState.setMessageText(suggestion)
                },
                onTranslate = { message, locale -> vm.translateMessage(message, locale) },
                onClearTranslation = { message -> vm.clearTranslationField(message.id) },
                onJumpToMessage = { index ->
                    scope.launch {
                        chatListState.animateScrollToItem(index)
                    }
                },
                onToolApproval = { toolCallId, approved, reason ->
                    vm.handleToolApproval(toolCallId, approved, reason)
                },
                onToolAnswer = { toolCallId, answer ->
                    vm.handleToolAnswer(toolCallId, answer)
                },
                onToggleFavorite = { node ->
                    vm.toggleMessageFavorite(node)
                },
            )
        }
    }

    if (showModelPicker) {
        ChatModelPickerSheet(
            selectedModelId = setting.getCurrentAssistant().chatModelId ?: setting.chatModelId,
            providers = setting.providers,
            onSelect = { model ->
                vm.setChatModel(assistant = setting.getCurrentAssistant(), model = model)
                showModelPicker = false
            },
            onDismiss = { showModelPicker = false }
        )
    }
}

@Composable
private fun TopBar(
    drawerState: DrawerState,
    bigScreen: Boolean,
    onOpenModelPicker: () -> Unit,
    onNewChat: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxWidth()) {
        HeaderTranslucentBackdrop(
            modifier = Modifier.matchParentSize(),
            containerColor = ZionBackground,
            containerAlpha = 0.74f,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!bigScreen) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .headerActionButtonShadow(RoundedCornerShape(21.dp))
                            .clip(RoundedCornerShape(21.dp))
                            .background(ZionSurface, RoundedCornerShape(21.dp))
                            .pressableScale(
                                pressedScale = 0.95f,
                                onClick = { scope.launch { drawerState.open() } }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        DrawerToggleIcon()
                    }
                } else {
                    Box(modifier = Modifier.size(42.dp))
                }

                Surface(
                    modifier = Modifier
                        .headerActionButtonShadow(RoundedCornerShape(21.dp))
                        .clip(RoundedCornerShape(21.dp))
                        .pressableScale(
                            pressedScale = 0.97f,
                            onClick = onOpenModelPicker
                        ),
                    shape = RoundedCornerShape(21.dp),
                    color = ZionSurface,
                ) {
                    Text(
                        text = "ChatGPT",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = ZionTextPrimary,
                        fontFamily = SourceSans3,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .headerActionButtonShadow(RoundedCornerShape(21.dp))
                    .clip(RoundedCornerShape(21.dp))
                    .background(ZionSurface, RoundedCornerShape(21.dp))
                    .pressableScale(
                        pressedScale = 0.95f,
                        onClick = onNewChat
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ZionAppIcons.NewChat,
                    contentDescription = "New Message",
                    tint = ZionTextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun DrawerToggleIcon() {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(20.dp)
                .size(width = 20.dp, height = 2.dp)
                .background(ZionTextPrimary, RoundedCornerShape(1.dp))
        )
        Box(
            modifier = Modifier
                .width(12.dp)
                .size(width = 12.dp, height = 2.dp)
                .background(ZionTextPrimary, RoundedCornerShape(1.dp))
        )
    }
}
