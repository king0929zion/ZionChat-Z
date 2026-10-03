package me.rerere.rikkahub.data.plugin

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

/**
 * 插件中心总配置
 *
 * - telegram: 内置 Telegram Bot 插件
 * - acRemote: 内置空调红外遥控插件
 * - store: 商店插件 (MCP 接入) 的安装与配置
 */
@Serializable
data class PluginSettings(
    val telegram: TelegramPluginConfig = TelegramPluginConfig(),
    val acRemote: AcRemoteConfig = AcRemoteConfig(),
    val store: StorePluginSettings = StorePluginSettings(),
) {
    fun enabledPluginCount(): Int =
        listOf(telegram.enabled, acRemote.enabled).count { it } +
            store.plugins.values.count { it.enabled }

    fun hasAnyEnabledTools(): Boolean = acRemote.enabled || store.plugins.values.any { it.enabled }
}

// ------------------------- Telegram -------------------------

@Serializable
data class TelegramPluginConfig(
    val enabled: Boolean = false,
    val botToken: String = "",
    val allowedUsersRaw: String = "",
    val modelId: Uuid? = null,
    val lastUpdateId: Long = 0L,
    val sessions: List<TelegramChatSession> = emptyList(),
) {
    fun hasToken(): Boolean = botToken.trim().isNotEmpty()

    fun normalizedAllowedEntries(): List<String> {
        return allowedUsersRaw
            .lineSequence()
            .flatMap { line ->
                line.split(',', '，', ' ', '\t')
                    .asSequence()
            }
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .toList()
    }

    fun allowedIdentityCount(): Int = normalizedAllowedEntries().size

    fun hasAllowedUsers(): Boolean = allowedIdentityCount() > 0

    fun sessionFor(chatId: Long): TelegramChatSession? {
        return sessions.firstOrNull { it.chatId == chatId }
    }

    fun upsertSession(
        chatId: Long,
        title: String,
        transform: (TelegramChatSession) -> TelegramChatSession,
    ): TelegramPluginConfig {
        val base = sessionFor(chatId) ?: TelegramChatSession(
            chatId = chatId,
            title = title,
        )
        val transformed = transform(base)
        val updated = transformed.copy(
            title = title.ifBlank { base.title },
            updatedAt = System.currentTimeMillis(),
            messages = transformed.messages
                .takeLast(12)
                .map { message -> message.copy(text = message.text.take(1200)) }
        )
        val nextSessions = sessions
            .filterNot { it.chatId == chatId }
            .plus(updated)
            .sortedByDescending { it.updatedAt }
            .take(8)
        return copy(sessions = nextSessions)
    }
}

@Serializable
data class TelegramChatSession(
    val chatId: Long,
    val title: String = "",
    val updatedAt: Long = 0L,
    val messages: List<TelegramChatMessage> = emptyList(),
)

@Serializable
data class TelegramChatMessage(
    val role: TelegramChatRole,
    val text: String,
    val createdAt: Long = System.currentTimeMillis(),
)

@Serializable
enum class TelegramChatRole {
    User,
    Assistant,
}

// ------------------------- 空调红外遥控 -------------------------

/** 支持红外遥控的空调品牌 */
@Serializable
enum class AcRemoteBrand {
    /** 格力 (YAW1F / YBOFB 系列遥控协议) */
    GREE,
}

/** 空调运行模式 */
@Serializable
enum class AcRemoteMode {
    AUTO,
    COOL,
    DRY,
    FAN,
    HEAT,
}

/** 风速 */
@Serializable
enum class AcRemoteFan {
    AUTO,
    LOW,
    MEDIUM,
    HIGH,
}

@Serializable
data class AcRemoteConfig(
    val enabled: Boolean = false,
    val brand: AcRemoteBrand = AcRemoteBrand.GREE,
    val power: Boolean = false,
    val temperature: Int = 26,
    val mode: AcRemoteMode = AcRemoteMode.COOL,
    val fan: AcRemoteFan = AcRemoteFan.AUTO,
) {
    companion object {
        const val MIN_TEMPERATURE = 16
        const val MAX_TEMPERATURE = 30
    }
}

// ------------------------- 商店插件 (MCP) -------------------------

@Serializable
data class StorePluginSettings(
    /** pluginId -> 配置; 存在于 map 即视为已安装 */
    val plugins: Map<String, StorePluginConfig> = emptyMap(),
) {
    fun isInstalled(pluginId: String): Boolean = plugins.containsKey(pluginId)

    fun configOf(pluginId: String): StorePluginConfig? = plugins[pluginId]

    fun installedCount(): Int = plugins.size
}

@Serializable
data class StorePluginConfig(
    val enabled: Boolean = true,
    /** MCP Server 端点 (支持 {KEY} 占位符, 会被 apiKey 替换) */
    val endpoint: String = "",
    /** API Key / Token; 对 GitHub 会作为 Bearer 头发送 */
    val apiKey: String = "",
)
