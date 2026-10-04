package me.rerere.ai.provider.providers.copilot

import kotlinx.coroutines.flow.Flow
import me.rerere.ai.provider.CustomHeader
import me.rerere.ai.provider.ImageGenerationParams
import me.rerere.ai.provider.Model
import me.rerere.ai.provider.Provider
import me.rerere.ai.provider.ProviderSetting
import me.rerere.ai.provider.TextGenerationParams
import me.rerere.ai.provider.TextGenerationResult
import me.rerere.ai.ui.UIMessage
import me.rerere.ai.ui.ImageGenerationItem
import me.rerere.ai.ui.StreamChunk

/**
 * GitHub Copilot 供应商
 *
 * Copilot 的 API 与 OpenAI 兼容, 差异只在两点:
 * 1. 令牌短时效, 每次调用前需要刷新 (由 [CopilotTokenProvider] 负责)
 * 2. 必须携带 Copilot 客户端标识请求头
 *
 * 这里统一在调用前注入新鲜令牌与请求头, 其余逻辑完全复用 [OpenAIProvider]。
 */
class CopilotProvider(
    private val openAIProvider: Provider<ProviderSetting.OpenAI>,
    private val tokenProvider: CopilotTokenProvider,
) : Provider<ProviderSetting.OpenAI> {

    /** 生成带新鲜令牌与 Copilot 请求头的设置副本 */
    private suspend fun withFreshToken(
        providerSetting: ProviderSetting.OpenAI
    ): ProviderSetting.OpenAI {
        val token = tokenProvider.accessToken(providerSetting.id)
        return providerSetting.copy(
            apiKey = token,
            customHeaders = COPILOT_CUSTOM_HEADERS.map { (name, value) ->
                CustomHeader(name = name, value = value)
            },
        )
    }

    override suspend fun listModels(providerSetting: ProviderSetting.OpenAI): List<Model> =
        openAIProvider.listModels(withFreshToken(providerSetting))

    override suspend fun getBalance(providerSetting: ProviderSetting.OpenAI): String =
        openAIProvider.getBalance(withFreshToken(providerSetting))

    override suspend fun generateText(
        providerSetting: ProviderSetting.OpenAI,
        messages: List<UIMessage>,
        params: TextGenerationParams,
    ): TextGenerationResult = openAIProvider.generateText(
        withFreshToken(providerSetting),
        messages,
        params,
    )

    override suspend fun streamText(
        providerSetting: ProviderSetting.OpenAI,
        messages: List<UIMessage>,
        params: TextGenerationParams,
    ): Flow<StreamChunk> = openAIProvider.streamText(
        withFreshToken(providerSetting),
        messages,
        params,
    )

    override suspend fun generateImage(
        providerSetting: ProviderSetting,
        params: ImageGenerationParams,
    ): Flow<ImageGenerationItem> {
        val openAI = providerSetting as? ProviderSetting.OpenAI
            ?: error("Copilot requires an OpenAI-compatible provider setting")
        return openAIProvider.generateImage(withFreshToken(openAI), params)
    }
}