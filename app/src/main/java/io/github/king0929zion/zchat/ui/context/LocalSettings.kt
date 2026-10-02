package io.github.king0929zion.zchat.ui.context

import androidx.compose.runtime.staticCompositionLocalOf
import io.github.king0929zion.zchat.data.datastore.Settings

val LocalSettings = staticCompositionLocalOf<Settings> {
    error("No SettingsStore provided")
}
