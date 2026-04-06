package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.material3.Switch as MaterialSwitch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
internal fun PluginSystemSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    MaterialSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = Color.Black,
            checkedBorderColor = Color.Black,
            uncheckedThumbColor = Color.Black,
            uncheckedTrackColor = Color.White,
            uncheckedBorderColor = Color(0xFF1C1C1E),
        )
    )
}
