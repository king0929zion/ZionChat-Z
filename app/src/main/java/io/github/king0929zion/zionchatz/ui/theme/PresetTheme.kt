package io.github.king0929zion.zionchatz.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import io.github.king0929zion.zionchatz.ui.theme.presets.AutumnThemePreset
import io.github.king0929zion.zionchatz.ui.theme.presets.BlackThemePreset
import io.github.king0929zion.zionchatz.ui.theme.presets.OceanThemePreset
import io.github.king0929zion.zionchatz.ui.theme.presets.SakuraThemePreset
import io.github.king0929zion.zionchatz.ui.theme.presets.SpringThemePreset

data class PresetTheme(
    val id: String,
    val name: @Composable () -> Unit,
    val standardLight: ColorScheme,
    val standardDark: ColorScheme,
) {
    fun getColorScheme(dark: Boolean): ColorScheme {
        return if (dark) standardDark else standardLight
    }
}

val PresetThemes by lazy {
    listOf(
        SakuraThemePreset,
        OceanThemePreset,
        SpringThemePreset,
        AutumnThemePreset,
        BlackThemePreset,
    )
}

fun findPresetTheme(id: String): PresetTheme {
    return PresetThemes.find { it.id == id } ?: SakuraThemePreset
}
