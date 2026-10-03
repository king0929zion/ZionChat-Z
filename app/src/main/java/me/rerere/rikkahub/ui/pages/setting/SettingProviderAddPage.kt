package me.rerere.rikkahub.ui.pages.setting

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dokar.sonner.ToastType
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode
import androidx.activity.compose.rememberLauncherForActivityResult
import me.rerere.ai.provider.ProviderSetting
import me.rerere.rikkahub.R
import me.rerere.rikkahub.ui.components.ui.HeaderActionButton
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.PillInput
import me.rerere.rikkahub.ui.components.ui.SectionLabel
import me.rerere.rikkahub.ui.components.ui.Select
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.decodeProviderSetting
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.components.ui.settingsBottomInsets
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.context.LocalToaster
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.Uuid

/**
 * API 类型选项 (设计稿: API type 下拉)
 *
 * [ProviderSetting] 只有三种实现: OpenAI / Claude / Google。
 * "OpenAI Compatible" 同样映射到 OpenAI 类型, 但默认不预填官方 Base URL。
 */
private enum class ApiTypeOption(
    val label: String,
    val defaultBaseUrl: String,
    val defaultName: String,
) {
    OPENAI("OpenAI", "https://api.openai.com/v1", "OpenAI"),
    COMPATIBLE("OpenAI Compatible", "", "Custom Service"),
    ANTHROPIC("Anthropic", "https://api.anthropic.com/v1", "Claude"),
    GEMINI("Gemini", "https://generativelanguage.googleapis.com/v1beta", "Google");
}

private fun ApiTypeOption.isOpenAiCompatible(): Boolean =
    this == ApiTypeOption.OPENAI || this == ApiTypeOption.COMPATIBLE

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
    val toaster = LocalToaster.current
    val context = LocalContext.current

    val initialType = remember(apiType) {
        when (apiType.lowercase()) {
            "google" -> ApiTypeOption.GEMINI
            "claude", "anthropic" -> ApiTypeOption.ANTHROPIC
            "compatible", "openai compatible" -> ApiTypeOption.COMPATIBLE
            else -> ApiTypeOption.OPENAI
        }
    }

    var serviceName by remember(initialType) { mutableStateOf("") }
    var apiTypeOption by remember { mutableStateOf(initialType) }
    var useResponseApi by remember { mutableStateOf(false) }
    var baseUrl by remember(initialType) { mutableStateOf(initialType.defaultBaseUrl) }
    var apiKey by remember { mutableStateOf("") }

    fun applyType(newType: ApiTypeOption) {
        val previous = apiTypeOption
        apiTypeOption = newType
        // Base URL 为空或仍是上一个类型的默认值时, 跟随类型切换为新的默认值
        if (baseUrl.isBlank() || baseUrl == previous.defaultBaseUrl) {
            baseUrl = newType.defaultBaseUrl
        }
    }

    val scanQrCodeLauncher = rememberLauncherForActivityResult(ScanQRCode()) { result ->
        handleScanResult(
            result = result,
            context = context,
            toaster = toaster,
            onPrefill = { setting ->
                apiTypeOption = when (setting) {
                    is ProviderSetting.Claude -> ApiTypeOption.ANTHROPIC
                    is ProviderSetting.Google -> ApiTypeOption.GEMINI
                    is ProviderSetting.OpenAI -> {
                        if (setting.baseUrl == ApiTypeOption.OPENAI.defaultBaseUrl) {
                            ApiTypeOption.OPENAI
                        } else {
                            ApiTypeOption.COMPATIBLE
                        }
                    }
                }
                serviceName = setting.name
                baseUrl = when (setting) {
                    is ProviderSetting.OpenAI -> setting.baseUrl
                    is ProviderSetting.Claude -> setting.baseUrl
                    is ProviderSetting.Google -> setting.baseUrl
                }
                apiKey = when (setting) {
                    is ProviderSetting.OpenAI -> setting.apiKey
                    is ProviderSetting.Claude -> setting.apiKey
                    is ProviderSetting.Google -> setting.apiKey
                }
                if (setting is ProviderSetting.OpenAI) {
                    useResponseApi = setting.useResponseApi
                }
            }
        )
    }

    SettingsPage(
        title = "Add model service",
        onBack = { navController.popBackStack() },
        trailing = {
            HeaderActionButton(
                onClick = { scanQrCodeLauncher.launch(null) },
                icon = ZionAppIcons.Camera,
                contentDescription = "Scan QR code"
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
            Select(
                options = ApiTypeOption.entries,
                selectedOption = apiTypeOption,
                onOptionSelected = { applyType(it) },
                optionToString = { it.label },
                modifier = Modifier.fillMaxWidth()
            )

            // OpenAI API: 仅 OpenAI 兼容类型可切换
            val openAiSelectEnabled = apiTypeOption.isOpenAiCompatible()
            SectionLabel(text = "OpenAI API")
            Select(
                options = listOf(false, true),
                selectedOption = useResponseApi,
                onOptionSelected = { if (openAiSelectEnabled) useResponseApi = it },
                optionToString = { if (it) "Responses API" else "Chat Completions API" },
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

            Spacer(modifier = Modifier.height(24.dp))

            // Create & enable
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(ZionTextPrimary, RoundedCornerShape(25.dp))
                    .pressableScale(pressedScale = 0.97f) {
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
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Create & enable",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = SourceSans3,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

private fun buildProvider(
    apiTypeOption: ApiTypeOption,
    serviceName: String,
    baseUrl: String,
    apiKey: String,
    useResponseApi: Boolean,
): ProviderSetting {
    val name = serviceName.ifBlank { apiTypeOption.defaultName }
    return when (apiTypeOption) {
        ApiTypeOption.OPENAI, ApiTypeOption.COMPATIBLE -> ProviderSetting.OpenAI(
            name = name,
            baseUrl = baseUrl,
            apiKey = apiKey,
            useResponseApi = useResponseApi
        )

        ApiTypeOption.ANTHROPIC -> ProviderSetting.Claude(
            name = name,
            baseUrl = baseUrl,
            apiKey = apiKey
        )

        ApiTypeOption.GEMINI -> ProviderSetting.Google(
            name = name,
            baseUrl = baseUrl,
            apiKey = apiKey
        )
    }
}

private fun handleScanResult(
    result: QRResult,
    context: Context,
    toaster: com.dokar.sonner.ToasterState,
    onPrefill: (ProviderSetting) -> Unit,
) {
    runCatching {
        when (result) {
            is QRResult.QRError -> {
                toaster.show(
                    context.getString(R.string.setting_provider_page_scan_error, result),
                    type = ToastType.Error
                )
            }

            QRResult.QRMissingPermission -> {
                toaster.show(
                    context.getString(R.string.setting_provider_page_no_permission),
                    type = ToastType.Error
                )
            }

            is QRResult.QRSuccess -> {
                val setting = decodeProviderSetting(result.content.rawValue ?: "")
                onPrefill(setting)
                toaster.show(
                    context.getString(R.string.setting_provider_page_import_success),
                    type = ToastType.Success
                )
            }

            QRResult.QRUserCanceled -> {}
        }
    }.onFailure { error ->
        toaster.show(
            context.getString(R.string.setting_provider_page_qr_decode_failed, error.message ?: ""),
            type = ToastType.Error
        )
    }
}
