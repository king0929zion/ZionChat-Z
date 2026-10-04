package me.rerere.ai.provider

import android.content.Context
import me.rerere.ai.provider.providers.claude.ClaudeProvider
import me.rerere.ai.provider.providers.copilot.CopilotProvider
import me.rerere.ai.provider.providers.copilot.CopilotTokenProvider
import me.rerere.ai.provider.providers.copilot.isCopilot
import me.rerere.ai.provider.providers.google.GoogleProvider
import me.rerere.ai.provider.providers.openai.OpenAIProvider
import okhttp3.OkHttpClient

/**
 * Provider管理器，负责注册和获取Provider实例
 */
class ProviderManager(
    client: OkHttpClient,
    context: Context,
    copilotTokenProvider: CopilotTokenProvider? = null,
) {
    // 存储已注册的Provider实例
    private val providers = mutableMapOf<String, Provider<*>>()

    init {
        // 注册默认Provider
        val openAIProvider = OpenAIProvider(client, context)
        registerProvider("openai", openAIProvider)
        registerProvider("google", GoogleProvider(client, context))
        registerProvider("claude", ClaudeProvider(client, context))
        // GitHub Copilot: OpenAI 兼容 + 短时效令牌刷新
        if (copilotTokenProvider != null) {
            registerProvider(
                "copilot",
                CopilotProvider(openAIProvider, copilotTokenProvider)
            )
        }
    }

    /**
     * 注册Provider实例
     *
     * @param name Provider名称
     * @param provider Provider实例
     */
    fun registerProvider(name: String, provider: Provider<*>) {
        providers[name] = provider
    }

    /**
     * 获取Provider实例
     *
     * @param name Provider名称
     * @return Provider实例，如果不存在则返回null
     */
    fun getProvider(name: String): Provider<*> {
        return providers[name] ?: throw IllegalArgumentException("Provider not found: $name")
    }

    /**
     * 根据ProviderSetting获取对应的Provider实例
     *
     * @param setting Provider设置
     * @return Provider实例，如果不存在则返回null
     */
    fun <T : ProviderSetting> getProviderByType(setting: T): Provider<T> {
        @Suppress("UNCHECKED_CAST")
        return when (setting) {
            // GitHub Copilot 复用 OpenAI 协议, 但需要刷新短时效令牌
            is ProviderSetting.OpenAI ->
                if (setting.isCopilot() && providers.containsKey("copilot")) {
                    getProvider("copilot")
                } else {
                    getProvider("openai")
                }

            is ProviderSetting.Google -> getProvider("google")
            is ProviderSetting.Claude -> getProvider("claude")
        } as Provider<T>
    }
}
