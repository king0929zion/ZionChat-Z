package me.rerere.ai.provider.providers.copilot

import me.rerere.ai.provider.ProviderSetting

/**
 * GitHub Copilot 令牌供给接口
 *
 * Copilot 使用短时效令牌 (约 30 分钟), 每次请求前需要用长期 GitHub OAuth 令牌
 * 换取新的 Copilot 令牌。具体实现由 app 层提供 (网络请求 + 本地存储)。
 */
fun interface CopilotTokenProvider {
    /**
     * 返回可用的 Copilot 访问令牌
     *
     * @param providerId 供应商 id, 用于定位该供应商保存的 GitHub OAuth 令牌
     * @throws Exception 未登录或刷新失败
     */
    suspend fun accessToken(providerId: kotlin.uuid.Uuid): String
}

/** GitHub Copilot 默认供应商名称 (用于路由与 UI 判定) */
const val COPILOT_PROVIDER_NAME = "GitHub Copilot"

/** Copilot 默认 API 根地址 */
const val COPILOT_DEFAULT_BASE_URL = "https://api.individual.githubcopilot.com"

/**
 * Copilot 必需请求头
 *
 * GitHub 会校验客户端标识, 缺少这些头会返回 401/403。
 */
val COPILOT_CUSTOM_HEADERS: List<Pair<String, String>> = listOf(
    "User-Agent" to "GitHubCopilotChat/0.35.0",
    "Editor-Version" to "vscode/1.107.0",
    "Editor-Plugin-Version" to "copilot-chat/0.35.0",
    "Copilot-Integration-Id" to "vscode-chat",
)

/** 判断某个 OpenAI 兼容供应商是否为 GitHub Copilot */
fun ProviderSetting.isCopilot(): Boolean = name.trim() == COPILOT_PROVIDER_NAME