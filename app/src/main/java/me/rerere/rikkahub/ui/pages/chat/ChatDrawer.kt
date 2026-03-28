package me.rerere.rikkahub.ui.pages.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import me.rerere.rikkahub.R
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.data.datastore.Settings
import me.rerere.rikkahub.data.datastore.getBotAssistants
import me.rerere.rikkahub.data.datastore.getCurrentAssistant
import me.rerere.rikkahub.data.datastore.getPersonalizationAssistant
import me.rerere.rikkahub.data.datastore.isPersonalization
import me.rerere.rikkahub.data.model.Assistant
import me.rerere.rikkahub.data.model.Avatar
import me.rerere.rikkahub.data.model.Conversation
import me.rerere.rikkahub.ui.components.ui.UIAvatar
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.Navigator
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionGrayLighter
import me.rerere.rikkahub.ui.theme.ZionSectionItem
import me.rerere.rikkahub.ui.theme.ZionSurface
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import me.rerere.rikkahub.utils.navigateToChatPage

@Composable
fun ChatDrawerContent(
    navController: Navigator,
    vm: ChatVM,
    settings: Settings,
    current: Conversation,
) {
    val conversations = vm.conversations.collectAsLazyPagingItems()
    val conversationListState = rememberLazyListState()
    val conversationJobs by vm.conversationJobs.collectAsStateWithLifecycle(initialValue = emptyMap())
    val currentAssistant = settings.getCurrentAssistant()
    val personalizationAssistant = settings.getPersonalizationAssistant()
    val botAssistants = settings.getBotAssistants()

    var showMoveToAssistantSheet by remember { mutableStateOf(false) }
    val openPersonalizationChat = {
        vm.updateSettings(settings.copy(assistantId = personalizationAssistant.id))
        navigateToChatPage(navController)
    }
    var conversationToMove by remember { mutableStateOf<Conversation?>(null) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalDrawerSheet(
        modifier = Modifier.width(280.dp),
        drawerContainerColor = ZionSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 8.dp),
        ) {
            SidebarSearchHeader(
                onSearchClick = { navController.navigate(Screen.MessageSearch) },
                onNewChatClick = openPersonalizationChat
            )

            Column(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SidebarMenuEntry(
                    label = stringResource(R.string.chat_page_new_chat),
                    onClick = openPersonalizationChat,
                ) {
                    Icon(
                        imageVector = ZionAppIcons.NewChat,
                        contentDescription = null,
                        tint = ZionTextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                SidebarMenuEntry(
                    label = "Images",
                    onClick = { navController.navigate(Screen.ImageGen) },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_photo_picker),
                        contentDescription = null,
                        tint = ZionTextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                SidebarMenuEntry(
                    label = stringResource(R.string.stats_page_title),
                    onClick = { navController.navigate(Screen.Stats) },
                ) {
                    Icon(
                        imageVector = ZionAppIcons.Stats,
                        contentDescription = null,
                        tint = ZionTextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                SidebarMenuEntry(
                    label = "Z-Phone",
                    onClick = { navController.navigate(Screen.ZPhone) },
                ) {
                    Icon(
                        painter = painterResource(R.mipmap.ic_launcher_foreground),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            ConversationList(
                current = current,
                conversations = conversations,
                conversationJobs = conversationJobs.keys,
                listState = conversationListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                onClick = {
                    navigateToChatPage(navController, it.id)
                },
                onRegenerateTitle = {
                    vm.generateTitle(it, true)
                },
                onDelete = {
                    vm.deleteConversation(it)
                    conversations.refresh()
                    if (it.id == current.id) {
                        navigateToChatPage(navController)
                    }
                },
                onPin = {
                    vm.updatePinnedStatus(it)
                },
                onMoveToAssistant = {
                    conversationToMove = it
                    showMoveToAssistantSheet = true
                }
            )

            SidebarProfileCard(
                nickname = settings.displaySetting.userNickname.ifBlank {
                    stringResource(R.string.user_default_name)
                },
                avatar = settings.displaySetting.userAvatar,
                assistantName = if (currentAssistant.isPersonalization()) {
                    stringResource(R.string.chat_drawer_main_chat)
                } else {
                    currentAssistant.name.ifBlank {
                        stringResource(R.string.assistant_page_default_assistant)
                    }
                },
                onClick = { navController.navigate(Screen.Setting) }
            )
        }
    }

    if (showMoveToAssistantSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showMoveToAssistantSheet = false
                conversationToMove = null
            },
            sheetState = bottomSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.chat_page_move_to_assistant),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(listOf(personalizationAssistant) + botAssistants) { assistant ->
                        AssistantItem(
                            assistant = assistant,
                            isCurrentAssistant = assistant.id == conversationToMove?.assistantId,
                            onClick = {
                                conversationToMove?.let { conversation ->
                                    vm.moveConversationToAssistant(conversation, assistant.id)
                                    showMoveToAssistantSheet = false
                                    conversationToMove = null
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SidebarSearchHeader(
    onSearchClick: () -> Unit,
    onNewChatClick: () -> Unit,
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 40.dp)
                .background(ZionGrayLighter, RoundedCornerShape(20.dp))
                .pressableScale(
                    pressedScale = 0.98f,
                    onClick = onSearchClick
                )
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = ZionAppIcons.Search,
                contentDescription = null,
                tint = ZionTextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = stringResource(R.string.chat_page_search_chats),
                color = ZionTextSecondary,
                fontFamily = SourceSans3,
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(ZionGrayLighter, CircleShape)
                .pressableScale(
                    pressedScale = 0.95f,
                    onClick = onNewChatClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ZionAppIcons.NewChat,
                contentDescription = null,
                tint = ZionTextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SidebarMenuEntry(
    label: String,
    onClick: () -> Unit,
    active: Boolean = false,
    icon: @Composable () -> Unit,
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (active) ZionSectionItem else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .pressableScale(
                pressedScale = 0.98f,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(28.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Text(
            text = label,
            color = ZionTextPrimary,
            fontFamily = SourceSans3,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun SidebarProfileCard(
    nickname: String,
    avatar: Avatar,
    assistantName: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(ZionSectionItem, RoundedCornerShape(12.dp))
            .pressableScale(
                pressedScale = 0.98f,
                onClick = onClick
            )
            .padding(12.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UIAvatar(
                name = nickname,
                value = avatar,
                onUpdate = null,
                modifier = Modifier.size(36.dp),
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = nickname,
                    color = ZionTextPrimary,
                    fontFamily = SourceSans3,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = assistantName,
                    color = ZionTextSecondary,
                    fontFamily = SourceSans3,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

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
private fun AssistantItem(
    assistant: Assistant,
    isCurrentAssistant: Boolean,
    onClick: () -> Unit,
) {
    val label = if (assistant.isPersonalization()) {
        stringResource(R.string.chat_drawer_main_chat)
    } else {
        assistant.name.ifBlank { stringResource(R.string.assistant_page_default_assistant) }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = if (isCurrentAssistant) ZionSurface else ZionSectionItem,
        tonalElevation = 0.dp,
        shadowElevation = if (isCurrentAssistant) 8.dp else 0.dp,
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .pressableScale(
                    pressedScale = 0.985f,
                    onClick = onClick,
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UIAvatar(
                name = label,
                value = assistant.avatar,
                onUpdate = null,
                modifier = Modifier.size(40.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = SourceSans3,
                    fontWeight = FontWeight.SemiBold,
                    color = ZionTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (isCurrentAssistant) {
                    Text(
                        text = stringResource(R.string.assistant_page_current_assistant),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = SourceSans3,
                        color = ZionTextSecondary
                    )
                }
            }
        }
    }
}
