package io.github.king0929zion.zionchatz.ui.context

import androidx.compose.runtime.staticCompositionLocalOf
import io.github.king0929zion.zionchatz.data.datastore.Settings

val LocalSettings = staticCompositionLocalOf<Settings> {
    error("No SettingsStore provided")
}
