package me.rerere.rikkahub.data.ai.tools

import android.content.Context
import com.whl.quickjs.wrapper.QuickJSContext
import com.whl.quickjs.wrapper.QuickJSObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import me.rerere.ai.core.InputSchema
import me.rerere.ai.core.Tool
import me.rerere.ai.ui.UIMessagePart
import me.rerere.rikkahub.data.datastore.SettingsStore
import me.rerere.rikkahub.data.event.AppEvent
import me.rerere.rikkahub.data.event.AppEventBus
import me.rerere.rikkahub.service.XTimelineService
import me.rerere.rikkahub.utils.readClipboardText
import me.rerere.rikkahub.utils.writeClipboardText
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.Locale

@Serializable
sealed class LocalToolOption {
    @Serializable
    @SerialName("javascript_engine")
    data object JavascriptEngine : LocalToolOption()

    @Serializable
    @SerialName("time_info")
    data object TimeInfo : LocalToolOption()

    @Serializable
    @SerialName("clipboard")
    data object Clipboard : LocalToolOption()

    @Serializable
    @SerialName("tts")
    data object Tts : LocalToolOption()

    @Serializable
    @SerialName("ask_user")
    data object AskUser : LocalToolOption()
}

class LocalTools(
    private val context: Context,
    private val eventBus: AppEventBus,
    private val xTimelineService: XTimelineService,
    private val settingsStore: SettingsStore,
) {
    val javascriptTool by lazy {
        Tool(
            name = "eval_javascript",
            description = """
                Execute JavaScript code using QuickJS engine (ES2020).
                The result is the value of the last expression in the code.
                For calculations with decimals, use toFixed() to control precision.
                Console output (log/info/warn/error) is captured and returned in 'logs' field.
                No DOM or Node.js APIs available.
                Example: '1 + 2' returns 3; 'const x = 5; x * 2' returns 10.
            """.trimIndent().replace("\n", " "),
            parameters = {
                InputSchema.Obj(
                    properties = buildJsonObject {
                        put("code", buildJsonObject {
                            put("type", "string")
                            put("description", "The JavaScript code to execute")
                        })
                    },
                    required = listOf("code")
                )
            },
            execute = {
                val logs = arrayListOf<String>()
                val context = QuickJSContext.create()
                context.setConsole(object : QuickJSContext.Console {
                    override fun log(info: String?) {
                        logs.add("[LOG] $info")
                    }

                    override fun info(info: String?) {
                        logs.add("[INFO] $info")
                    }

                    override fun warn(info: String?) {
                        logs.add("[WARN] $info")
                    }

                    override fun error(info: String?) {
                        logs.add("[ERROR] $info")
                    }
                })
                val code = it.jsonObject["code"]?.jsonPrimitive?.contentOrNull
                val result = context.evaluate(code)
                val payload = buildJsonObject {
                    if (logs.isNotEmpty()) {
                        put("logs", JsonPrimitive(logs.joinToString("\n")))
                    }
                    put(
                        key = "result",
                        element = when (result) {
                            null -> JsonNull
                            is QuickJSObject -> JsonPrimitive(result.stringify())
                            else -> JsonPrimitive(result.toString())
                        }
                    )
                }
                listOf(UIMessagePart.Text(payload.toString()))
            }
        )
    }

    val timeTool by lazy {
        Tool(
            name = "get_time_info",
            description = """
                Get the current local date and time info from the device.
                Returns year/month/day, weekday, ISO date/time strings, timezone, and timestamp.
            """.trimIndent().replace("\n", " "),
            parameters = {
                InputSchema.Obj(
                    properties = buildJsonObject { }
                )
            },
            execute = {
                val now = ZonedDateTime.now()
                val date = now.toLocalDate()
                val time = now.toLocalTime().withNano(0)
                val weekday = now.dayOfWeek
                val payload = buildJsonObject {
                    put("year", date.year)
                    put("month", date.monthValue)
                    put("day", date.dayOfMonth)
                    put("weekday", weekday.getDisplayName(TextStyle.FULL, Locale.getDefault()))
                    put("weekday_en", weekday.getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                    put("weekday_index", weekday.value)
                    put("date", date.toString())
                    put("time", time.toString())
                    put("datetime", now.withNano(0).toString())
                    put("timezone", now.zone.id)
                    put("utc_offset", now.offset.id)
                    put("timestamp_ms", now.toInstant().toEpochMilli())
                }
                listOf(UIMessagePart.Text(payload.toString()))
            }
        )
    }

    val clipboardTool by lazy {
        Tool(
            name = "clipboard_tool",
            description = """
                Read or write plain text from the device clipboard.
                Use action: read or write. For write, provide text.
            """.trimIndent().replace("\n", " "),
            parameters = {
                InputSchema.Obj(
                    properties = buildJsonObject {
                        put("action", buildJsonObject {
                            put("type", "string")
                            put(
                                "enum",
                                kotlinx.serialization.json.buildJsonArray {
                                    add("read")
                                    add("write")
                                }
                            )
                            put("description", "Operation to perform: read or write")
                        })
                        put("text", buildJsonObject {
                            put("type", "string")
                            put("description", "Text to write to the clipboard (required for write)")
                        })
                    },
                    required = listOf("action")
                )
            },
            execute = {
                val params = it.jsonObject
                val action = params["action"]?.jsonPrimitive?.contentOrNull ?: error("action is required")
                when (action) {
                    "read" -> {
                        val payload = buildJsonObject {
                            put("text", context.readClipboardText())
                        }
                        listOf(UIMessagePart.Text(payload.toString()))
                    }

                    "write" -> {
                        val text = params["text"]?.jsonPrimitive?.contentOrNull ?: error("text is required")
                        context.writeClipboardText(text)
                        val payload = buildJsonObject {
                            put("success", true)
                            put("text", text)
                        }
                        listOf(UIMessagePart.Text(payload.toString()))
                    }

                    else -> error("unknown action: $action, must be one of [read, write]")
                }
            }
        )
    }

    val ttsTool by lazy {
        Tool(
            name = "text_to_speech",
            description = """
                Speak text aloud to the user using the device's text-to-speech engine.
                Use this when the user asks you to read something aloud, or when audio output is appropriate.
                The tool returns immediately; audio plays in the background on the device.
                Provide natural, readable text without markdown formatting.
            """.trimIndent().replace("\n", " "),
            parameters = {
                InputSchema.Obj(
                    properties = buildJsonObject {
                        put("text", buildJsonObject {
                            put("type", "string")
                            put("description", "The text to speak aloud")
                        })
                    },
                    required = listOf("text")
                )
            },
            execute = {
                val text = it.jsonObject["text"]?.jsonPrimitive?.contentOrNull
                    ?: error("text is required")
                eventBus.emit(AppEvent.Speak(text))
                val payload = buildJsonObject {
                    put("success", true)
                }
                listOf(UIMessagePart.Text(payload.toString()))
            }
        )
    }

    val askUserTool by lazy {
        Tool(
            name = "ask_user",
            description = """
                Ask the user one or more questions when you need clarification, additional information, or confirmation.
                Each question can optionally provide a list of suggested options for the user to choose from.
                The user may select an option or provide their own free-text answer for each question.
                The answers will be returned as a JSON object mapping question IDs to the user's responses.
            """.trimIndent().replace("\n", " "),
            parameters = {
                InputSchema.Obj(
                    properties = buildJsonObject {
                        put("questions", buildJsonObject {
                            put("type", "array")
                            put("description", "List of questions to ask the user")
                            put("items", buildJsonObject {
                                put("type", "object")
                                put("properties", buildJsonObject {
                                    put("id", buildJsonObject {
                                        put("type", "string")
                                        put("description", "Unique identifier for this question")
                                    })
                                    put("question", buildJsonObject {
                                        put("type", "string")
                                        put("description", "The question text to display to the user")
                                    })
                                    put("options", buildJsonObject {
                                        put("type", "array")
                                        put(
                                            "description",
                                            "Optional list of suggested options for the user to choose from"
                                        )
                                        put("items", buildJsonObject {
                                            put("type", "string")
                                        })
                                    })
                                })
                                put("required", kotlinx.serialization.json.buildJsonArray {
                                    add("id")
                                    add("question")
                                })
                            })
                        })
                    },
                    required = listOf("questions")
                )
            },
            needsApproval = true,
            execute = {
                error("ask_user tool should be handled by HITL flow")
            }
        )
    }

    val readXTimelineTool by lazy {
        Tool(
            name = "read_x_timeline",
            description = "Read the latest posts from the local built-in X timeline. Returns post IDs, author info, text content, and interaction counts for AI context.",
            parameters = {
                InputSchema.Obj(
                    properties = buildJsonObject {
                        put("limit", buildJsonObject {
                            put("type", "integer")
                            put("description", "Optional number of latest posts to read, between 1 and 20")
                        })
                    }
                )
            },
            execute = {
                val limit = it.jsonObject["limit"]?.jsonPrimitive?.contentOrNull?.toIntOrNull()?.coerceIn(1, 20) ?: 8
                val posts = xTimelineService.getFeedSnapshot(limit)
                val payload = buildJsonObject {
                    put("success", true)
                    put("posts", buildJsonArray {
                        posts.forEach { post ->
                            add(buildJsonObject {
                                put("id", post.id)
                                put("author_name", post.authorName)
                                put("author_handle", post.authorHandle)
                                put("content", post.content)
                                put("reply_count", post.replyCount)
                                put("repost_count", post.repostCount)
                                put("like_count", post.likeCount)
                                put("view_count", post.viewCount)
                                put("created_at", post.createAt)
                            })
                        }
                    })
                }
                listOf(UIMessagePart.Text(payload.toString()))
            }
        )
    }

    val publishXPostTool by lazy {
        Tool(
            name = "publish_x_post",
            description = "Publish a new post to the local built-in X timeline.",
            parameters = {
                InputSchema.Obj(
                    properties = buildJsonObject {
                        put("content", buildJsonObject {
                            put("type", "string")
                            put("description", "Text content to publish to the local X timeline")
                        })
                    },
                    required = listOf("content")
                )
            },
            execute = {
                val content = it.jsonObject["content"]?.jsonPrimitive?.contentOrNull ?: error("content is required")
                val postId = xTimelineService.createToolPost(content = content)
                    ?: error("failed to publish post")
                val post = xTimelineService.getPost(postId)
                listOf(UIMessagePart.Text(buildPostPayload(postId, post).toString()))
            }
        )
    }

    val replyXPostTool by lazy {
        Tool(
            name = "reply_x_post",
            description = "Reply to an existing post in the local built-in X timeline.",
            parameters = {
                InputSchema.Obj(
                    properties = buildJsonObject {
                        put("post_id", buildJsonObject {
                            put("type", "string")
                            put("description", "The target post ID to reply to")
                        })
                        put("content", buildJsonObject {
                            put("type", "string")
                            put("description", "Reply content")
                        })
                    },
                    required = listOf("post_id", "content")
                )
            },
            execute = {
                val params = it.jsonObject
                val postId = params["post_id"]?.jsonPrimitive?.contentOrNull ?: error("post_id is required")
                val content = params["content"]?.jsonPrimitive?.contentOrNull ?: error("content is required")
                val replyId = xTimelineService.createToolPost(content = content, replyToId = postId)
                    ?: error("failed to publish reply")
                val post = xTimelineService.getPost(replyId)
                listOf(UIMessagePart.Text(buildPostPayload(replyId, post).toString()))
            }
        )
    }

    val likeXPostTool by lazy {
        Tool(
            name = "like_x_post",
            description = "Like or unlike a post in the local built-in X timeline.",
            parameters = {
                InputSchema.Obj(
                    properties = buildJsonObject {
                        put("post_id", buildJsonObject {
                            put("type", "string")
                            put("description", "The target post ID")
                        })
                        put("liked", buildJsonObject {
                            put("type", "boolean")
                            put("description", "Whether the post should be liked. Defaults to true")
                        })
                    },
                    required = listOf("post_id")
                )
            },
            execute = {
                val params = it.jsonObject
                val postId = params["post_id"]?.jsonPrimitive?.contentOrNull ?: error("post_id is required")
                val liked = params["liked"]?.jsonPrimitive?.booleanOrNull ?: true
                val post = xTimelineService.setLike(postId, liked) ?: error("failed to update like state")
                listOf(UIMessagePart.Text(buildInteractionPayload(post).toString()))
            }
        )
    }

    val repostXPostTool by lazy {
        Tool(
            name = "repost_x_post",
            description = "Repost or undo repost for a post in the local built-in X timeline.",
            parameters = {
                InputSchema.Obj(
                    properties = buildJsonObject {
                        put("post_id", buildJsonObject {
                            put("type", "string")
                            put("description", "The target post ID")
                        })
                        put("reposted", buildJsonObject {
                            put("type", "boolean")
                            put("description", "Whether the post should be reposted. Defaults to true")
                        })
                    },
                    required = listOf("post_id")
                )
            },
            execute = {
                val params = it.jsonObject
                val postId = params["post_id"]?.jsonPrimitive?.contentOrNull ?: error("post_id is required")
                val reposted = params["reposted"]?.jsonPrimitive?.booleanOrNull ?: true
                val post = xTimelineService.setRepost(postId, reposted) ?: error("failed to update repost state")
                listOf(UIMessagePart.Text(buildInteractionPayload(post).toString()))
            }
        )
    }

    val bookmarkXPostTool by lazy {
        Tool(
            name = "bookmark_x_post",
            description = "Bookmark or remove bookmark for a post in the local built-in X timeline.",
            parameters = {
                InputSchema.Obj(
                    properties = buildJsonObject {
                        put("post_id", buildJsonObject {
                            put("type", "string")
                            put("description", "The target post ID")
                        })
                        put("bookmarked", buildJsonObject {
                            put("type", "boolean")
                            put("description", "Whether the post should be bookmarked. Defaults to true")
                        })
                    },
                    required = listOf("post_id")
                )
            },
            execute = {
                val params = it.jsonObject
                val postId = params["post_id"]?.jsonPrimitive?.contentOrNull ?: error("post_id is required")
                val bookmarked = params["bookmarked"]?.jsonPrimitive?.booleanOrNull ?: true
                val post = xTimelineService.setBookmark(postId, bookmarked) ?: error("failed to update bookmark state")
                listOf(UIMessagePart.Text(buildInteractionPayload(post).toString()))
            }
        )
    }

    private fun buildPostPayload(postId: String, post: me.rerere.rikkahub.data.db.entity.XPostEntity?) = buildJsonObject {
        put("success", true)
        put("post_id", postId)
        if (post != null) {
            put("author_name", post.authorName)
            put("author_handle", post.authorHandle)
            put("content", post.content)
            put("reply_count", post.replyCount)
            put("repost_count", post.repostCount)
            put("like_count", post.likeCount)
            put("view_count", post.viewCount)
        }
    }

    private fun buildInteractionPayload(post: me.rerere.rikkahub.data.db.entity.XPostEntity) = buildJsonObject {
        put("success", true)
        put("post_id", post.id)
        put("liked_by_user", post.likedByUser)
        put("reposted_by_user", post.repostedByUser)
        put("bookmarked_by_user", post.bookmarkedByUser)
        put("reply_count", post.replyCount)
        put("repost_count", post.repostCount)
        put("like_count", post.likeCount)
        put("view_count", post.viewCount)
    }

    fun getTools(options: List<LocalToolOption>): List<Tool> {
        val tools = mutableListOf<Tool>()
        if (options.contains(LocalToolOption.JavascriptEngine)) {
            tools.add(javascriptTool)
        }
        if (options.contains(LocalToolOption.TimeInfo)) {
            tools.add(timeTool)
        }
        if (options.contains(LocalToolOption.Clipboard)) {
            tools.add(clipboardTool)
        }
        if (options.contains(LocalToolOption.Tts)) {
            tools.add(ttsTool)
        }
        if (options.contains(LocalToolOption.AskUser)) {
            tools.add(askUserTool)
        }
        val xTools = settingsStore.settingsFlow.value.pluginSettings.xTools
        if (xTools.enabled) {
            if (xTools.allowReadTimeline) {
                tools.add(readXTimelineTool)
            }
            if (xTools.allowPublishPost) {
                tools.add(publishXPostTool)
            }
            if (xTools.allowReplyPost) {
                tools.add(replyXPostTool)
            }
            if (xTools.allowLikePost) {
                tools.add(likeXPostTool)
            }
            if (xTools.allowRepostPost) {
                tools.add(repostXPostTool)
            }
            if (xTools.allowBookmarkPost) {
                tools.add(bookmarkXPostTool)
            }
        }
        return tools
    }

}
