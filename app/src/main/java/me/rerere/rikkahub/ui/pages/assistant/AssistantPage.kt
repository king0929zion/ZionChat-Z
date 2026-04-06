package me.rerere.rikkahub.ui.pages.assistant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.Add01
import me.rerere.rikkahub.R
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.data.datastore.findModelById
import me.rerere.rikkahub.data.datastore.getBotAssistants
import me.rerere.rikkahub.data.model.Assistant
import me.rerere.rikkahub.data.model.Avatar
import me.rerere.rikkahub.ui.components.ui.HeaderActionButton
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.theme.ZionGrayLighter
import me.rerere.rikkahub.ui.theme.ZionSectionItem
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import org.koin.androidx.compose.koinViewModel

@Composable
fun AssistantPage(vm: AssistantVM = koinViewModel()) {
    val navController = LocalNavController.current
    val settings by vm.settings.collectAsStateWithLifecycle()
    val bots = settings.getBotAssistants()

    fun createBot() {
        val assistant = Assistant()
        vm.addAssistant(assistant)
        navController.navigate(Screen.AssistantDetail(assistant.id.toString()))
    }

    SettingsPage(
        title = stringResource(R.string.assistant_page_title),
        onBack = { navController.popBackStack() },
        trailing = {
            HeaderActionButton(
                onClick = ::createBot,
                icon = HugeIcons.Add01,
                contentDescription = stringResource(R.string.assistant_page_add)
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (bots.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.assistant_page_empty_desc),
                        color = ZionTextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 40.dp)
                    )
                }
            } else {
                bots.forEach { assistant ->
                    val modelName = settings.findModelById(
                        assistant.chatModelId ?: settings.chatModelId
                    )?.displayName
                    BotCard(
                        assistant = assistant,
                        modelDisplay = modelName,
                        onClick = {
                            navController.navigate(Screen.AssistantDetail(assistant.id.toString()))
                        }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun BotCard(
    assistant: Assistant,
    modelDisplay: String?,
    onClick: () -> Unit,
) {
    val displayName = assistant.name.ifBlank {
        stringResource(R.string.assistant_page_default_assistant)
    }

    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ZionSectionItem)
            .pressableScale(pressedScale = 0.98f, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BotAvatar(
            name = displayName,
            avatar = assistant.avatar,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = displayName,
                color = ZionTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!modelDisplay.isNullOrBlank()) {
                Text(
                    text = stringResource(R.string.assistant_page_model_summary, modelDisplay),
                    color = ZionTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun BotAvatar(
    name: String,
    avatar: Avatar,
) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(ZionGrayLighter),
        contentAlignment = Alignment.Center
    ) {
        when (avatar) {
            is Avatar.Image -> {
                AsyncImage(
                    model = avatar.url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            is Avatar.Emoji -> {
                Text(
                    text = avatar.content,
                    color = ZionTextPrimary,
                    fontSize = 28.sp
                )
            }

            Avatar.Dummy -> {
                Icon(
                    imageVector = ZionAppIcons.Bot,
                    contentDescription = name,
                    tint = ZionTextPrimary,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}
