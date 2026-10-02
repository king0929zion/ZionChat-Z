package io.github.king0929zion.zchat.ui.hooks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.king0929zion.zchat.data.datastore.Settings
import io.github.king0929zion.zchat.data.datastore.SettingsStore
import org.koin.compose.koinInject

@Composable
fun rememberUserSettingsState(): State<Settings> {
    val store = koinInject<SettingsStore>()
    return store.settingsFlow.collectAsStateWithLifecycle(
        initialValue = Settings.dummy(),
    )
}
