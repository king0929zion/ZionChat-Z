package me.rerere.rikkahub.ui.pages.personalization

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.Brain02
import me.rerere.rikkahub.R
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.data.datastore.getPersonalizationAssistant
import me.rerere.rikkahub.data.repository.MemoryRepository
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.pages.setting.SettingVM
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionSectionItem
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun PersonalizationPage(vm: SettingVM = koinViewModel()) {
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

    var nickname by rememberSaveable { mutableStateOf("") }
    var instructions by rememberSaveable { mutableStateOf("") }
    var nicknameFocused by remember { mutableStateOf(false) }
    var instructionsFocused by remember { mutableStateOf(false) }

    LaunchedEffect(settings.displaySetting.userNickname, nicknameFocused) {
        if (!nicknameFocused && nickname != settings.displaySetting.userNickname) {
            nickname = settings.displaySetting.userNickname
        }
    }

    LaunchedEffect(personalization.systemPrompt, instructionsFocused) {
        if (!instructionsFocused && instructions != personalization.systemPrompt) {
            instructions = personalization.systemPrompt
        }
    }

    SettingsPage(
        title = stringResource(R.string.setting_page_personalization),
        onBack = { navController.popBackStack() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = PageTopBarContentTopPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            PersonalizationSectionTitle(title = stringResource(R.string.personalization_nickname))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = ZionSectionItem)
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    BasicTextField(
                        value = nickname,
                        onValueChange = { value ->
                            nickname = value
                            vm.updateSettings(
                                settings.copy(
                                    displaySetting = settings.displaySetting.copy(userNickname = value)
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { nicknameFocused = it.isFocused },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = ZionTextPrimary,
                            fontFamily = SourceSans3
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(ZionTextPrimary),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxWidth()) {
                                if (nickname.isBlank()) {
                                    Text(
                                        text = stringResource(R.string.personalization_nickname_placeholder),
                                        fontSize = 16.sp,
                                        fontFamily = SourceSans3,
                                        color = Color(0xFFC7C7CC)
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PersonalizationSectionTitle(title = stringResource(R.string.personalization_custom_instructions))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 280.dp),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = ZionSectionItem)
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    BasicTextField(
                        value = instructions,
                        onValueChange = { value ->
                            instructions = value
                            vm.updateSettings(
                                settings.copy(
                                    assistants = settings.assistants.map { assistant ->
                                        if (assistant.id == personalization.id) {
                                            assistant.copy(systemPrompt = value)
                                        } else {
                                            assistant
                                        }
                                    }
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 280.dp)
                            .onFocusChanged { instructionsFocused = it.isFocused },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 22.sp,
                            color = ZionTextPrimary,
                            fontFamily = SourceSans3
                        ),
                        cursorBrush = SolidColor(ZionTextPrimary),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxSize()) {
                                if (instructions.isBlank()) {
                                    Text(
                                        text = stringResource(R.string.personalization_instructions_placeholder),
                                        fontSize = 16.sp,
                                        lineHeight = 22.sp,
                                        fontFamily = SourceSans3,
                                        color = Color(0xFFC7C7CC)
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PersonalizationSectionTitle(title = stringResource(R.string.personalization_memory))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = ZionSectionItem)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { navController.navigate(Screen.PersonalizationMemory) }
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = HugeIcons.Brain02,
                        contentDescription = null,
                        tint = ZionTextPrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.personalization_memories),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = SourceSans3,
                        color = ZionTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = stringResource(R.string.personalization_memories_count, memories.size),
                        fontSize = 15.sp,
                        fontFamily = SourceSans3,
                        color = ZionTextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = me.rerere.rikkahub.ui.icons.ZionAppIcons.ChevronRight,
                        contentDescription = null,
                        tint = ZionTextSecondary,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
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
