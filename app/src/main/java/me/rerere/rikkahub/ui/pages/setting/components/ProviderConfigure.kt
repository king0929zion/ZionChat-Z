package me.rerere.rikkahub.ui.pages.setting.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.rerere.ai.provider.BalanceOption
import me.rerere.ai.provider.ProviderSetting
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.datastore.DEFAULT_PROVIDERS
import me.rerere.rikkahub.ui.theme.JetbrainsMono
import me.rerere.rikkahub.ui.theme.ZionAccentNeutral
import me.rerere.rikkahub.ui.theme.ZionGrayLight
import me.rerere.rikkahub.ui.theme.ZionGrayLighter
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import kotlin.reflect.KClass

@Composable
fun ProviderConfigure(
    provider: ProviderSetting,
    modifier: Modifier = Modifier,
    onEdit: (provider: ProviderSetting) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        if (!provider.builtIn) {
            ProviderTypeSelector(
                provider = provider,
                onEdit = onEdit
            )
        }

        when (provider) {
            is ProviderSetting.OpenAI -> ProviderConfigureOpenAI(provider, onEdit)
            is ProviderSetting.Google -> ProviderConfigureGoogle(provider, onEdit)
            is ProviderSetting.Claude -> ProviderConfigureClaude(provider, onEdit)
        }
    }
}

private fun ProviderSetting.withAlwaysEnabledDefaults(): ProviderSetting {
    return copyProvider(
        enabled = true,
        balanceOption = BalanceOption()
    )
}

@Composable
private fun ProviderTypeSelector(
    provider: ProviderSetting,
    onEdit: (ProviderSetting) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProviderSetting.Types.forEach { type ->
            val selected = provider::class == type
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = if (selected) ZionAccentNeutral else ZionGrayLighter,
                modifier = Modifier.clickable {
                    onEdit(provider.convertTo(type).withAlwaysEnabledDefaults())
                }
            ) {
                Text(
                    text = type.simpleName ?: "",
                    color = if (selected) Color.White else ZionTextPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun ProviderField(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    textStyle: TextStyle = TextStyle.Default,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    supportingText: String? = null,
) {
    val resolvedTextStyle = if (textStyle == TextStyle.Default) {
        MaterialTheme.typography.bodyLarge
    } else {
        textStyle
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            color = ZionTextSecondary,
            style = MaterialTheme.typography.labelMedium
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = if (singleLine) 56.dp else 112.dp),
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            textStyle = resolvedTextStyle.copy(
                color = ZionTextPrimary,
                fontSize = 16.sp
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = if (singleLine) ImeAction.Next else ImeAction.Default
            ),
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = if (isError) Color(0xFFFFF2F0) else ZionGrayLighter,
                unfocusedContainerColor = if (isError) Color(0xFFFFF2F0) else ZionGrayLighter,
                disabledContainerColor = ZionGrayLighter,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = ZionTextPrimary,
                unfocusedTextColor = ZionTextPrimary,
                cursorColor = ZionAccentNeutral,
                focusedPlaceholderColor = ZionTextSecondary,
                unfocusedPlaceholderColor = ZionTextSecondary
            ),
            placeholder = {
                Text(
                    text = title,
                    color = ZionTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            supportingText = supportingText?.let {
                {
                    Text(
                        text = it,
                        color = if (isError) Color(0xFFC62828) else ZionTextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        )
    }
}

@Composable
private fun ProviderToggleCard(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    supportingText: String? = null,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = ZionGrayLighter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    color = ZionTextPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (!supportingText.isNullOrBlank()) {
                    Text(
                        text = supportingText,
                        color = ZionTextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ZionAccentNeutral,
                    checkedBorderColor = Color.Transparent,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = ZionGrayLight,
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
    }
}

fun ProviderSetting.convertTo(type: KClass<out ProviderSetting>): ProviderSetting {
    if (this::class == type) {
        return this
    }

    val apiKey = when (this) {
        is ProviderSetting.OpenAI -> this.apiKey
        is ProviderSetting.Google -> this.apiKey
        is ProviderSetting.Claude -> this.apiKey
    }

    val sourceBaseUrl = when (this) {
        is ProviderSetting.OpenAI -> this.baseUrl
        is ProviderSetting.Google -> this.baseUrl
        is ProviderSetting.Claude -> this.baseUrl
    }
    val targetDefaultBaseUrl = when (type) {
        ProviderSetting.OpenAI::class -> ProviderSetting.OpenAI().baseUrl
        ProviderSetting.Google::class -> ProviderSetting.Google().baseUrl
        ProviderSetting.Claude::class -> ProviderSetting.Claude().baseUrl
        else -> error("Unsupported provider type: $type")
    }
    val convertedBaseUrl = sourceBaseUrl.convertToTargetBaseUrl(targetDefaultBaseUrl)

    return when (type) {
        ProviderSetting.OpenAI::class -> ProviderSetting.OpenAI(
            id = this.id,
            enabled = this.enabled,
            name = this.name,
            models = this.models,
            balanceOption = this.balanceOption,
            builtIn = this.builtIn,
            description = this.description,
            shortDescription = this.shortDescription,
            apiKey = apiKey,
            baseUrl = convertedBaseUrl
        )

        ProviderSetting.Google::class -> ProviderSetting.Google(
            id = this.id,
            enabled = this.enabled,
            name = this.name,
            models = this.models,
            balanceOption = this.balanceOption,
            builtIn = this.builtIn,
            description = this.description,
            shortDescription = this.shortDescription,
            apiKey = apiKey,
            baseUrl = convertedBaseUrl
        )

        ProviderSetting.Claude::class -> ProviderSetting.Claude(
            id = this.id,
            enabled = this.enabled,
            name = this.name,
            models = this.models,
            balanceOption = this.balanceOption,
            builtIn = this.builtIn,
            description = this.description,
            shortDescription = this.shortDescription,
            apiKey = apiKey,
            baseUrl = convertedBaseUrl
        )

        else -> error("Unsupported provider type: $type")
    }
}

internal fun ProviderSetting.defaultBaseUrlForReset(): String {
    val defaultProvider = DEFAULT_PROVIDERS.find { it.id == id }
    if (defaultProvider != null) {
        when (this) {
            is ProviderSetting.OpenAI -> if (defaultProvider is ProviderSetting.OpenAI) return defaultProvider.baseUrl
            is ProviderSetting.Google -> if (defaultProvider is ProviderSetting.Google) return defaultProvider.baseUrl
            is ProviderSetting.Claude -> if (defaultProvider is ProviderSetting.Claude) return defaultProvider.baseUrl
        }
    }

    return when (this) {
        is ProviderSetting.OpenAI -> ProviderSetting.OpenAI().baseUrl
        is ProviderSetting.Google -> ProviderSetting.Google().baseUrl
        is ProviderSetting.Claude -> ProviderSetting.Claude().baseUrl
    }
}

internal fun ProviderSetting.resetBaseUrlToDefault(): ProviderSetting {
    val defaultBaseUrl = defaultBaseUrlForReset()
    return when (this) {
        is ProviderSetting.OpenAI -> this.copy(baseUrl = defaultBaseUrl)
        is ProviderSetting.Google -> this.copy(baseUrl = defaultBaseUrl)
        is ProviderSetting.Claude -> this.copy(baseUrl = defaultBaseUrl)
    }
}

internal fun ProviderSetting.isUsingDefaultBaseUrl(): Boolean {
    val baseUrl = when (this) {
        is ProviderSetting.OpenAI -> this.baseUrl
        is ProviderSetting.Google -> this.baseUrl
        is ProviderSetting.Claude -> this.baseUrl
    }
    return baseUrl == defaultBaseUrlForReset()
}

private fun String.convertToTargetBaseUrl(targetDefaultBaseUrl: String): String {
    val sourceUrl = this.toHttpUrlOrNull() ?: return this
    val sourceHost = sourceUrl.host.lowercase()
    if (sourceHost in OFFICIAL_PROVIDER_HOSTS) {
        return targetDefaultBaseUrl
    }

    val targetUrl = targetDefaultBaseUrl.toHttpUrlOrNull() ?: return this
    val convertedPath = sourceUrl.encodedPath.convertToTargetPath(targetUrl.encodedPath)
    return sourceUrl.newBuilder()
        .encodedPath(convertedPath)
        .build()
        .toString()
}

private fun String.convertToTargetPath(targetPath: String): String {
    val source = this.normalizePath()
    val target = targetPath.normalizePath()

    val replaced = when {
        source.lowercase().endsWith(V1_BETA_SUFFIX) -> source.dropLast(V1_BETA_SUFFIX.length) + target
        source.lowercase().endsWith(V1_SUFFIX) -> source.dropLast(V1_SUFFIX.length) + target
        source.isBlank() -> target
        else -> source + target
    }

    return replaced.normalizePath()
}

private fun String.normalizePath(): String {
    val value = this.trim()
    if (value.isEmpty() || value == "/") {
        return ""
    }
    val path = if (value.startsWith("/")) value else "/$value"
    return path.trimEnd('/')
}

private const val OPENAI_OFFICIAL_HOST = "api.openai.com"
private const val GOOGLE_OFFICIAL_HOST = "generativelanguage.googleapis.com"
private const val CLAUDE_OFFICIAL_HOST = "api.anthropic.com"
private const val V1_SUFFIX = "/v1"
private const val V1_BETA_SUFFIX = "/v1beta"
private val OFFICIAL_PROVIDER_HOSTS = setOf(
    OPENAI_OFFICIAL_HOST,
    GOOGLE_OFFICIAL_HOST,
    CLAUDE_OFFICIAL_HOST
)

@Composable
private fun ColumnScope.ProviderConfigureOpenAI(
    provider: ProviderSetting.OpenAI,
    onEdit: (provider: ProviderSetting.OpenAI) -> Unit
) {
    val supportsResponseApi = provider.baseUrl.toHttpUrlOrNull()?.host?.lowercase() == OPENAI_OFFICIAL_HOST

    LaunchedEffect(provider.baseUrl, provider.useResponseApi) {
        if (!supportsResponseApi && provider.useResponseApi) {
            onEdit(provider.copy(useResponseApi = false, enabled = true, balanceOption = BalanceOption()))
        }
    }

    ProviderField(
        title = stringResource(id = R.string.setting_provider_page_name),
        value = provider.name,
        onValueChange = {
            onEdit(provider.copy(name = it.trim(), enabled = true, balanceOption = BalanceOption()))
        }
    )
    ProviderField(
        title = stringResource(id = R.string.setting_provider_page_api_key),
        value = provider.apiKey,
        onValueChange = {
            onEdit(provider.copy(apiKey = it.trim(), enabled = true, balanceOption = BalanceOption()))
        },
        singleLine = false,
        minLines = 1,
        maxLines = 4
    )
    ProviderField(
        title = stringResource(id = R.string.setting_provider_page_api_base_url),
        value = provider.baseUrl,
        onValueChange = {
            onEdit(provider.copy(baseUrl = it.trim(), enabled = true, balanceOption = BalanceOption()))
        },
        keyboardType = KeyboardType.Uri
    )
    if (!supportsResponseApi || !provider.useResponseApi) {
        ProviderField(
            title = stringResource(id = R.string.setting_provider_page_api_path),
            value = provider.chatCompletionsPath,
            onValueChange = {
                onEdit(provider.copy(chatCompletionsPath = it.trim(), enabled = true, balanceOption = BalanceOption()))
            },
            keyboardType = KeyboardType.Uri
        )
    }
    if (supportsResponseApi) {
        ProviderToggleCard(
            title = stringResource(id = R.string.setting_provider_page_response_api),
            checked = provider.useResponseApi,
            onCheckedChange = {
                onEdit(provider.copy(useResponseApi = it, enabled = true, balanceOption = BalanceOption()))
            }
        )
    }
}

@Composable
private fun ColumnScope.ProviderConfigureClaude(
    provider: ProviderSetting.Claude,
    onEdit: (provider: ProviderSetting.Claude) -> Unit
) {
    ProviderField(
        title = stringResource(id = R.string.setting_provider_page_name),
        value = provider.name,
        onValueChange = {
            onEdit(provider.copy(name = it.trim(), enabled = true, balanceOption = BalanceOption()))
        }
    )
    ProviderField(
        title = stringResource(id = R.string.setting_provider_page_api_key),
        value = provider.apiKey,
        onValueChange = {
            onEdit(provider.copy(apiKey = it.trim(), enabled = true, balanceOption = BalanceOption()))
        },
        singleLine = false,
        minLines = 1,
        maxLines = 4
    )
    ProviderField(
        title = stringResource(id = R.string.setting_provider_page_api_base_url),
        value = provider.baseUrl,
        onValueChange = {
            onEdit(provider.copy(baseUrl = it.trim(), enabled = true, balanceOption = BalanceOption()))
        },
        keyboardType = KeyboardType.Uri
    )
    ProviderToggleCard(
        title = stringResource(id = R.string.setting_provider_page_claude_prompt_caching),
        checked = provider.promptCaching,
        onCheckedChange = {
            onEdit(provider.copy(promptCaching = it, enabled = true, balanceOption = BalanceOption()))
        }
    )
}

@Composable
private fun ColumnScope.ProviderConfigureGoogle(
    provider: ProviderSetting.Google,
    onEdit: (provider: ProviderSetting.Google) -> Unit
) {
    ProviderField(
        title = stringResource(id = R.string.setting_provider_page_name),
        value = provider.name,
        onValueChange = {
            onEdit(provider.copy(name = it.trim(), enabled = true, balanceOption = BalanceOption()))
        }
    )
    ProviderToggleCard(
        title = stringResource(id = R.string.setting_provider_page_vertex_ai),
        checked = provider.vertexAI,
        onCheckedChange = {
            onEdit(provider.copy(vertexAI = it, enabled = true, balanceOption = BalanceOption()))
        }
    )

    if (!provider.vertexAI) {
        ProviderField(
            title = stringResource(id = R.string.setting_provider_page_api_key),
            value = provider.apiKey,
            onValueChange = {
                onEdit(provider.copy(apiKey = it.trim(), enabled = true, balanceOption = BalanceOption()))
            },
            singleLine = false,
            minLines = 1,
            maxLines = 4
        )

        ProviderField(
            title = stringResource(id = R.string.setting_provider_page_api_base_url),
            value = provider.baseUrl,
            onValueChange = {
                onEdit(provider.copy(baseUrl = it.trim(), enabled = true, balanceOption = BalanceOption()))
            },
            keyboardType = KeyboardType.Uri,
            isError = !provider.baseUrl.endsWith("/v1beta"),
            supportingText = if (!provider.baseUrl.endsWith("/v1beta")) "The base URL usually ends with `/v1beta`" else null
        )
    } else {
        ProviderField(
            title = stringResource(id = R.string.setting_provider_page_service_account_email),
            value = provider.serviceAccountEmail,
            onValueChange = {
                onEdit(provider.copy(serviceAccountEmail = it.trim(), enabled = true, balanceOption = BalanceOption()))
            }
        )
        ProviderField(
            title = stringResource(id = R.string.setting_provider_page_private_key),
            value = provider.privateKey,
            onValueChange = {
                onEdit(provider.copy(privateKey = it.trim(), enabled = true, balanceOption = BalanceOption()))
            },
            singleLine = false,
            minLines = 4,
            maxLines = 8,
            textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = JetbrainsMono)
        )
        ProviderField(
            title = stringResource(id = R.string.setting_provider_page_location),
            value = provider.location,
            onValueChange = {
                onEdit(provider.copy(location = it.trim(), enabled = true, balanceOption = BalanceOption()))
            }
        )
        ProviderField(
            title = stringResource(id = R.string.setting_provider_page_project_id),
            value = provider.projectId,
            onValueChange = {
                onEdit(provider.copy(projectId = it.trim(), enabled = true, balanceOption = BalanceOption()))
            }
        )
    }
}
