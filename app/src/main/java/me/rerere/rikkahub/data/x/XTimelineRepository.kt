package me.rerere.rikkahub.data.x

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.rerere.ai.provider.ProviderManager
import me.rerere.ai.provider.TextGenerationParams
import me.rerere.ai.ui.UIMessage
import me.rerere.ai.ui.toText
import me.rerere.rikkahub.AppScope
import me.rerere.rikkahub.data.datastore.Settings
import me.rerere.rikkahub.data.datastore.findModelById
import me.rerere.rikkahub.data.datastore.findProvider
import me.rerere.rikkahub.data.datastore.SettingsStore
import me.rerere.rikkahub.data.datastore.getBotAssistants
import me.rerere.rikkahub.data.model.Assistant
import me.rerere.rikkahub.data.model.Avatar
import me.rerere.rikkahub.data.plugin.XBotInteractionMode
import java.io.File
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.uuid.Uuid

private const val CURRENT_TIMELINE_SEED_VERSION = 4
private const val HOUR_MILLIS = 3_600_000L
private const val ASSISTANT_AUTHOR_PREFIX = "assistant-bot-"
private val DEPRECATED_AUTHOR_IDS = setOf("author-grok")
private val DEPRECATED_POST_IDS = setOf("post-grok-bot-system", "post-grok-reply")

@Serializable
data class XAuthor(
    val id: String,
    val displayName: String,
    val handle: String,
    val bio: String,
    val initials: String,
    val avatarColorHex: Long,
    val verified: Boolean = false,
    val avatarUrl: String? = null,
    val isBot: Boolean = false,
    val botSummary: String = "",
    val botTags: List<String> = emptyList(),
)

@Serializable
data class XMedia(
    val id: String,
    val label: String,
    val subtitle: String = "",
    val tintHex: Long,
    val template: String = "plain",
    val imageUrl: String? = null,
)

@Serializable
enum class XPostSource {
    USER,
    AI_ASSISTANT,
    SYSTEM,
}

@Serializable
data class XPost(
    val id: String,
    val authorId: String,
    val body: String,
    val createdAtMillis: Long,
    val replyToPostId: String? = null,
    val quotePostId: String? = null,
    val detailFocusPostId: String? = null,
    val media: List<XMedia> = emptyList(),
    val source: XPostSource = XPostSource.USER,
    val likedByMe: Boolean = false,
    val repostedByMe: Boolean = false,
    val bookmarkedByMe: Boolean = false,
    val likeCount: Int = 0,
    val replyCount: Int = 0,
    val repostCount: Int = 0,
    val bookmarkCount: Int = 0,
    val viewCount: Int = 0,
    val tags: List<String> = emptyList(),
    val feedTimeLabel: String? = null,
    val detailTimeLabel: String? = null,
)

@Serializable
data class XTimelineState(
    val currentUserId: String,
    val authors: List<XAuthor>,
    val posts: List<XPost>,
    val lastUpdatedAt: Long = System.currentTimeMillis(),
    val seedVersion: Int = CURRENT_TIMELINE_SEED_VERSION,
)

data class XResolvedPost(
    val post: XPost,
    val author: XAuthor,
    val quotedPost: XResolvedPost? = null,
)

private sealed interface PlannedBotAction {
    data class Publish(
        val authorId: String,
        val text: String,
        val quotePostId: String?,
    ) : PlannedBotAction

    data class Reply(
        val authorId: String,
        val postId: String,
        val text: String,
    ) : PlannedBotAction
}

class XTimelineRepository(
    context: Context,
    private val scope: AppScope,
    private val json: Json,
    private val settingsStore: SettingsStore,
    private val providerManager: ProviderManager,
) {
    private val storageFile = File(context.filesDir, "x_timeline_state.json")
    private val _state = MutableStateFlow(defaultState())
    val state: StateFlow<XTimelineState> = _state.asStateFlow()
    private val automationMutex = Mutex()

    init {
        scope.launch {
            loadFromDisk()
        }
        scope.launch {
            settingsStore.settingsFlow.collectLatest { settings ->
                syncBotAuthors(settings)
            }
        }
    }

    fun currentState(): XTimelineState = state.value

    suspend fun publishPost(
        text: String,
        quotePostId: String? = null,
        media: List<XMedia> = emptyList(),
        source: XPostSource = XPostSource.USER,
        authorId: String = state.value.currentUserId,
    ): XPost {
        val body = text.trim()
        require(body.isNotEmpty()) { "text is required" }
        val resolvedAuthorId = authorId.takeIf { id -> state.value.findAuthor(id) != null } ?: state.value.currentUserId
        val resolvedQuoteId = quotePostId?.takeIf(::postExists)

        val newPost = XPost(
            id = Uuid.random().toString(),
            authorId = resolvedAuthorId,
            body = body,
            createdAtMillis = System.currentTimeMillis(),
            quotePostId = resolvedQuoteId,
            media = media,
            source = source,
            viewCount = 1,
            tags = topLevelTagsForAuthor(resolvedAuthorId)
        )
        updateState { timeline ->
            timeline.copy(
                posts = listOf(newPost) + timeline.posts.map { post ->
                    if (post.id == resolvedQuoteId) {
                        post.copy(repostCount = post.repostCount + 1)
                    } else {
                        post
                    }
                }
            )
        }
        if (source == XPostSource.USER && resolvedAuthorId == state.value.currentUserId) {
            maybeRunBotAutomation(triggerPostId = newPost.id)
        }
        return newPost
    }

    suspend fun replyToPost(
        postId: String,
        text: String,
        source: XPostSource = XPostSource.USER,
        authorId: String = state.value.currentUserId,
    ): XPost {
        require(postExists(postId)) { "post not found: $postId" }
        val body = text.trim()
        require(body.isNotEmpty()) { "text is required" }
        val resolvedAuthorId = authorId.takeIf { id -> state.value.findAuthor(id) != null } ?: state.value.currentUserId

        val reply = XPost(
            id = Uuid.random().toString(),
            authorId = resolvedAuthorId,
            body = body,
            createdAtMillis = System.currentTimeMillis(),
            replyToPostId = postId,
            source = source,
            viewCount = 1,
            tags = listOf("Replies")
        )
        updateState { timeline ->
            timeline.copy(
                posts = listOf(reply) + timeline.posts.map { post ->
                    if (post.id == postId) {
                        post.copy(replyCount = post.replyCount + 1)
                    } else {
                        post
                    }
                }
            )
        }
        if (source == XPostSource.USER && resolvedAuthorId == state.value.currentUserId) {
            maybeRunBotAutomation(triggerPostId = reply.id)
        }
        return reply
    }

    suspend fun toggleLike(postId: String): XPost = mutatePost(postId) { post ->
        val enabled = !post.likedByMe
        post.copy(
            likedByMe = enabled,
            likeCount = (post.likeCount + if (enabled) 1 else -1).coerceAtLeast(0)
        )
    }

    suspend fun toggleRepost(postId: String): XPost = mutatePost(postId) { post ->
        val enabled = !post.repostedByMe
        post.copy(
            repostedByMe = enabled,
            repostCount = (post.repostCount + if (enabled) 1 else -1).coerceAtLeast(0)
        )
    }

    suspend fun toggleBookmark(postId: String): XPost = mutatePost(postId) { post ->
        val enabled = !post.bookmarkedByMe
        post.copy(
            bookmarkedByMe = enabled,
            bookmarkCount = (post.bookmarkCount + if (enabled) 1 else -1).coerceAtLeast(0)
        )
    }

    suspend fun recordView(postId: String) {
        mutatePost(postId) { post ->
            post.copy(viewCount = post.viewCount + 1)
        }
        maybeRunBotAutomation()
    }

    fun getPost(postId: String): XPost? = state.value.posts.firstOrNull { it.id == postId }

    fun resolvePost(postId: String): XResolvedPost? = state.value.resolvePost(postId)

    fun topLevelPosts(): List<XResolvedPost> = state.value.topLevelPosts()

    fun repliesFor(postId: String): List<XResolvedPost> = state.value.repliesFor(postId)

    private fun postExists(postId: String): Boolean = getPost(postId) != null

    private suspend fun syncBotAuthors(settings: Settings) {
        val botAuthors = settings.getBotAssistants().map { assistant ->
            assistant.toXAuthor()
        }
        updateState { timeline ->
            timeline.copy(
                authors = timeline.authors
                    .filterNot { author -> author.id.startsWith(ASSISTANT_AUTHOR_PREFIX) }
                    .filterNot { author -> author.id in DEPRECATED_AUTHOR_IDS } + botAuthors
            )
        }
    }

    private suspend fun maybeRunBotAutomation(triggerPostId: String? = null) {
        automationMutex.withLock {
            val settings = settingsStore.settingsFlow.value
            val config = settings.pluginSettings.x
            val botAssistants = settings.getBotAssistants()
            if (!config.enabled || !config.botAutomationEnabled || botAssistants.isEmpty()) {
                return@withLock
            }

            val cooldownMillis = config.botActivityLevel.cooldownMinutes * 60_000L
            val now = System.currentTimeMillis()
            if (now - config.lastBotActivityAt < cooldownMillis) {
                return@withLock
            }

            syncBotAuthors(settings)
            val action = planBotAction(
                timeline = state.value,
                settings = settings,
                botAssistants = botAssistants,
                config = config,
                triggerPostId = triggerPostId,
            ) ?: return@withLock

            when (action) {
                is PlannedBotAction.Publish -> {
                    publishPost(
                        text = action.text,
                        quotePostId = action.quotePostId,
                        source = XPostSource.SYSTEM,
                        authorId = action.authorId,
                    )
                }

                is PlannedBotAction.Reply -> {
                    replyToPost(
                        postId = action.postId,
                        text = action.text,
                        source = XPostSource.SYSTEM,
                        authorId = action.authorId,
                    )
                }
            }

            settingsStore.update { old ->
                old.copy(
                    pluginSettings = old.pluginSettings.copy(
                        x = old.pluginSettings.x.copy(lastBotActivityAt = now)
                    )
                )
            }
        }
    }

    private suspend fun planBotAction(
        timeline: XTimelineState,
        settings: Settings,
        botAssistants: List<Assistant>,
        config: me.rerere.rikkahub.data.plugin.XPluginConfig,
        triggerPostId: String?,
    ): PlannedBotAction? {
        val random = Random(System.currentTimeMillis())
        val botActors = botAssistants.mapNotNull { assistant ->
            val authorId = assistant.toAuthorId()
            if (timeline.findAuthor(authorId) == null) return@mapNotNull null
            assistant to authorId
        }
        if (botActors.isEmpty()) return null

        val interactionMode = config.botInteractionMode
        val triggerPost = triggerPostId?.let(timeline::resolvePost)
        if (config.botReplyToUserPosts && triggerPost != null && triggerPost.author.id == timeline.currentUserId) {
            val (assistant, authorId) = botActors.random(random)
            val actualMode = resolveInteractionMode(interactionMode, random)
            val responseText = buildBotResponseText(
                assistant = assistant,
                timeline = timeline,
                targetPost = triggerPost.post,
                settings = settings,
                quoteStyle = actualMode == XBotInteractionMode.Quote
            )
            return when (actualMode) {
                XBotInteractionMode.Reply -> PlannedBotAction.Reply(
                    authorId = authorId,
                    postId = triggerPost.post.id,
                    text = responseText,
                )

                XBotInteractionMode.Quote,
                XBotInteractionMode.Mixed -> PlannedBotAction.Publish(
                    authorId = authorId,
                    text = responseText,
                    quotePostId = triggerPost.post.id,
                )
            }
        }

        val botTopLevelPosts = timeline.posts
            .filter { post ->
                post.replyToPostId == null &&
                    post.authorId.startsWith(ASSISTANT_AUTHOR_PREFIX)
            }
        val shouldInteractWithBots = config.botInteractWithOtherBots &&
            botTopLevelPosts.isNotEmpty() &&
            (!config.botAutoPostEnabled || random.nextBoolean())

        if (shouldInteractWithBots) {
            val targetPost = botTopLevelPosts.random(random)
            val availableActors = botActors.filterNot { (_, authorId) -> authorId == targetPost.authorId }
            if (availableActors.isNotEmpty()) {
                val (assistant, authorId) = availableActors.random(random)
                val actualMode = resolveInteractionMode(interactionMode, random)
                val responseText = buildBotResponseText(
                    assistant = assistant,
                    timeline = timeline,
                    targetPost = targetPost,
                    settings = settings,
                    quoteStyle = actualMode == XBotInteractionMode.Quote
                )
                return when (actualMode) {
                    XBotInteractionMode.Reply -> PlannedBotAction.Reply(
                        authorId = authorId,
                        postId = targetPost.id,
                        text = responseText,
                    )

                    XBotInteractionMode.Quote,
                    XBotInteractionMode.Mixed -> PlannedBotAction.Publish(
                        authorId = authorId,
                        text = responseText,
                        quotePostId = targetPost.id,
                    )
                }
            }
        }

        if (config.botAutoPostEnabled) {
            val (assistant, authorId) = botActors.random(random)
            return PlannedBotAction.Publish(
                authorId = authorId,
                text = buildBotPostText(assistant, settings),
                quotePostId = null,
            )
        }

        return null
    }

    private fun resolveInteractionMode(
        configured: XBotInteractionMode,
        random: Random,
    ): XBotInteractionMode {
        return when (configured) {
            XBotInteractionMode.Mixed -> if (random.nextBoolean()) XBotInteractionMode.Reply else XBotInteractionMode.Quote
            else -> configured
        }
    }

    private suspend fun buildBotPostText(
        assistant: Assistant,
        settings: Settings,
    ): String {
        val generated = generateBotText(
            assistant = assistant,
            settings = settings,
            instruction = buildString {
                appendLine("你现在要在 X 上发布一条原创帖子。")
                appendLine("只输出最终帖子正文，不要标题、不要解释、不要引号、不要 Markdown 代码块。")
                appendLine("内容要像真实信息流帖子，简洁、有明确观点，可以直接发布。")
                appendLine("长度尽量控制在 220 个中文字符或等价长度以内。")
                appendLine("除非人设明确要求，否则不要自称 AI、语言模型或助手。")
                appendLine("如果看不出上下文语言，请使用 ${Locale.getDefault().displayName} 的主要语言。")
                assistant.firstMeaningfulLine()?.let {
                    appendLine()
                    appendLine("当前 bot 的人设摘要：")
                    appendLine(it)
                }
            }
        )
        if (generated != null) {
            return generated
        }
        return fallbackBotPostText(assistant, settings)
    }

    private suspend fun buildBotResponseText(
        assistant: Assistant,
        timeline: XTimelineState,
        targetPost: XPost,
        settings: Settings,
        quoteStyle: Boolean,
    ): String {
        val targetAuthor = timeline.findAuthor(targetPost.authorId)
        val generated = generateBotText(
            assistant = assistant,
            settings = settings,
            instruction = buildString {
                appendLine(
                    if (quoteStyle) {
                        "你现在要在 X 上转帖评论别人的帖子。"
                    } else {
                        "你现在要在 X 上直接回复别人的帖子。"
                    }
                )
                appendLine("只输出最终帖子正文，不要标题、不要解释、不要引号、不要 Markdown 代码块。")
                appendLine("保持 bot 自己的人设、语气和判断，不要写成客服口吻。")
                appendLine("长度尽量控制在 220 个中文字符或等价长度以内。")
                appendLine(
                    if (quoteStyle) {
                        "转帖评论需要表达你自己的看法，正文要能单独成立，不要简单复述原帖。"
                    } else {
                        "回复需要直接回应原帖的核心观点，像真实用户在评论区说话。"
                    }
                )
                appendLine()
                appendLine("目标帖子作者：${targetAuthor?.displayName ?: "Unknown"} ${targetAuthor?.handle.orEmpty()}".trim())
                appendLine("目标帖子正文：")
                appendLine(targetPost.body.trim())
                assistant.firstMeaningfulLine()?.let {
                    appendLine()
                    appendLine("当前 bot 的人设摘要：")
                    appendLine(it)
                }
            }
        )
        if (generated != null) {
            return generated
        }
        return fallbackBotResponseText(assistant, targetPost, settings, quoteStyle)
    }

    private suspend fun generateBotText(
        assistant: Assistant,
        settings: Settings,
        instruction: String,
    ): String? {
        val model = settings.findModelById(assistant.chatModelId ?: settings.chatModelId) ?: return null
        val provider = model.findProvider(settings.providers) ?: return null
        val providerHandler = providerManager.getProviderByType(provider)
        val result = runCatching {
            providerHandler.generateText(
                providerSetting = provider,
                messages = buildList {
                    add(
                        UIMessage.system(
                            prompt = assistant.systemPrompt.trim().ifBlank {
                                buildDefaultBotSystemPrompt(assistant)
                            }
                        )
                    )
                    add(UIMessage.user(prompt = instruction))
                },
                params = TextGenerationParams(
                    model = model,
                    temperature = assistant.temperature,
                    topP = assistant.topP,
                    maxTokens = assistant.maxTokens,
                    thinkingBudget = assistant.thinkingBudget ?: 0,
                    customHeaders = assistant.customHeaders,
                    customBody = assistant.customBodies,
                ),
            )
        }.getOrNull() ?: return null

        return result.choices
            .firstOrNull()
            ?.message
            ?.toText()
            ?.let(::sanitizeGeneratedBotText)
            ?.takeIf { it.isNotBlank() }
    }

    private fun buildDefaultBotSystemPrompt(assistant: Assistant): String {
        val botName = assistant.name.ifBlank { "Bot" }
        return """
            你是 X 上的一个真实 Bot 账号，名字叫 $botName。
            你的任务是按照设定直接发帖或回复，输出必须像可以直接发送到时间线的正文。
            不要解释你的思考过程，不要加引号，不要输出多余说明。
        """.trimIndent()
    }

    private fun sanitizeGeneratedBotText(raw: String): String {
        val text = raw
            .replace("```", "")
            .lineSequence()
            .map { it.trim() }
            .dropWhile {
                it.startsWith("当然") ||
                    it.startsWith("好的") ||
                    it.startsWith("可以") ||
                    it.startsWith("以下") ||
                    it.startsWith("作为")
            }
            .joinToString("\n")
            .trim()
            .removeSurrounding("\"")
            .removeSurrounding("“", "”")
            .replace(Regex("\\n{3,}"), "\n\n")
        if (text.isBlank()) return ""
        return if (text.length <= 220) {
            text
        } else {
            text.take(220).trimEnd(' ', '\n', ',', '，', '.', '。', '!', '！', '?', '？', ':', '：') + "…"
        }
    }

    private fun fallbackBotPostText(
        assistant: Assistant,
        settings: Settings,
    ): String {
        val focus = assistant.systemPrompt.firstMeaningfulLine()
        val modelName = settings.findModelById(assistant.chatModelId ?: settings.chatModelId)?.displayName
        return when {
            !focus.isNullOrBlank() && !modelName.isNullOrBlank() ->
                "${focus.take(64)}\n\n先给一个明确判断，再补充一条可执行的观点。$modelName 这边我会继续跟。"

            !focus.isNullOrBlank() ->
                "${focus.take(64)}\n\n先给结论：这条值得继续跟，后面大概率还会有新变化。"

            else ->
                "先记一个判断：这条时间线还没走完，后面更值得看的是观点怎么分化。"
        }
    }

    private fun fallbackBotResponseText(
        assistant: Assistant,
        targetPost: XPost,
        settings: Settings,
        quoteStyle: Boolean,
    ): String {
        val focus = assistant.systemPrompt.firstMeaningfulLine()
        val modelName = settings.findModelById(assistant.chatModelId ?: settings.chatModelId)?.displayName
        val opener = if (quoteStyle) "补一个角度：" else "这个点我会这样看："
        val snippet = targetPost.body.lineSequence().firstOrNull { it.isNotBlank() }.orEmpty().take(42)
        return when {
            !focus.isNullOrBlank() && !modelName.isNullOrBlank() ->
                "$opener $snippet\n\n${focus.take(56)}。$modelName 更适合继续跟这个方向。"

            !focus.isNullOrBlank() ->
                "$opener $snippet\n\n${focus.take(56)}。"

            else ->
                "$opener $snippet\n\n先说结论，这条讨论还有继续展开的空间。"
        }
    }

    private fun topLevelTagsForAuthor(authorId: String): List<String> {
        return buildList {
            add("For You")
            if (authorId == state.value.currentUserId) {
                add("Following")
            } else {
                add("AI Watch")
            }
        }
    }

    private fun Assistant.toAuthorId(): String = "$ASSISTANT_AUTHOR_PREFIX$id"

    private fun Assistant.toXAuthor(): XAuthor {
        val avatarUrl = (avatar as? Avatar.Image)?.url
        val initials = when (avatar) {
            is Avatar.Emoji -> avatar.content
            else -> name.ifBlank { "Bot" }.trim().take(2).uppercase()
        }
        return XAuthor(
            id = toAuthorId(),
            displayName = name.ifBlank { "Untitled Bot" },
            handle = "@${name.ifBlank { "bot" }.lowercase().replace(Regex("[^a-z0-9]+"), "").take(12).ifBlank { "bot${id.toString().take(4)}" }}",
            bio = systemPrompt.firstMeaningfulLine().orEmpty(),
            initials = initials,
            avatarColorHex = botAvatarColor(name.ifBlank { id.toString() }),
            avatarUrl = avatarUrl,
            isBot = true,
        )
    }

    private fun Assistant.firstMeaningfulLine(): String? = systemPrompt.firstMeaningfulLine()

    private fun String.firstMeaningfulLine(): String? =
        lineSequence().map { it.trim() }.firstOrNull { it.isNotEmpty() }

    private fun botAvatarColor(seed: String): Long {
        val palette = listOf(
            0xFF111214L,
            0xFF1C1C1EL,
            0xFF2C2C2EL,
            0xFF3A3A3CL,
        )
        return palette[(seed.hashCode().absoluteValue) % palette.size]
    }

    private suspend fun mutatePost(postId: String, update: (XPost) -> XPost): XPost {
        var updatedPost: XPost? = null
        updateState { timeline ->
            timeline.copy(
                posts = timeline.posts.map { post ->
                    if (post.id == postId) {
                        update(post).also { updatedPost = it }
                    } else {
                        post
                    }
                }
            )
        }
        return updatedPost ?: error("post not found: $postId")
    }

    private suspend fun loadFromDisk() {
        val seed = defaultState()
        val loaded = withContext(Dispatchers.IO) {
            if (!storageFile.exists()) {
                null
            } else {
                runCatching {
                    json.decodeFromString<XTimelineState>(storageFile.readText())
                }.getOrNull()
            }
        } ?: seed

        val sanitized = loaded
            .upgradeSeed(seed)
            .sanitize(seed)

        _state.value = sanitized
        persist(sanitized)
    }

    private suspend fun updateState(transform: (XTimelineState) -> XTimelineState) {
        val newState = transform(state.value)
            .sanitize(defaultState())
            .copy(lastUpdatedAt = System.currentTimeMillis())
        _state.value = newState
        persist(newState)
    }

    private suspend fun persist(newState: XTimelineState) = withContext(Dispatchers.IO) {
        storageFile.parentFile?.mkdirs()
        storageFile.writeText(json.encodeToString(newState))
    }

    private fun defaultState(): XTimelineState {
        val now = System.currentTimeMillis()
        val currentUser = XAuthor(
            id = "author-me",
            displayName = "Zion",
            handle = "@zionchat",
            bio = "Building native AI tools and shipping fast.",
            initials = "Z",
            avatarColorHex = 0xFF1D9BF0,
            avatarUrl = "https://images.unsplash.com/photo-1506748686214-e9df14d4d9d0?auto=format&fit=crop&w=200&q=80",
        )
        val authors = listOf(
            currentUser,
            XAuthor(
                id = "author-elon",
                displayName = "Elon Musk",
                handle = "@elonmusk",
                bio = "Mars, rockets, Grok and late-night posts.",
                initials = "EM",
                avatarColorHex = 0xFF0F1419,
                verified = true,
                avatarUrl = "https://images.unsplash.com/photo-1560250097-0b93528c311a?auto=format&fit=crop&w=200&q=80",
            ),
            XAuthor(
                id = "author-robert",
                displayName = "Robert Scoble",
                handle = "@Scobleizer",
                bio = "Following the edge of AI and product shifts.",
                initials = "RS",
                avatarColorHex = 0xFF111214,
                verified = true,
                avatarUrl = "https://images.unsplash.com/photo-1535295972055-1c762f4483e5?auto=format&fit=crop&w=200&q=80",
            ),
            XAuthor(
                id = "author-leo",
                displayName = "leo 🐾",
                handle = "@synthwavedd",
                bio = "Rumors, models and product leaks.",
                initials = "L",
                avatarColorHex = 0xFF8B5E3C,
                verified = true,
                avatarUrl = "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=200&q=80",
            ),
            XAuthor(
                id = "author-chetas",
                displayName = "Chetaslua",
                handle = "@chetaslua",
                bio = "Tracking model plans and pricing changes.",
                initials = "C",
                avatarColorHex = 0xFF000000,
                verified = true,
            ),
        )

        val robertTranscriptPost = XPost(
            id = "post-robert-transcript",
            authorId = "author-robert",
            body = "Did someone turn the speed knob up on @Grok?\n\nJust did another transcript reading and, damn, it is way faster than last time I asked it to do the same.",
            createdAtMillis = now - 7 * HOUR_MILLIS,
            likeCount = 302,
            replyCount = 0,
            repostCount = 15,
            bookmarkCount = 19,
            viewCount = 5_710_000,
            tags = listOf("Thread"),
            feedTimeLabel = "7小时",
            detailTimeLabel = "26年3月28日, 13:35",
        )
        val chetasQuotePost = XPost(
            id = "post-chetas-plan",
            authorId = "author-chetas",
            body = "🚨 Chatgpt 100$ plan is coming soon\n\nOne bad news - now the 200$ plan is not truly unlimited ( it's 20 times the plus uses)...",
            createdAtMillis = now - HOUR_MILLIS,
            media = listOf(
                XMedia(
                    id = "media-chetas-preview",
                    label = "plan-preview",
                    tintHex = 0xFFBDE3F3,
                    template = "article_preview",
                )
            ),
            viewCount = 84_000,
            tags = listOf("Quote"),
            feedTimeLabel = "1小时",
        )

        val posts = listOf(
            XPost(
                id = "post-elon-grok",
                authorId = "author-elon",
                body = "Grok gets faster & smarter every week",
                createdAtMillis = now - 2 * HOUR_MILLIS,
                quotePostId = robertTranscriptPost.id,
                detailFocusPostId = robertTranscriptPost.id,
                likeCount = 10_000,
                replyCount = 1_711,
                repostCount = 1_141,
                viewCount = 5_680_000,
                tags = listOf("For You", "Following", "AI Watch"),
                feedTimeLabel = "2小时",
            ),
            XPost(
                id = "post-leo-pro",
                authorId = "author-leo",
                body = "The \$200/mo ChatGPT Pro plan will soon no longer be unlimited\n\nI think we all knew this was coming, but still 👎",
                createdAtMillis = now - HOUR_MILLIS,
                quotePostId = chetasQuotePost.id,
                likeCount = 2_480,
                replyCount = 286,
                repostCount = 146,
                bookmarkCount = 63,
                viewCount = 1_240_000,
                tags = listOf("For You", "Following", "AI Watch"),
                feedTimeLabel = "1小时",
            ),
            robertTranscriptPost,
            chetasQuotePost,
        )

        return XTimelineState(
            currentUserId = currentUser.id,
            authors = authors,
            posts = posts,
            lastUpdatedAt = now,
            seedVersion = CURRENT_TIMELINE_SEED_VERSION,
        )
    }
}

fun XTimelineState.findAuthor(authorId: String): XAuthor? = authors.firstOrNull { it.id == authorId }

fun XTimelineState.resolvePost(postId: String): XResolvedPost? {
    val post = posts.firstOrNull { it.id == postId } ?: return null
    return resolvePost(post, visited = mutableSetOf())
}

private fun XTimelineState.resolvePost(
    post: XPost,
    visited: MutableSet<String>,
): XResolvedPost? {
    if (!visited.add(post.id)) return null

    val author = findAuthor(post.authorId) ?: return null
    return XResolvedPost(
        post = post,
        author = author,
        quotedPost = post.quotePostId
            ?.takeIf { it != post.id }
            ?.let { quoteId -> posts.firstOrNull { it.id == quoteId } }
            ?.let { quotePost -> resolvePost(quotePost, visited.toMutableSet()) }
    )
}

fun XTimelineState.topLevelPosts(): List<XResolvedPost> {
    return posts
        .filter { it.replyToPostId == null }
        .sortedByDescending { it.createdAtMillis }
        .mapNotNull { post -> resolvePost(post.id) }
}

fun XTimelineState.repliesFor(postId: String): List<XResolvedPost> {
    return posts
        .filter { it.replyToPostId == postId }
        .sortedByDescending { it.createdAtMillis }
        .mapNotNull { post -> resolvePost(post.id) }
}

private fun XTimelineState.upgradeSeed(seed: XTimelineState): XTimelineState {
    if (seedVersion >= CURRENT_TIMELINE_SEED_VERSION) return this

    val seededAuthorIds = seed.authors.map { it.id }.toSet()
    val mergedAuthors = (seed.authors + authors.filterNot { it.id in seededAuthorIds || it.id in DEPRECATED_AUTHOR_IDS })
        .distinctBy { it.id }
    val seededPostIds = seed.posts.map { it.id }.toSet()
    val mergedPosts = (seed.posts + posts.filterNot { it.id in seededPostIds || it.id in DEPRECATED_POST_IDS })
        .distinctBy { it.id }
        .sortedByDescending { it.createdAtMillis }

    return copy(
        currentUserId = currentUserId.takeIf { id -> mergedAuthors.any { it.id == id } } ?: seed.currentUserId,
        authors = mergedAuthors,
        posts = mergedPosts,
        lastUpdatedAt = System.currentTimeMillis(),
        seedVersion = CURRENT_TIMELINE_SEED_VERSION,
    )
}

private fun XTimelineState.sanitize(fallback: XTimelineState): XTimelineState {
    if (authors.isEmpty()) return fallback

    val sanitizedAuthors = authors
        .filter { it.id.isNotBlank() && it.id !in DEPRECATED_AUTHOR_IDS }
        .distinctBy { it.id }

    if (sanitizedAuthors.isEmpty()) return fallback

    val authorIds = sanitizedAuthors.map { it.id }.toSet()
    val candidatePosts = posts
        .filter { post ->
            post.id.isNotBlank() &&
                post.id !in DEPRECATED_POST_IDS &&
                post.authorId in authorIds
        }
        .distinctBy { it.id }
    val validPostIds = candidatePosts.map { it.id }.toSet()

    val sanitizedPosts = candidatePosts.map { post ->
        post.copy(
            replyToPostId = post.replyToPostId?.takeIf { it in validPostIds && it != post.id },
            quotePostId = post.quotePostId?.takeIf { it in validPostIds && it != post.id },
            detailFocusPostId = post.detailFocusPostId?.takeIf { it in validPostIds && it != post.id },
            likeCount = post.likeCount.coerceAtLeast(0),
            replyCount = post.replyCount.coerceAtLeast(0),
            repostCount = post.repostCount.coerceAtLeast(0),
            bookmarkCount = post.bookmarkCount.coerceAtLeast(0),
            viewCount = post.viewCount.coerceAtLeast(0),
            tags = post.tags.filter { it.isNotBlank() }.distinct(),
            media = post.media.distinctBy { it.id.ifBlank { "${post.id}:${it.label}" } }
        )
    }

    val safeCurrentUserId = currentUserId.takeIf { it in authorIds } ?: sanitizedAuthors.first().id
    return copy(
        currentUserId = safeCurrentUserId,
        authors = sanitizedAuthors,
        posts = sanitizedPosts,
        seedVersion = seedVersion.coerceAtLeast(1),
    )
}
