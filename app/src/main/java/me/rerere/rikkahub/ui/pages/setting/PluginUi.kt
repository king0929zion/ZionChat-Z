package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.datastore.PluginSettings
import me.rerere.rikkahub.data.datastore.Settings
import me.rerere.rikkahub.data.datastore.XToolPluginSettings
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionAccentBlue
import me.rerere.rikkahub.ui.theme.ZionGrayLighter
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import java.util.Locale

@Composable
internal fun PluginEntryIcon(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ZionAppIcons.Blocks,
            contentDescription = null,
            tint = ZionTextPrimary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
internal fun XToolsBrandIcon(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.x_logo_black),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
internal fun PluginRowTrailing(enabled: Boolean) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PluginStatePill(enabled = enabled)
        Icon(
            imageVector = ZionAppIcons.ChevronRight,
            contentDescription = null,
            tint = ZionTextSecondary,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
internal fun PluginStatePill(enabled: Boolean) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (enabled) Color(0xFFE8F4FD) else ZionGrayLighter)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (enabled) pluginText("已启用", "Enabled") else pluginText("已关闭", "Off"),
            color = if (enabled) ZionAccentBlue else ZionTextSecondary,
            fontFamily = SourceSans3,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        )
    }
}

internal fun updateTwitterPlugin(
    vm: SettingVM,
    settings: Settings,
    transform: XToolPluginSettings.() -> XToolPluginSettings,
) {
    vm.updateSettings(
        settings.copy(
            pluginSettings = PluginSettings(
                xTools = settings.pluginSettings.xTools.transform()
            )
        )
    )
}

internal fun pluginText(zh: String, en: String): String {
    return if (Locale.getDefault().language.equals("zh", ignoreCase = true)) zh else en
}
