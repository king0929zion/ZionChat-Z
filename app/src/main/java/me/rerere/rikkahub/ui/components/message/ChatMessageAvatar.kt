package me.rerere.rikkahub.ui.components.message

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.datetime.toJavaLocalDateTime
import me.rerere.ai.core.MessageRole
import me.rerere.ai.provider.Model
import me.rerere.ai.ui.UIMessage
import me.rerere.ai.ui.isEmptyUIMessage
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.model.Assistant
import me.rerere.rikkahub.ui.context.LocalSettings
import me.rerere.rikkahub.utils.toLocalString

@Composable
fun ChatMessageMeta(
    message: UIMessage,
    model: Model?,
    assistant: Assistant?,
    modifier: Modifier = Modifier,
) {
    val settings = LocalSettings.current
    if (message.parts.isEmptyUIMessage()) {
        return
    }

    when (message.role) {
        MessageRole.USER -> {
            val showNickname = settings.displaySetting.userNickname.isNotBlank()
            val showDate = settings.displaySetting.showDateBelowName
            if (!showNickname && !showDate) {
                return
            }
            Column(
                modifier = modifier.padding(top = 4.dp, bottom = 2.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                if (showNickname) {
                    Text(
                        text = settings.displaySetting.userNickname,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        color = LocalContentColor.current.copy(alpha = 0.85f),
                    )
                }
                if (showDate) {
                    Text(
                        text = message.createdAt.toJavaLocalDateTime().toLocalString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = LocalContentColor.current.copy(alpha = 0.6f),
                        maxLines = 1,
                    )
                }
            }
        }

        MessageRole.ASSISTANT -> {
            val showName = settings.displaySetting.showModelName
            val showDate = settings.displaySetting.showDateBelowName
            val displayName = when {
                assistant?.useAssistantAvatar == true -> assistant.name.ifEmpty {
                    stringResource(R.string.assistant_page_default_assistant)
                }
                model != null -> model.displayName
                else -> null
            }
            if ((!showName || displayName.isNullOrBlank()) && !showDate) {
                return
            }
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 2.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (showName && !displayName.isNullOrBlank()) {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleSmallEmphasized,
                        maxLines = 1,
                        color = LocalContentColor.current.copy(alpha = 0.88f),
                    )
                }
                if (showDate) {
                    Text(
                        text = message.createdAt.toJavaLocalDateTime().toLocalString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = LocalContentColor.current.copy(alpha = 0.62f),
                        maxLines = 1,
                    )
                }
            }
        }

        else -> Unit
    }
}
