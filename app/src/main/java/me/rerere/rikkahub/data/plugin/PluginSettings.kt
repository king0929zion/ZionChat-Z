package me.rerere.rikkahub.data.plugin

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class PluginSettings(
    val x: XPluginConfig = XPluginConfig(),
    val telegram: TelegramPluginConfig = TelegramPluginConfig(),
) {
    fun enabledPluginCount(): Int = listOf(x.enabled, telegram.enabled).count { it }

    fun hasAnyEnabledTools(): Boolean = x.enabledToolCount() > 0
}

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

@Serializable
data class XPluginConfig(
    val enabled: Boolean = true,
    val readTimeline: Boolean = true,
    val readPostDetail: Boolean = true,
    val publishPost: Boolean = true,
    val replyPost: Boolean = true,
    val likePost: Boolean = true,
    val repostPost: Boolean = true,
    val bookmarkPost: Boolean = true,
    val botAutomationEnabled: Boolean = false,
    val botAutoPostEnabled: Boolean = false,
    val botReplyToUserPosts: Boolean = true,
    val botInteractWithOtherBots: Boolean = false,
    val botInteractionMode: XBotInteractionMode = XBotInteractionMode.Mixed,
    val botActivityLevel: XBotActivityLevel = XBotActivityLevel.Medium,
    val lastBotActivityAt: Long = 0L,
) {
    fun enabledToolCount(): Int {
        if (!enabled) return 0
        return XPluginTool.entries.count { rawToolEnabled(it) }
    }

    fun isToolEnabled(tool: XPluginTool): Boolean {
        return enabled && rawToolEnabled(tool)
    }

    fun rawToolEnabled(tool: XPluginTool): Boolean = when (tool) {
            XPluginTool.ReadTimeline -> readTimeline
            XPluginTool.ReadPostDetail -> readPostDetail
            XPluginTool.PublishPost -> publishPost
            XPluginTool.ReplyPost -> replyPost
            XPluginTool.LikePost -> likePost
            XPluginTool.RepostPost -> repostPost
            XPluginTool.BookmarkPost -> bookmarkPost
    }

    fun toggle(tool: XPluginTool, value: Boolean): XPluginConfig = when (tool) {
        XPluginTool.ReadTimeline -> copy(readTimeline = value)
        XPluginTool.ReadPostDetail -> copy(readPostDetail = value)
        XPluginTool.PublishPost -> copy(publishPost = value)
        XPluginTool.ReplyPost -> copy(replyPost = value)
        XPluginTool.LikePost -> copy(likePost = value)
        XPluginTool.RepostPost -> copy(repostPost = value)
        XPluginTool.BookmarkPost -> copy(bookmarkPost = value)
    }
}

@Serializable
enum class XBotInteractionMode {
    Reply,
    Quote,
    Mixed,
}

@Serializable
enum class XBotActivityLevel(
    val cooldownMinutes: Int,
) {
    Low(240),
    Medium(120),
    High(45),
}

enum class XPluginTool(
    val toolName: String,
    val title: String,
    val description: String,
    val parameterTags: List<String>,
    val requiresApproval: Boolean,
) {
    ReadTimeline(
        toolName = "read_x_home_timeline",
        title = "读取时间线",
        description = "读取首页时间线，帮助 AI 获取最新帖子和上下文。",
        parameterTags = listOf("limit", "tab"),
        requiresApproval = false,
    ),
    ReadPostDetail(
        toolName = "read_x_post_detail",
        title = "读取帖子详情",
        description = "读取指定帖子详情与回复线程，便于 AI 基于上下文继续操作。",
        parameterTags = listOf("post_id"),
        requiresApproval = false,
    ),
    PublishPost(
        toolName = "publish_x_post",
        title = "发布帖子",
        description = "代表用户发布新的 X 帖子。",
        parameterTags = listOf("text", "quote_post_id"),
        requiresApproval = true,
    ),
    ReplyPost(
        toolName = "reply_x_post",
        title = "回复帖子",
        description = "代表用户回复指定帖子。",
        parameterTags = listOf("post_id", "text"),
        requiresApproval = true,
    ),
    LikePost(
        toolName = "like_x_post",
        title = "点赞帖子",
        description = "为指定帖子点赞或取消点赞。",
        parameterTags = listOf("post_id"),
        requiresApproval = true,
    ),
    RepostPost(
        toolName = "repost_x_post",
        title = "转发帖子",
        description = "转发或取消转发指定帖子。",
        parameterTags = listOf("post_id"),
        requiresApproval = true,
    ),
    BookmarkPost(
        toolName = "bookmark_x_post",
        title = "收藏帖子",
        description = "收藏或取消收藏指定帖子。",
        parameterTags = listOf("post_id"),
        requiresApproval = true,
    ),
}
