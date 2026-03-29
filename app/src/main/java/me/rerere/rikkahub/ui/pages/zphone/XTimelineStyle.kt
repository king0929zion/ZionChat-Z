package me.rerere.rikkahub.ui.pages.zphone

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import me.rerere.rikkahub.data.datastore.Settings
import me.rerere.rikkahub.data.db.entity.XPostEntity
import me.rerere.rikkahub.data.model.Avatar
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

internal val XSurface = Color.White
internal val XBackground = Color(0xFF242D34)
internal val XText = Color(0xFF0F1419)
internal val XSubText = Color(0xFF536471)
internal val XDivider = Color(0xFFEFF3F4)
internal val XBlue = Color(0xFF1D9BF0)
internal val XGreen = Color(0xFF00BA7C)
internal val XPink = Color(0xFFF91880)
internal const val ComposerLimit = 280

internal data class AuthorVisual(
    val avatarUrl: String? = null,
    val background: Color,
    val foreground: Color = Color.White,
    val fallbackText: String,
)

@Composable
internal fun CurrentUserAvatar(
    settings: Settings,
    size: Dp,
) {
    val avatar = settings.displaySetting.userAvatar
    val name = settings.displaySetting.userNickname.ifBlank { localizedYou() }

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFFE5E7EB)),
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
                    fontSize = (size.value * 0.45f).sp
                )
            }

            Avatar.Dummy -> {
                Text(
                    text = name.take(1),
                    color = XText,
                    fontSize = (size.value * 0.42f).sp
                )
            }
        }
    }
}

@Composable
internal fun PostAvatar(
    settings: Settings,
    post: XPostEntity,
    size: Dp,
) {
    if (post.authorHandle == "@you") {
        CurrentUserAvatar(settings = settings, size = size)
        return
    }

    val visual = remember(post.authorHandle, post.authorKind) {
        authorVisual(post)
    }

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(visual.background),
        contentAlignment = Alignment.Center
    ) {
        if (visual.avatarUrl != null) {
            AsyncImage(
                model = visual.avatarUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = visual.fallbackText,
                color = visual.foreground,
                fontSize = (size.value * 0.42f).sp
            )
        }
    }
}

internal fun authorVisual(post: XPostEntity): AuthorVisual {
    return when (post.authorHandle.lowercase()) {
        "@elonmusk" -> AuthorVisual(
            avatarUrl = "https://images.unsplash.com/photo-1560250097-0b93528c311a?auto=format&fit=crop&w=200&q=80",
            background = Color(0xFF101418),
            fallbackText = "E"
        )

        "@scobleizer" -> AuthorVisual(
            avatarUrl = "https://images.unsplash.com/photo-1535295972055-1c762f4483e5?auto=format&fit=crop&w=200&q=80",
            background = Color(0xFF101418),
            fallbackText = "R"
        )

        "@synthwavedd" -> AuthorVisual(
            avatarUrl = "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=200&q=80",
            background = Color(0xFFE8EDF1),
            foreground = XText,
            fallbackText = "L"
        )

        "@grok" -> AuthorVisual(
            background = Color.Black,
            foreground = Color.White,
            fallbackText = "G"
        )

        else -> {
            val isAssistant = post.authorKind == "assistant"
            AuthorVisual(
                background = if (isAssistant) Color(0xFFCBD5F5) else Color(0xFFE5E7EB),
                foreground = if (isAssistant) Color(0xFF1F2937) else XText,
                fallbackText = post.authorName.take(1).uppercase()
            )
        }
    }
}

internal fun isVerified(post: XPostEntity): Boolean {
    return post.authorHandle in setOf("@elonmusk", "@scobleizer", "@synthwavedd", "@grok")
}

internal fun relativeTime(timestamp: Long): String {
    val isChinese = isChineseLocale()
    val diffMinutes = ((System.currentTimeMillis() - timestamp) / 60_000L).coerceAtLeast(0L)
    return when {
        diffMinutes < 1L -> if (isChinese) "刚刚" else "now"
        diffMinutes < 60L -> if (isChinese) "${diffMinutes}分钟" else "${diffMinutes}m"
        diffMinutes < 60L * 24L -> if (isChinese) "${diffMinutes / 60L}小时" else "${diffMinutes / 60L}h"
        else -> if (isChinese) "${diffMinutes / (60L * 24L)}天" else "${diffMinutes / (60L * 24L)}d"
    }
}

internal fun absoluteTime(timestamp: Long): String {
    val locale = Locale.getDefault()
    val formatter = DateTimeFormatter.ofPattern(
        if (isChineseLocale(locale)) "yy年M月d日, HH:mm" else "MMM d, yy, HH:mm",
        locale
    )
    return formatter.format(
        Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault())
    )
}

internal fun compactCount(count: Int): String {
    if (count <= 0) return "0"
    val locale = Locale.getDefault()
    val isChinese = isChineseLocale(locale)
    return when {
        count >= 1_000_000 -> {
            val value = count / 1_000_000f
            if (value >= 100f || value % 1f == 0f) {
                "${value.toInt()}M"
            } else {
                String.format(locale, "%.1fM", value)
            }
        }

        isChinese && count >= 10_000 -> {
            val value = count / 10_000f
            if (value >= 100f || value % 1f == 0f) {
                "${value.toInt()}万"
            } else {
                String.format(locale, "%.1f万", value)
            }
        }

        count >= 1_000 -> String.format(locale, "%.1fK", count / 1_000f)
        else -> count.toString()
    }
}

internal fun timelineMetricCount(count: Int): String {
    if (count <= 0) return "0"
    val locale = Locale.getDefault()
    val isChinese = isChineseLocale(locale)
    return when {
        isChinese && count >= 10_000 -> {
            val value = count / 10_000f
            if (value >= 100f || value % 1f == 0f) {
                "${value.toInt()}万"
            } else {
                String.format(locale, "%.1f万", value)
            }
        }

        !isChinese && count >= 1_000_000 -> {
            val value = count / 1_000_000f
            if (value >= 100f || value % 1f == 0f) {
                "${value.toInt()}M"
            } else {
                String.format(locale, "%.1fM", value)
            }
        }

        !isChinese && count >= 10_000 -> {
            val value = count / 1_000f
            if (value >= 100f || value % 1f == 0f) {
                "${value.toInt()}K"
            } else {
                String.format(locale, "%.1fK", value)
            }
        }

        else -> NumberFormat.getIntegerInstance(locale).format(count)
    }
}

internal fun detailMetricCount(count: Int): String {
    return NumberFormat.getIntegerInstance(Locale.getDefault()).format(count)
}

internal fun quoteTimeLabel(post: XPostEntity): String? {
    return when (post.authorHandle.lowercase()) {
        "@elonmusk" -> localizedHoursAgo(7)
        "@synthwavedd" -> localizedHoursAgo(1)
        else -> null
    }
}

internal fun detailReplyContent(post: XPostEntity): String {
    return if (post.authorHandle.lowercase() == "@grok") {
        "Thanks! We've been optimizing hard at xAI—speed improvements like this are rolling out fast. Glad the transcript readings feel snappier. What else are you testing?"
    } else {
        post.content
    }
}

private fun localizedHoursAgo(hours: Int): String {
    return if (isChineseLocale()) "${hours}小时" else "${hours}h"
}

private fun localizedYou(): String {
    return if (isChineseLocale()) "你" else "You"
}

private fun isChineseLocale(locale: Locale = Locale.getDefault()): Boolean {
    return locale.language.equals("zh", ignoreCase = true)
}
