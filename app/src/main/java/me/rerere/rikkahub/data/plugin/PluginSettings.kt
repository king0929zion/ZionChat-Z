package me.rerere.rikkahub.data.plugin

import kotlinx.serialization.Serializable

@Serializable
data class PluginSettings(
    val x: XPluginConfig = XPluginConfig(),
) {
    fun enabledPluginCount(): Int = listOf(x.enabled).count { it }

    fun hasAnyEnabledTools(): Boolean = x.enabledToolCount() > 0
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
