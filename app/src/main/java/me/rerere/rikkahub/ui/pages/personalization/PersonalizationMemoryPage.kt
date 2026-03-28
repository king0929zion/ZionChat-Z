package me.rerere.rikkahub.ui.pages.personalization

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.Cancel01
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

@Composable
fun PersonalizationMemoryPage(vm: SettingVM = koinViewModel()) {
    val navController = LocalNavController.current
    val memoryRepository: MemoryRepository = koinInject()
    val settings by vm.settings.collectAsStateWithLifecycle()
    val personalization = settings.getPersonalizationAssistant()
    val memoriesFlow = remember(personalization.id) {
        memoryRepository.getMemoriesOfAssistantFlow(personalization.id.toString())
    }
    val memories by memoriesFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = androidx.compose.runtime.rememberCoroutineScope()

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

    val memoryOwnerId = remember(personalization.id) {
        personalization.id.toString()
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

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = ZionSectionItem)
            ) {
                PersonalizationSwitchRow(
                    title = stringResource(R.string.assistant_page_recent_chats),
                    checked = personalization.enableRecentChatsReference,
                    onCheckedChange = {
                        updatePersonalization { assistant -> assistant.copy(enableRecentChatsReference = it) }
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
                            onDelete = {
                                scope.launch {
                                    memoryRepository.deleteMemory(memory.id)
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
private fun PersonalizationSwitchRow(
    title: String,
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
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = SourceSans3,
            color = if (enabled) ZionTextPrimary else ZionTextSecondary,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF1C1C1E),
                checkedBorderColor = Color(0xFF1C1C1E),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFD9D9DE),
                uncheckedBorderColor = Color(0xFFD9D9DE)
            )
        )
    }
}

@Composable
private fun SwipeableMemoryItem(
    content: String,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
) {
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val dismissState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        scope.launch { dismissState.reset() }
                    }
                ) {
                    Icon(HugeIcons.Cancel01, null)
                }
                FilledIconButton(
                    onClick = {
                        scope.launch {
                            onDelete()
                            dismissState.reset()
                        }
                    }
                ) {
                    Icon(
                        imageVector = HugeIcons.Delete01,
                        contentDescription = stringResource(R.string.chat_page_delete)
                    )
                }
            }
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        modifier = Modifier.clip(RoundedCornerShape(26.dp))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .pressableScale(
                    pressedScale = 0.985f,
                    onClick = onEdit
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
