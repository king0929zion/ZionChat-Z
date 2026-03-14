package me.rerere.rikkahub.ui.pages.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import me.rerere.rikkahub.data.datastore.Settings
import me.rerere.rikkahub.data.datastore.getCurrentAssistant
import me.rerere.rikkahub.ui.theme.ZionBackground
import me.rerere.rikkahub.ui.theme.ZionChatBackground

@Composable
fun AssistantBackground(setting: Settings) {
    val assistant = setting.getCurrentAssistant()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ZionBackground,
                        ZionChatBackground,
                    )
                )
            )
    ) {
        if (assistant.background != null) {
            val backgroundOpacity = assistant.backgroundOpacity.coerceIn(0f, 1f)
            AsyncImage(
                model = assistant.background,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(backgroundOpacity)
            )

            // 全屏渐变遮罩
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                ZionBackground.copy(alpha = 0.22f),
                                ZionChatBackground.copy(alpha = 0.6f)
                            )
                        )
                    )
            )
        }
    }
}
