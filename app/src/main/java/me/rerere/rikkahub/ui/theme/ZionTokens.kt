package me.rerere.rikkahub.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

val ZionBackground = Color(0xFFFCFCFC)
val ZionChatBackground = Color(0xFFF5F5F5)
val ZionSurface = Color(0xFFFFFFFF)
val ZionTextPrimary = Color(0xFF1C1C1E)
val ZionTextSecondary = Color(0xFF8E8E93)
val ZionGrayLight = Color(0xFFE5E5EA)
val ZionGrayLighter = Color(0xFFF2F2F7)
val ZionSectionItem = Color(0xFFF1F1F1)
val ZionSectionItemPressed = Color(0xFFE5E5E5)
val ZionDivider = Color(0xFFE4E4E4)
val ZionAccentNeutral = Color(0xFF111214)
val ZionAccentNeutralSoft = Color(0xFFF0F0F0)
val ZionAccentNeutralBorder = Color(0xFFE0E0E5)
val ZionAccentBlue = Color(0xFF007AFF)
val ZionSelectedToolBackground = Color(0xFFE8F4FD)
val ZionActionIcon = Color(0xFF374151)
val ZionToggleActive = Color(0xFF34C759)
val ZionUserMessageBubble = Color(0xFFEEEEEE)
val ZionThinkingBackground = Color(0xFFF5F5F5)
val ZionThinkingLabelColor = Color(0xFF3A3A3C)

@Immutable
data class ZionAccentPalette(
    val key: String,
    val actionColor: Color,
    val bubbleColor: Color,
    val bubbleTextColor: Color,
    val bubbleColorSecondary: Color? = null,
)

private val defaultAccentPalette = ZionAccentPalette(
    key = "default",
    actionColor = Color(0xFF9CA3AF),
    bubbleColor = Color(0xFFEFF2F6),
    bubbleTextColor = Color(0xFF1E2733),
)

private val accentPaletteMap = mapOf(
    "default" to defaultAccentPalette,
    "blue" to ZionAccentPalette(
        key = "blue",
        actionColor = Color(0xFF3B82F6),
        bubbleColor = Color(0xFFE8F1FF),
        bubbleTextColor = Color(0xFF173A66),
    ),
    "pink" to ZionAccentPalette(
        key = "pink",
        actionColor = Color(0xFFEC4899),
        bubbleColor = Color(0xFFFFEBF5),
        bubbleTextColor = Color(0xFF5F1F42),
    ),
    "orange" to ZionAccentPalette(
        key = "orange",
        actionColor = Color(0xFFF97316),
        bubbleColor = Color(0xFFFFEFE4),
        bubbleTextColor = Color(0xFF663410),
    ),
    "black" to ZionAccentPalette(
        key = "black",
        actionColor = Color(0xFF111214),
        bubbleColor = Color(0xFF2C2D31),
        bubbleTextColor = Color(0xFFF7F7F8),
        bubbleColorSecondary = Color(0xFF16171A),
    ),
)

fun zionAccentPaletteForKey(key: String?): ZionAccentPalette {
    return accentPaletteMap[key?.trim()?.lowercase()] ?: defaultAccentPalette
}
