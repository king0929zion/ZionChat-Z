package me.rerere.rikkahub.data.ai.tools

import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import me.rerere.ai.core.InputSchema
import me.rerere.ai.core.Tool
import me.rerere.ai.ui.UIMessagePart
import me.rerere.rikkahub.data.plugin.XPluginConfig
import me.rerere.rikkahub.data.plugin.XPluginTool
import me.rerere.rikkahub.data.x.XPost
import me.rerere.rikkahub.data.x.XPostSource
import me.rerere.rikkahub.data.x.XResolvedPost
import me.rerere.rikkahub.data.x.XTimelineRepository

class XPluginTools(
    private val repository: XTimelineRepository,
) {
    fun getTools(config: XPluginConfig): List<Tool> {
        if (!config.enabled) return emptyList()
        return buildList {
            if (config.readTimeline) add(readTimelineTool)
            if (config.readPostDetail) add(readPostDetailTool)
            if (config.publishPost) add(publishPostTool)
            if (config.replyPost) add(replyPostTool)
            if (config.likePost) add(likePostTool)
            if (config.repostPost) add(repostPostTool)
            if (config.bookmarkPost) add(bookmarkPostTool)
        }
    }

    private val readTimelineTool = Tool(
        name = XPluginTool.ReadTimeline.toolName,
        description = "Read the current X home timeline and return a concise list of posts with IDs, authors, counts and snippets.",
        parameters = {
            InputSchema.Obj(
                properties = buildJsonObject {
                    put("limit", integerField("Maximum number of posts to return. Default 6."))
                    put("tab", stringField("Optional feed tab hint such as for_you, following, ai_watch."))
                }
            )
        },
        execute = { args ->
            val limit = args.jsonObject["limit"]?.jsonPrimitive?.intOrNull?.coerceIn(1, 12) ?: 6
            val tab = args.jsonObject["tab"]?.jsonPrimitive?.contentOrNull
            val posts = repository.topLevelPosts()
                .filter { resolved ->
                    when (tab?.trim()?.lowercase()) {
                        "following" -> resolved.post.tags.any { it.equals("Following", ignoreCase = true) }
                        "ai_watch", "ai" -> resolved.post.tags.any { it.equals("AI Watch", ignoreCase = true) }
                        else -> true
                    }
                }
                .take(limit)
            listOf(
                UIMessagePart.Text(
                    buildJsonObject {
                        put("summary", "已返回 ${posts.size} 条 X 时间线帖子")
                        put(
                            "posts",
                            buildJsonArray {
                                posts.forEach { add(it.toJson()) }
                            }
                        )
                    }.toString()
                )
            )
        }
    )

    private val readPostDetailTool = Tool(
        name = XPluginTool.ReadPostDetail.toolName,
        description = "Read a specific X post and its reply thread using a post_id from the timeline.",
        parameters = {
            InputSchema.Obj(
                properties = buildJsonObject {
                    put("post_id", stringField("Required. ID of the post to inspect."))
                },
                required = listOf("post_id")
            )
        },
        execute = { args ->
            val postId = args.requireString("post_id")
            repository.recordView(postId)
            val post = repository.resolvePost(postId) ?: error("post not found: $postId")
            val replies = repository.repliesFor(postId)
            listOf(
                UIMessagePart.Text(
                    buildJsonObject {
                        put("summary", "已读取帖子详情，包含 ${replies.size} 条回复")
                        put("post", post.toJson())
                        put(
                            "replies",
                            buildJsonArray {
                                replies.forEach { add(it.toJson()) }
                            }
                        )
                    }.toString()
                )
            )
        }
    )

    private val publishPostTool = Tool(
        name = XPluginTool.PublishPost.toolName,
        description = "Publish a new X post for the current user. Use quote_post_id only when explicitly quoting another post.",
        parameters = {
            InputSchema.Obj(
                properties = buildJsonObject {
                    put("text", stringField("Required. Final post body to publish."))
                    put("quote_post_id", stringField("Optional. Existing post ID to quote."))
                },
                required = listOf("text")
            )
        },
        needsApproval = XPluginTool.PublishPost.requiresApproval,
        execute = { args ->
            val text = args.requireString("text")
            val quotePostId = args.jsonObject["quote_post_id"]?.jsonPrimitive?.contentOrNull
            val post = repository.publishPost(
                text = text,
                quotePostId = quotePostId,
                source = XPostSource.AI_ASSISTANT
            )
            listOf(UIMessagePart.Text(postMutationPayload("已发布 X 帖子", post).toString()))
        }
    )

    private val replyPostTool = Tool(
        name = XPluginTool.ReplyPost.toolName,
        description = "Reply to an existing X post on behalf of the current user.",
        parameters = {
            InputSchema.Obj(
                properties = buildJsonObject {
                    put("post_id", stringField("Required. ID of the target post."))
                    put("text", stringField("Required. Reply body to send."))
                },
                required = listOf("post_id", "text")
            )
        },
        needsApproval = XPluginTool.ReplyPost.requiresApproval,
        execute = { args ->
            val postId = args.requireString("post_id")
            val text = args.requireString("text")
            val post = repository.replyToPost(
                postId = postId,
                text = text,
                source = XPostSource.AI_ASSISTANT
            )
            listOf(UIMessagePart.Text(postMutationPayload("已回复 X 帖子", post).toString()))
        }
    )

    private val likePostTool = Tool(
        name = XPluginTool.LikePost.toolName,
        description = "Toggle like on an X post.",
        parameters = {
            InputSchema.Obj(
                properties = buildJsonObject {
                    put("post_id", stringField("Required. ID of the target post."))
                },
                required = listOf("post_id")
            )
        },
        needsApproval = XPluginTool.LikePost.requiresApproval,
        execute = { args ->
            val post = repository.toggleLike(args.requireString("post_id"))
            listOf(UIMessagePart.Text(togglePayload("点赞", post.id, post.likedByMe).toString()))
        }
    )

    private val repostPostTool = Tool(
        name = XPluginTool.RepostPost.toolName,
        description = "Toggle repost on an X post.",
        parameters = {
            InputSchema.Obj(
                properties = buildJsonObject {
                    put("post_id", stringField("Required. ID of the target post."))
                },
                required = listOf("post_id")
            )
        },
        needsApproval = XPluginTool.RepostPost.requiresApproval,
        execute = { args ->
            val post = repository.toggleRepost(args.requireString("post_id"))
            listOf(UIMessagePart.Text(togglePayload("转发", post.id, post.repostedByMe).toString()))
        }
    )

    private val bookmarkPostTool = Tool(
        name = XPluginTool.BookmarkPost.toolName,
        description = "Toggle bookmark on an X post.",
        parameters = {
            InputSchema.Obj(
                properties = buildJsonObject {
                    put("post_id", stringField("Required. ID of the target post."))
                },
                required = listOf("post_id")
            )
        },
        needsApproval = XPluginTool.BookmarkPost.requiresApproval,
        execute = { args ->
            val post = repository.toggleBookmark(args.requireString("post_id"))
            listOf(UIMessagePart.Text(togglePayload("收藏", post.id, post.bookmarkedByMe).toString()))
        }
    )

    private fun postMutationPayload(summary: String, post: XPost) = buildJsonObject {
        put("summary", summary)
        put("post_id", post.id)
        put("text", post.body)
    }

    private fun togglePayload(action: String, postId: String, enabled: Boolean) = buildJsonObject {
        put("summary", if (enabled) "已${action}帖子" else "已取消${action}")
        put("post_id", postId)
        put("active", enabled)
    }

    private fun integerField(description: String) = buildJsonObject {
        put("type", "integer")
        put("description", description)
    }

    private fun stringField(description: String) = buildJsonObject {
        put("type", "string")
        put("description", description)
    }

    private fun kotlinx.serialization.json.JsonElement.requireString(key: String): String {
        return jsonObject[key]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
            ?: error("$key is required")
    }

    private fun XResolvedPost.toJson(): kotlinx.serialization.json.JsonObject = buildJsonObject {
        put("id", post.id)
        put("author_name", author.displayName)
        put("author_handle", author.handle)
        put("text", post.body)
        put("created_at_ms", post.createdAtMillis)
        put("likes", post.likeCount)
        put("replies", post.replyCount)
        put("reposts", post.repostCount)
        put("bookmarks", post.bookmarkCount)
        put("views", post.viewCount)
        put(
            "source",
            when (post.source) {
                XPostSource.USER -> JsonPrimitive("user")
                XPostSource.AI_ASSISTANT -> JsonPrimitive("ai_assistant")
                XPostSource.SYSTEM -> JsonPrimitive("system")
            }
        )
        put(
            "tags",
            buildJsonArray {
                post.tags.forEach { add(JsonPrimitive(it)) }
            }
        )
        quotedPost?.let { quoted ->
            put("quoted_post", quoted.toJson())
        }
        if (post.media.isNotEmpty()) {
            put(
                "media",
                buildJsonArray {
                    post.media.forEach { media ->
                        add(
                            buildJsonObject {
                                put("label", media.label)
                                put("subtitle", media.subtitle)
                            }
                        )
                    }
                }
            )
        }
    }
}
