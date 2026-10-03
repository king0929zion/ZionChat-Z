package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.data.plugin.PluginStore
import me.rerere.rikkahub.data.plugin.StorePluginConfig
import me.rerere.rikkahub.data.plugin.StorePluginMcpSync
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.PillInput
import me.rerere.rikkahub.ui.components.ui.SectionLabel
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import org.koin.androidx.compose.koinViewModel

/**
 * 商店插件 (MCP 接入) 详情页: 启用开关 / Endpoint / API Key / 说明
 */
@Composable
fun PluginDetailPage(
    pluginId: String,
    vm: SettingVM = koinViewModel(),
) {
    val navController = LocalNavController.current
    val settings by vm.settings.collectAsStateWithLifecycle()
    val spec = remember(pluginId) { PluginStore.specOf(pluginId) }

    if (spec == null) {
        SettingsPage(
            title = "Plugin",
            onBack = { navController.popBackStack() },
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Plugin not found",
                    fontSize = 14.sp,
                    fontFamily = SourceSans3,
                    color = ZionTextSecondary,
                )
            }
        }
        return
    }

    val config = settings.pluginSettings.store.configOf(spec.id) ?: StorePluginConfig(
        enabled = true,
        endpoint = spec.defaultEndpoint,
        apiKey = ""
    )
    val installed = settings.pluginSettings.store.isInstalled(spec.id)
    val endpointValid = config.endpoint.isNotBlank() &&
        (!spec.requiresApiKey || config.apiKey.isNotBlank())

    SettingsPage(
        title = spec.name,
        onBack = { navController.popBackStack() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = PageTopBarContentTopPadding)
                .padding(horizontal = 16.dp),
        ) {
            // Enable 卡片
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (installed && config.enabled) ZionTextPrimary else Color.White)
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ZionAppIcons.Globe,
                    contentDescription = null,
                    tint = if (installed && config.enabled) Color.White else ZionTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = "Enable",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = SourceSans3,
                    color = if (installed && config.enabled) Color.White else ZionTextPrimary,
                    modifier = Modifier.weight(1f)
                )
                PluginSystemSwitch(
                    checked = installed && config.enabled,
                    onCheckedChange = { enabled ->
                        vm.updateSettings(
                            StorePluginMcpSync.apply(
                                settings,
                                spec,
                                config.copy(enabled = enabled)
                            )
                        )
                    }
                )
            }

            if (spec.description.isNotEmpty()) {
                Text(
                    text = spec.description,
                    fontSize = 12.sp,
                    fontFamily = SourceSans3,
                    color = ZionTextSecondary,
                    modifier = Modifier.padding(start = 4.dp, top = 12.dp, bottom = 2.dp)
                )
            }

            SectionLabel(text = "Endpoint URL")
            PillInput(
                value = config.endpoint,
                onValueChange = { endpoint ->
                    if (installed) {
                        vm.updateSettings(
                            StorePluginMcpSync.apply(
                                settings,
                                spec,
                                config.copy(endpoint = endpoint)
                            )
                        )
                    }
                },
                placeholder = "https://example.com/mcp",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Uri
            )

            if (spec.requiresApiKey) {
                SectionLabel(
                    text = if (spec.apiKeyAsBearerHeader) "API Token" else "API Key"
                )
                PillInput(
                    value = config.apiKey,
                    onValueChange = { key ->
                        if (installed) {
                            vm.updateSettings(
                                StorePluginMcpSync.apply(
                                    settings,
                                    spec,
                                    config.copy(apiKey = key)
                                )
                            )
                        }
                    },
                    placeholder = if (spec.apiKeyAsBearerHeader) {
                        "ghp_xxxx (Personal Access Token)"
                    } else {
                        "API Key"
                    },
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Password
                )
            }

            if (installed && config.enabled && !endpointValid) {
                Text(
                    text = if (config.endpoint.isBlank()) {
                        "请填入 MCP 端点后再启用"
                    } else {
                        "请填入 ${if (spec.apiKeyAsBearerHeader) "Token" else "API Key"} 后再启用"
                    },
                    fontSize = 12.sp,
                    fontFamily = SourceSans3,
                    color = ZionTextSecondary,
                    modifier = Modifier.padding(start = 4.dp, top = 10.dp)
                )
            }

            // 卸载
            if (installed) {
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFFF5F5F5))
                        .pressableScale(pressedScale = 0.97f) {
                            vm.updateSettings(StorePluginMcpSync.uninstall(settings, spec))
                            navController.popBackStack()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Uninstall",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = SourceSans3,
                        color = ZionTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
