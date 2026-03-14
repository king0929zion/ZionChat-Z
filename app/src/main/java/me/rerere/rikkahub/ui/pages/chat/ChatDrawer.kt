package me.rerere.rikkahub.ui.pages.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import me.rerere.rikkahub.R
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.data.datastore.Settings
import me.rerere.rikkahub.data.datastore.getCurrentAssistant
import me.rerere.rikkahub.data.model.Assistant
import me.rerere.rikkahub.data.model.Avatar
import me.rerere.rikkahub.data.model.Conversation
import me.rerere.rikkahub.ui.components.ui.UIAvatar
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.Navigator
import me.rerere.rikkahub.ui.hooks.EditStateContent
import me.rerere.rikkahub.ui.hooks.useEditState
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionAccentBlue
import me.rerere.rikkahub.ui.theme.ZionBackground
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
    val scope = rememberCoroutineScope()
    val conversations = vm.conversations.collectAsLazyPagingItems()
    val conversationListState = rememberLazyListState()
    val conversationJobs by vm.conversationJobs.collectAsStateWithLifecycle(initialValue = emptyMap())
    val currentAssistant = settings.getCurrentAssistant()

    val nicknameEditState = useEditState<String> { newNickname ->
        vm.updateSettings(
            settings.copy(
                displaySetting = settings.displaySetting.copy(
                    userNickname = newNickname
                )
            )
        )
    }

    var showMoveToAssistantSheet by remember { mutableStateOf(false) }
    var conversationToMove by remember { mutableStateOf<Conversation?>(null) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showToolsMenu by remember { mutableStateOf(false) }

    ModalDrawerSheet(
        modifier = Modifier.width(304.dp),
        drawerContainerColor = ZionBackground,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SidebarSearchHeader(
                onSearchClick = { navController.navigate(Screen.MessageSearch) },
                onNewChatClick = { navigateToChatPage(navController) }
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SidebarMenuEntry(
                    icon = ZionAppIcons.Assistant,
                    label = stringResource(R.string.assistant_page_title),
                    onClick = { navController.navigate(Screen.Assistant) }
                )
                SidebarMenuEntry(
                    icon = ZionAppIcons.History,
                    label = stringResource(R.string.chat_page_history),
                    onClick = { navController.navigate(Screen.History) }
                )
                SidebarMenuEntry(
                    icon = ZionAppIcons.Favorite,
                    label = stringResource(R.string.favorite_page_title),
                    onClick = { navController.navigate(Screen.Favorite) }
                )
                SidebarMenuEntry(
                    icon = ZionAppIcons.Stats,
                    label = stringResource(R.string.stats_page_title),
                    onClick = { navController.navigate(Screen.Stats) }
                )
                Box {
                    SidebarMenuEntry(
                        icon = ZionAppIcons.Tool,
                        label = stringResource(R.string.menu),
                        onClick = { showToolsMenu = true }
                    )
                    DropdownMenu(
                        expanded = showToolsMenu,
                        onDismissRequest = { showToolsMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.chat_page_menu_ai_translator)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = ZionAppIcons.Globe,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                showToolsMenu = false
                                navController.navigate(Screen.Translator)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.chat_page_menu_image_generation)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = ZionAppIcons.Image,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                showToolsMenu = false
                                navController.navigate(Screen.ImageGen)
                            }
                        )
                    }
                }
                SidebarMenuEntry(
                    icon = ZionAppIcons.Settings,
                    label = stringResource(R.string.settings),
                    onClick = { navController.navigate(Screen.Setting) }
                )
            }

            ConversationList(
                current = current,
                conversations = conversations,
                conversationJobs = conversationJobs.keys,
                listState = conversationListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
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
                assistantName = currentAssistant.name.ifBlank {
                    stringResource(R.string.assistant_page_default_assistant)
                },
                onClick = { navController.navigate(Screen.Setting) },
                onEditNickname = {
                    nicknameEditState.open(settings.displaySetting.userNickname)
                }
            )
        }
    }

    nicknameEditState.EditStateContent { nickname, onUpdate ->
        AlertDialog(
            onDismissRequest = {
                nicknameEditState.dismiss()
            },
            title = {
                Text(stringResource(R.string.chat_page_edit_nickname))
            },
            text = {
                OutlinedTextField(
                    value = nickname,
                    onValueChange = onUpdate,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.chat_page_nickname_placeholder)) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        nicknameEditState.confirm()
                    }
                ) {
                    Text(stringResource(R.string.chat_page_save))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        nicknameEditState.dismiss()
                    }
                ) {
                    Text(stringResource(R.string.chat_page_cancel))
                }
            }
        )
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
                    items(settings.assistants) { assistant ->
                        AssistantItem(
                            assistant = assistant,
                            isCurrentAssistant = assistant.id == conversationToMove?.assistantId,
                            onClick = {
                                conversationToMove?.let { conversation ->
                                    vm.moveConversationToAssistant(conversation, assistant.id)
                                    scope.launch {
                                        bottomSheetState.hide()
                                        showMoveToAssistantSheet = false
                                        conversationToMove = null
                                    }
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    clip = false,
                    ambientColor = Color.Black.copy(alpha = 0.08f),
                    spotColor = Color.Black.copy(alpha = 0.08f)
                ),
            shape = RoundedCornerShape(20.dp),
            color = ZionSurface,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .pressableScale(
                        pressedScale = 0.985f,
                        onClick = onSearchClick
                    )
                    .padding(horizontal = 12.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    clip = false,
                    ambientColor = Color.Black.copy(alpha = 0.08f),
                    spotColor = Color.Black.copy(alpha = 0.08f)
                )
                .background(ZionSurface, CircleShape)
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
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.06f)
            ),
        shape = RoundedCornerShape(20.dp),
        color = ZionSurface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pressableScale(
                    pressedScale = 0.985f,
                    onClick = onClick
                )
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(ZionSectionItem, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ZionTextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = label,
                color = ZionTextPrimary,
                fontFamily = SourceSans3,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SidebarProfileCard(
    nickname: String,
    avatar: Avatar,
    assistantName: String,
    onClick: () -> Unit,
    onEditNickname: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(28.dp),
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(28.dp),
        color = ZionSurface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pressableScale(
                    pressedScale = 0.985f,
                    onClick = onClick
                )
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UIAvatar(
                name = nickname,
                value = avatar,
                onUpdate = {},
                modifier = Modifier.size(46.dp),
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
                    modifier = Modifier.clickable(onClick = onEditNickname)
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
                imageVector = ZionAppIcons.Settings,
                contentDescription = null,
                tint = ZionTextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun AssistantItem(
    assistant: Assistant,
    isCurrentAssistant: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = if (isCurrentAssistant) {
            ZionSurface
        } else {
            ZionSectionItem
        },
        tonalElevation = 0.dp,
        shadowElevation = if (isCurrentAssistant) 10.dp else 0.dp,
    ) {
        Row(
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
                name = assistant.name,
                value = assistant.avatar,
                onUpdate = {},
                modifier = Modifier.size(40.dp),
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = assistant.name.ifBlank { stringResource(R.string.assistant_page_default_assistant) },
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
                        color = ZionAccentBlue
                    )
                }
            }
        }
    }
}
