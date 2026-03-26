package me.rerere.rikkahub.ui.pages.personalization

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.Delete01
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.datastore.getPersonalizationAssistant
import me.rerere.rikkahub.data.model.Assistant
import me.rerere.rikkahub.data.model.AssistantMemory
import me.rerere.rikkahub.data.repository.MemoryRepository
import me.rerere.rikkahub.ui.components.ui.HeaderActionButton
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.pages.setting.SettingVM
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionSectionItem
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import kotlin.math.roundToInt

@Composable
fun PersonalizationMemoryPage(vm: SettingVM = koinViewModel()) {
    val navController = LocalNavController.current
    val memoryRepository: MemoryRepository = koinInject()
    val settings by vm.settings.collectAsStateWithLifecycle()
    val personalization = settings.getPersonalizationAssistant()
    val memoriesFlow = remember(personalization.id, personalization.useGlobalMemory) {
        if (personalization.useGlobalMemory) {
            memoryRepository.getGlobalMemoriesFlow()
        } else {
            memoryRepository.getMemoriesOfAssistantFlow(personalization.id.toString())
        }
    }
    val memories by memoriesFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    var openedMemoryId by remember { mutableStateOf<Int?>(null) }
    var editingMemory by remember { mutableStateOf<AssistantMemory?>(null) }
    var editorText by remember { mutableStateOf("") }

    fun updatePersonalization(transform: (Assistant) -> Assistant) {
        vm.updateSettings(
            settings.copy(
                assistants = settings.assistants.map { assistant ->
                    if (assistant.id == personalization.id) {
                        transform(assistant)
                    } else {
                        assistant
                    }
                }
            )
        )
    }

    val memoryOwnerId = remember(personalization.id, personalization.useGlobalMemory) {
        if (personalization.useGlobalMemory) {
            MemoryRepository.GLOBAL_MEMORY_ID
        } else {
            personalization.id.toString()
        }
    }

    SettingsPage(
        title = stringResource(R.string.personalization_memories),
        onBack = { navController.popBackStack() },
        trailing = {
            HeaderActionButton(
                onClick = {
                    editingMemory = AssistantMemory(id = 0, content = "")
                    editorText = ""
                },
                icon = ZionAppIcons.Plus,
                contentDescription = stringResource(R.string.add)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = PageTopBarContentTopPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            PersonalizationSectionTitle(title = stringResource(R.string.personalization_memory))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = ZionSectionItem)
            ) {
                PersonalizationSwitchRow(
                    title = stringResource(R.string.assistant_page_memory),
                    subtitle = stringResource(R.string.assistant_page_memory_desc),
                    checked = personalization.enableMemory,
                    onCheckedChange = {
                        updatePersonalization { assistant -> assistant.copy(enableMemory = it) }
                    }
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.74f))
                PersonalizationSwitchRow(
                    title = stringResource(R.string.assistant_page_global_memory),
                    subtitle = stringResource(R.string.assistant_page_global_memory_desc),
                    checked = personalization.useGlobalMemory,
                    enabled = personalization.enableMemory,
                    onCheckedChange = {
                        updatePersonalization { assistant -> assistant.copy(useGlobalMemory = it) }
                    }
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.74f))
                PersonalizationSwitchRow(
                    title = stringResource(R.string.assistant_page_recent_chats),
                    subtitle = stringResource(R.string.assistant_page_recent_chats_desc),
                    checked = personalization.enableRecentChatsReference,
                    onCheckedChange = {
                        updatePersonalization { assistant -> assistant.copy(enableRecentChatsReference = it) }
                    }
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.74f))
                PersonalizationSwitchRow(
                    title = stringResource(R.string.assistant_page_time_reminder),
                    subtitle = stringResource(R.string.assistant_page_time_reminder_desc),
                    checked = personalization.enableTimeReminder,
                    onCheckedChange = {
                        updatePersonalization { assistant -> assistant.copy(enableTimeReminder = it) }
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.personalization_memories_summary, memories.size),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = SourceSans3,
                    color = ZionTextSecondary
                )
                if (memories.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                memoryRepository.deleteMemoriesOfAssistant(memoryOwnerId)
                                openedMemoryId = null
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.personalization_memory_clear_all),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = SourceSans3,
                            color = Color(0xFFFF3B30)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (memories.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = ZionSectionItem)
                ) {
                    Text(
                        text = stringResource(R.string.personalization_memory_empty),
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
                        fontSize = 16.sp,
                        fontFamily = SourceSans3,
                        color = ZionTextSecondary
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    memories.forEach { memory ->
                        SwipeableMemoryItem(
                            content = memory.content,
                            isOpened = openedMemoryId == memory.id,
                            onOpenChanged = { opened ->
                                openedMemoryId = if (opened) memory.id else null
                            },
                            onDelete = {
                                scope.launch {
                                    memoryRepository.deleteMemory(memory.id)
                                    if (openedMemoryId == memory.id) {
                                        openedMemoryId = null
                                    }
                                }
                            },
                            onEdit = {
                                editingMemory = memory
                                editorText = memory.content
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    LaunchedEffect(editingMemory) {
        if (editingMemory == null) {
            editorText = ""
        }
    }

    editingMemory?.let { memory ->
        AlertDialog(
            onDismissRequest = { editingMemory = null },
            title = {
                Text(
                    text = stringResource(R.string.assistant_page_manage_memory_title),
                    fontFamily = SourceSans3,
                    color = ZionTextPrimary
                )
            },
            text = {
                OutlinedTextField(
                    value = editorText,
                    onValueChange = { editorText = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 8,
                    shape = RoundedCornerShape(18.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val content = editorText.trim()
                        if (content.isNotEmpty()) {
                            scope.launch {
                                if (memory.id == 0) {
                                    memoryRepository.addMemory(memoryOwnerId, content)
                                } else {
                                    memoryRepository.updateContent(memory.id, content)
                                }
                                editingMemory = null
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.assistant_page_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { editingMemory = null }) {
                    Text(stringResource(R.string.assistant_page_cancel))
                }
            }
        )
    }
}

@Composable
private fun PersonalizationSectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = SourceSans3,
        color = ZionTextSecondary,
        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
    )
}

@Composable
private fun PersonalizationSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = SourceSans3,
                color = if (enabled) ZionTextPrimary else ZionTextSecondary
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontFamily = SourceSans3,
                color = ZionTextSecondary
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SwipeableMemoryItem(
    content: String,
    isOpened: Boolean,
    onOpenChanged: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
) {
    val itemScope = androidx.compose.runtime.rememberCoroutineScope()
    val actionWidth = 72.dp
    val actionWidthPx = with(LocalDensity.current) { actionWidth.toPx() }
    val swipeableState = rememberSwipeableState(
        initialValue = if (isOpened) 1 else 0,
        confirmStateChange = { targetValue ->
            onOpenChanged(targetValue != 0)
            true
        }
    )
    val anchors = remember(actionWidthPx) { mapOf(0f to 0, -actionWidthPx to 1) }

    LaunchedEffect(isOpened) {
        val target = if (isOpened) 1 else 0
        if (swipeableState.currentValue != target) {
            swipeableState.animateTo(target)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 8.dp)
                .size(72.dp)
                .align(Alignment.CenterEnd),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFF3B30))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            onOpenChanged(false)
                            onDelete()
                            itemScope.launch { swipeableState.animateTo(0) }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = HugeIcons.Delete01,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    thresholds = { _, _ -> FractionalThreshold(0.3f) },
                    orientation = Orientation.Horizontal
                )
                .pressableScale(
                    pressedScale = 0.985f,
                    onClick = {
                        if (swipeableState.currentValue != 0) {
                            itemScope.launch { swipeableState.animateTo(0) }
                        } else {
                            onEdit()
                        }
                    }
                ),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = ZionSectionItem)
        ) {
            Text(
                text = content,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                fontSize = 17.sp,
                lineHeight = 24.sp,
                fontFamily = SourceSans3,
                color = ZionTextPrimary
            )
        }
    }
}
