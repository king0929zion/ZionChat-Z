package io.github.king0929zion.zchat.ui.components.message

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.datetime.toJavaLocalDateTime
import io.github.king0929zion.zchat.ai.core.MessageRole
import io.github.king0929zion.zchat.ai.provider.Model
import io.github.king0929zion.zchat.ai.ui.UIMessage
import io.github.king0929zion.zchat.ai.ui.isEmptyUIMessage
import io.github.king0929zion.zchat.R
import io.github.king0929zion.zchat.data.model.Assistant
import io.github.king0929zion.zchat.data.model.Avatar
import io.github.king0929zion.zchat.ui.components.ui.AutoAIIcon
import io.github.king0929zion.zchat.ui.components.ui.UIAvatar
import io.github.king0929zion.zchat.ui.context.LocalSettings
import io.github.king0929zion.zchat.utils.formatNumber
import io.github.king0929zion.zchat.utils.toLocalString

@Composable
fun ChatMessageUserAvatar(
    message: UIMessage,
    avatar: Avatar,
    nickname: String,
    modifier: Modifier = Modifier,
) {
    val settings = LocalSettings.current
    if (message.role == MessageRole.USER && !message.parts.isEmptyUIMessage() && settings.displaySetting.showUserAvatar) {
        Row(
            modifier = modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = nickname.ifEmpty { stringResource(R.string.user_default_name) },
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    color = LocalContentColor.current.copy(alpha = 0.85f),
                )
                if (settings.displaySetting.showDateBelowName) {
                    Text(
                        text = message.createdAt.toJavaLocalDateTime().toLocalString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = LocalContentColor.current.copy(alpha = 0.6f),
                        maxLines = 1,
                    )
                }
            }
            UIAvatar(
                name = nickname,
                modifier = Modifier.size(36.dp),
                value = avatar,
                loading = false,
            )
        }
    }
}

@Composable
fun ChatMessageAssistantAvatar(
    message: UIMessage,
    loading: Boolean,
    model: Model?,
    assistant: Assistant?,
    modifier: Modifier = Modifier,
) {
    val settings = LocalSettings.current
    val showIcon = settings.displaySetting.showModelIcon
    if (message.role == MessageRole.ASSISTANT && model != null) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            if (assistant?.useAssistantAvatar == true) {
                if (showIcon) {
                    UIAvatar(
                        name = assistant.name,
                        modifier = Modifier.size(32.dp),
                        value = assistant.avatar,
                        loading = loading,
                    )
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    if(settings.displaySetting.showModelName) {
                        Text(
                            text = assistant.name.ifEmpty { stringResource(R.string.assistant_page_default_assistant) },
                            style = MaterialTheme.typography.titleSmallEmphasized,
                            maxLines = 1,
                        )
                        if (settings.displaySetting.showDateBelowName) {
                            Text(
                                text = message.createdAt.toJavaLocalDateTime().toLocalString(),
                                style = MaterialTheme.typography.titleSmall,
                                color = LocalContentColor.current.copy(alpha = 0.8f),
                                maxLines = 1,
                            )
                        }
                    }
                }
            } else {
                if (showIcon) {
                    AutoAIIcon(
                        name = model.modelId,
                        modifier = Modifier.size(32.dp),
                        loading = loading
                    )
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    if(settings.displaySetting.showModelName) {
                        Text(
                            text = model.displayName,
                            style = MaterialTheme.typography.titleSmallEmphasized,
                        )
                        if (settings.displaySetting.showDateBelowName) {
                            Text(
                                text = message.createdAt.toJavaLocalDateTime().toLocalString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = LocalContentColor.current.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}
