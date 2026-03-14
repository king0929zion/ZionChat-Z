package me.rerere.rikkahub.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import kotlinx.serialization.Serializable
import me.rerere.rikkahub.ui.hooks.rememberUserSettingsState

private val ExtendLightColors = lightExtendColors()
val LocalExtendColors = compositionLocalOf { ExtendLightColors }

val LocalDarkMode = compositionLocalOf { false }

@Serializable
enum class ColorMode {
    SYSTEM,
    LIGHT,
    DARK
}

private object NoRippleIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): androidx.compose.ui.Modifier.Node {
        return NoRippleIndicationNode()
    }

    override fun equals(other: Any?): Boolean = other === this

    override fun hashCode(): Int = javaClass.hashCode()

    private class NoRippleIndicationNode : androidx.compose.ui.Modifier.Node(), DrawModifierNode {
        override fun ContentDrawScope.draw() {
            drawContent()
        }
    }
}

private fun zionLightColorScheme(
    primary: Color,
    secondary: Color,
    tertiary: Color,
): androidx.compose.material3.ColorScheme = lightColorScheme(
    primary = primary,
    onPrimary = ZionSurface,
    primaryContainer = primary.copy(alpha = 0.18f),
    onPrimaryContainer = ZionTextPrimary,
    secondary = secondary,
    onSecondary = ZionSurface,
    secondaryContainer = ZionGrayLighter,
    onSecondaryContainer = ZionTextPrimary,
    tertiary = tertiary,
    onTertiary = ZionSurface,
    tertiaryContainer = ZionGrayLighter,
    onTertiaryContainer = ZionTextPrimary,
    error = Color(0xFFD9534F),
    onError = ZionSurface,
    errorContainer = Color(0xFFFFE5E2),
    onErrorContainer = Color(0xFF7A1D14),
    background = ZionBackground,
    onBackground = ZionTextPrimary,
    surface = ZionSurface,
    onSurface = ZionTextPrimary,
    surfaceVariant = ZionGrayLighter,
    onSurfaceVariant = ZionTextSecondary,
    outline = ZionGrayLight,
    outlineVariant = ZionGrayLight.copy(alpha = 0.7f),
    scrim = Color.Black.copy(alpha = 0.32f),
    inverseSurface = ZionTextPrimary,
    inverseOnSurface = ZionSurface,
    inversePrimary = primary,
    surfaceDim = Color(0xFFF2F2F2),
    surfaceBright = ZionSurface,
    surfaceContainerLowest = ZionSurface,
    surfaceContainerLow = Color(0xFFF9F9FB),
    surfaceContainer = ZionGrayLighter,
    surfaceContainerHigh = Color(0xFFF0F0F2),
    surfaceContainerHighest = Color(0xFFECECEF),
)

@Composable
fun RikkahubTheme(
    content: @Composable () -> Unit
) {
    val settings by rememberUserSettingsState()
    val context = LocalContext.current
    val presetLightScheme = remember(settings.themeId) {
        findPresetTheme(settings.themeId).standardLight
    }
    val dynamicScheme = remember(settings.dynamicColor, context) {
        if (settings.dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dynamicLightColorScheme(context)
        } else {
            null
        }
    }
    val colorScheme = remember(dynamicScheme, presetLightScheme) {
        zionLightColorScheme(
            primary = dynamicScheme?.primary ?: presetLightScheme.primary,
            secondary = dynamicScheme?.secondary ?: presetLightScheme.secondary,
            tertiary = dynamicScheme?.tertiary ?: presetLightScheme.tertiary,
        )
    }
    val extendColors = ExtendLightColors

    // 更新状态栏图标颜色
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
            }
        }
    }

    CompositionLocalProvider(
        LocalDarkMode provides false,
        LocalExtendColors provides extendColors,
        LocalIndication provides NoRippleIndication,
    ) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
            motionScheme = MotionScheme.expressive()
        )
    }
}

val MaterialTheme.extendColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendColors.current
