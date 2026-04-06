package me.rerere.rikkahub.ui.pages.assistant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.Add01
import me.rerere.rikkahub.R
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.data.datastore.findModelById
import me.rerere.rikkahub.data.datastore.getBotAssistants
import me.rerere.rikkahub.data.model.Assistant
import me.rerere.rikkahub.ui.components.ui.HeaderActionButton
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.UIAvatar
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionBackground
import me.rerere.rikkahub.ui.theme.ZionSectionItem
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import org.koin.androidx.compose.koinViewModel

@Composable
fun AssistantPage(vm: AssistantVM = koinViewModel()) {
    val navController = LocalNavController.current
    val settings by vm.settings.collectAsStateWithLifecycle()
    val bots = settings.getBotAssistants()
    val untitledBotLabel = stringResource(R.string.assistant_page_default_assistant)

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ZionBackground),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = PageTopBarContentTopPadding + 8.dp,
                end = 16.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (bots.isEmpty()) {
                item("empty") {
                    EmptyBotCard(onClick = ::createBot)
                }
            } else {
                items(
                    items = bots,
                    key = { it.id.toString() }
                ) { assistant ->
                    val resolvedModel = settings.findModelById(
                        assistant.chatModelId ?: settings.chatModelId
                    )
                    val displayName = assistant.name.ifBlank { untitledBotLabel }
                    val modelName = resolvedModel?.displayName ?: stringResource(R.string.not_set)

                    BotListCard(
                        assistant = assistant,
                        displayName = displayName,
                        modelName = modelName,
                        onClick = {
                            navController.navigate(Screen.AssistantDetail(assistant.id.toString()))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BotListCard(
    assistant: Assistant,
    displayName: String,
    modelName: String,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(26.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(ZionSectionItem)
            .pressableScale(pressedScale = 0.985f, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp)
            .heightIn(min = 88.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UIAvatar(
            name = displayName,
            value = assistant.avatar,
            modifier = Modifier.size(54.dp),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = displayName,
                color = ZionTextPrimary,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = SourceSans3,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.assistant_page_model_summary, modelName),
                color = ZionTextSecondary,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontFamily = SourceSans3,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EmptyBotCard(
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(26.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(ZionSectionItem)
            .pressableScale(pressedScale = 0.985f, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp)
            .heightIn(min = 88.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(ZionBackground),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ZionAppIcons.Bot,
                contentDescription = null,
                tint = ZionTextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.assistant_page_empty_title),
                color = ZionTextPrimary,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = SourceSans3,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.assistant_page_empty_desc),
                color = ZionTextSecondary,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontFamily = SourceSans3,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
