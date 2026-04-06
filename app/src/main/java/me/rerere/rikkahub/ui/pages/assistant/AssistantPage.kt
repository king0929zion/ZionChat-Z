package me.rerere.rikkahub.ui.pages.assistant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.Add01
import me.rerere.rikkahub.R
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.data.datastore.findModelById
import me.rerere.rikkahub.data.datastore.getBotAssistants
import me.rerere.rikkahub.data.model.Assistant
import me.rerere.rikkahub.ui.components.ui.CardGroup
import me.rerere.rikkahub.ui.components.ui.HeaderActionButton
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.UIAvatar
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.icons.ZionAppIcons
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
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = PageTopBarContentTopPadding,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item("bots") {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = { Text(stringResource(R.string.assistant_page_title).uppercase()) }
                ) {
                    if (bots.isEmpty()) {
                        item(
                            onClick = ::createBot,
                            leadingContent = {
                                Icon(
                                    imageVector = ZionAppIcons.Bot,
                                    contentDescription = null,
                                    tint = ZionTextPrimary
                                )
                            },
                            headlineContent = {
                                Text(stringResource(R.string.assistant_page_add))
                            },
                            supportingContent = {
                                Text(stringResource(R.string.assistant_page_empty_desc))
                            }
                        )
                    } else {
                        bots.forEach { assistant ->
                            val resolvedModel = settings.findModelById(
                                assistant.chatModelId ?: settings.chatModelId
                            )
                            val displayName = assistant.name.ifBlank { untitledBotLabel }
                            item(
                                onClick = {
                                    navController.navigate(Screen.AssistantDetail(assistant.id.toString()))
                                },
                                leadingContent = {
                                    UIAvatar(
                                        name = displayName,
                                        value = assistant.avatar,
                                    )
                                },
                                headlineContent = {
                                    Text(
                                        text = displayName,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                supportingContent = {
                                    BotSummaryText(
                                        modelName = resolvedModel?.displayName,
                                        prompt = assistant.systemPrompt
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BotSummaryText(
    modelName: String?,
    prompt: String,
) {
    val fallbackModelName = stringResource(R.string.not_set)
    val summary = prompt
        .lineSequence()
        .map { it.trim() }
        .firstOrNull { it.isNotEmpty() }
        .orEmpty()
    val headline = modelName ?: fallbackModelName

    Text(
        text = buildString {
            append(headline)
            if (summary.isNotBlank()) {
                append(" · ")
                append(summary)
            }
        },
        color = ZionTextSecondary,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}
