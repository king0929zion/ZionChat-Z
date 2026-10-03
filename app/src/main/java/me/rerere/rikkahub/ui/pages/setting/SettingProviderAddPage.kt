package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.ai.provider.ProviderSetting
import me.rerere.rikkahub.ui.components.ui.HeaderActionButton
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.PillInput
import me.rerere.rikkahub.ui.components.ui.PillSelect
import me.rerere.rikkahub.ui.components.ui.SectionLabel
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.settingsBottomInsets
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.Uuid

/**
 * API 类型选项复用 [ProviderApiTypeOption] (定义于 SettingProviderDetailPage.kt)
 */

/**
 * 新增模型服务页面 (设计稿: Add model service)
 *
 * Service name / API type / OpenAI API / Base URL / API Key,
 * 底部 Create & enable 一键创建并启用; 右上角支持扫码预填。
 */
@Composable
fun SettingProviderAddPage(
    apiType: String = "",
    vm: SettingVM = koinViewModel(),
) {
    val settings by vm.settings.collectAsStateWithLifecycle()
    val navController = LocalNavController.current
    val initialType = remember(apiType) {
        when (apiType.lowercase()) {
            "google" -> ProviderApiTypeOption.GEMINI
            "claude", "anthropic" -> ProviderApiTypeOption.ANTHROPIC
            "compatible", "openai compatible" -> ProviderApiTypeOption.COMPATIBLE
            else -> ProviderApiTypeOption.OPENAI
        }
    }

    var serviceName by remember(initialType) { mutableStateOf("") }
    var apiTypeOption by remember { mutableStateOf(initialType) }
    var useResponseApi by remember { mutableStateOf(false) }
    var baseUrl by remember(initialType) { mutableStateOf(initialType.defaultBaseUrl) }
    var apiKey by remember { mutableStateOf("") }

    fun applyType(newType: ProviderApiTypeOption) {
        val previous = apiTypeOption
        apiTypeOption = newType
        // Base URL 为空或仍是上一个类型的默认值时, 跟随类型切换为新的默认值
        if (baseUrl.isBlank() || baseUrl == previous.defaultBaseUrl) {
            baseUrl = newType.defaultBaseUrl
        }
    }

    // 右上角 ✅ 保存: 创建并启用服务后返回
    fun saveProvider() {
        val provider = buildProvider(
            apiTypeOption = apiTypeOption,
            serviceName = serviceName,
            baseUrl = baseUrl,
            apiKey = apiKey,
            useResponseApi = useResponseApi
        )
        vm.updateSettings(
            settings.copy(
                providers = listOf(provider.copyProvider(id = Uuid.random())) + settings.providers
            )
        )
        navController.popBackStack()
    }

    SettingsPage(
        title = "Add model service",
        onBack = { navController.popBackStack() },
        trailing = {
            HeaderActionButton(
                onClick = { saveProvider() },
                icon = ZionAppIcons.Check,
                contentDescription = "Save"
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = PageTopBarContentTopPadding)
                .padding(horizontal = 16.dp)
                .settingsBottomInsets()
        ) {
            SectionLabel(text = "Service name")
            PillInput(
                value = serviceName,
                onValueChange = { serviceName = it },
                placeholder = "e.g. custom service"
            )

            SectionLabel(text = "API type")
            PillSelect(
                options = ProviderApiTypeOption.entries,
                selectedOption = apiTypeOption,
                onOptionSelected = { applyType(it) },
                optionToString = { it.label },
                modifier = Modifier.fillMaxWidth()
            )

            // OpenAI API: 仅 OpenAI 兼容类型可切换
            val openAiSelectEnabled = apiTypeOption.isOpenAiCompatible
            SectionLabel(text = "OpenAI API")
            PillSelect(
                options = listOf(false, true),
                selectedOption = useResponseApi,
                onOptionSelected = { if (openAiSelectEnabled) useResponseApi = it },
                optionToString = { if (it) "Responses API" else "Chat Completions API" },
                enabled = openAiSelectEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (openAiSelectEnabled) 1f else 0.45f)
            )

            SectionLabel(text = "Base URL")
            PillInput(
                value = baseUrl,
                onValueChange = { baseUrl = it },
                placeholder = "https://api.example.com/v1",
                keyboardType = KeyboardType.Uri
            )

            SectionLabel(text = "API Key")
            PillInput(
                value = apiKey,
                onValueChange = { apiKey = it },
                placeholder = "Fill to enable, or save and add later",
                keyboardType = KeyboardType.Password
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

private fun buildProvider(
    apiTypeOption: ProviderApiTypeOption,
    serviceName: String,
    baseUrl: String,
    apiKey: String,
    useResponseApi: Boolean,
): ProviderSetting {
    val name = serviceName.ifBlank { apiTypeOption.defaultName }
    return when (apiTypeOption) {
        ProviderApiTypeOption.OPENAI, ProviderApiTypeOption.COMPATIBLE -> ProviderSetting.OpenAI(
            name = name,
            baseUrl = baseUrl,
            apiKey = apiKey,
            useResponseApi = useResponseApi
        )

        ProviderApiTypeOption.ANTHROPIC -> ProviderSetting.Claude(
            name = name,
            baseUrl = baseUrl,
            apiKey = apiKey
        )

        ProviderApiTypeOption.GEMINI -> ProviderSetting.Google(
            name = name,
            baseUrl = baseUrl,
            apiKey = apiKey
        )
    }
}
