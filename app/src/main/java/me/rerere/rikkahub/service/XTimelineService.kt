package me.rerere.rikkahub.service

import android.util.Log
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.rerere.ai.provider.ProviderManager
import me.rerere.ai.provider.TextGenerationParams
import me.rerere.ai.ui.UIMessage
import me.rerere.rikkahub.AppScope
import me.rerere.rikkahub.data.datastore.Settings
import me.rerere.rikkahub.data.datastore.SettingsStore
import me.rerere.rikkahub.data.datastore.findModelById
import me.rerere.rikkahub.data.datastore.findProvider
import me.rerere.rikkahub.data.datastore.getBotAssistants
import me.rerere.rikkahub.data.datastore.getCurrentAssistant
import me.rerere.rikkahub.data.db.entity.XPostEntity
import me.rerere.rikkahub.data.model.Assistant
import me.rerere.rikkahub.data.datastore.isPersonalization
import me.rerere.rikkahub.data.repository.MemoryRepository
import me.rerere.rikkahub.data.repository.XPostRepository
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.uuid.Uuid

private const val TAG = "XTimelineService"
private const val USER_KIND = "user"
private const val BOT_KIND = "bot"
private const val SYSTEM_KIND = "system"

class XTimelineService(
    private val appScope: AppScope,
    private val settingsStore: SettingsStore,
    private val repository: XPostRepository,
    private val providerManager: ProviderManager,
    private val memoryRepository: MemoryRepository,
) {
    private var autoPostJob: Job? = null
    private var seeded = false

    fun observeFeed(): Flow<List<XPostEntity>> = repository.observeFeed()

    fun observePost(id: String): Flow<XPostEntity?> = repository.observePost(id)

    fun observeReplies(parentId: String): Flow<List<XPostEntity>> = repository.observeReplies(parentId)

    fun bootstrap() {
        appScope.launch {
            ensureSeedData()
            ensureRecentBotActivity()
            startAutoPostingIfNeeded()
        }
    }

    suspend fun getPost(id: String): XPostEntity? = repository.getById(id)

    suspend fun createUserPost(content: String, replyToId: String? = null): String? {
        val normalized = sanitizeUserText(content)
        if (normalized.isBlank()) return null
        val settings = settingsStore.settingsFlow.first()
        val id = Uuid.random().toString()
        val parent = replyToId?.let { repository.getById(it) }
        val post = XPostEntity(
            id = id,
            parentPostId = parent?.id,
            rootPostId = parent?.rootPostId ?: id,
            authorKind = USER_KIND,
            authorName = settings.displaySetting.userNickname.ifBlank { "你" },
            authorHandle = "@you",
            content = normalized,
            likeCount = 0,
            replyCount = 0,
            repostCount = 0,
            viewCount = randomViews(userPost = true),
            createAt = System.currentTimeMillis(),
            updateAt = System.currentTimeMillis(),
        )
        repository.upsert(post)
        parent?.let { repository.upsert(it.copy(replyCount = it.replyCount + 1, updateAt = System.currentTimeMillis())) }
        scheduleBotReplies(post)
        return id
    }

    suspend fun createToolPost(content: String, replyToId: String? = null): String? {
        val normalized = sanitizeUserText(content)
        if (normalized.isBlank()) return null
        val settings = settingsStore.settingsFlow.first()
        val assistant = settings.getCurrentAssistant()
        return if (assistant.isPersonalization()) {
            createUserPost(normalized, replyToId)
        } else {
            createAssistantPost(assistant = assistant, content = normalized, replyToId = replyToId)
        }
    }

    suspend fun generateAssistantPostNow(): String? {
        val settings = settingsStore.settingsFlow.first()
        val assistant = pickAutomationAssistant(settings) ?: return null
        val recentFeed = repository.getRecentFeed(limit = 6)
        val prompt = buildString {
            append("请以你自己的口吻发布一条适合 X/Twitter 的短帖。")
            append("语气要自然，像真人发帖，不要解释，不要 Markdown，不要带引号，不超过 220 字。")
            if (recentFeed.isNotEmpty()) {
                append("\n\n最近时间线话题：\n")
                recentFeed.take(4).forEach { item ->
                    append("- ${item.authorName}: ${item.content}\n")
                }
            }
        }
        val content = generateAssistantCopy(settings, assistant, prompt)
        return createAssistantPost(assistant = assistant, content = content)
    }

    fun requestBotReplies(postId: String, force: Boolean = false) {
        appScope.launch {
            repository.getById(postId)?.let { post ->
                scheduleBotReplies(post, force = force)
            }
        }
    }

    suspend fun toggleLike(postId: String) {
        repository.getById(postId)?.let { post ->
            val nextLiked = !post.likedByUser
            repository.upsert(
                post.copy(
                    likedByUser = nextLiked,
                    likeCount = (post.likeCount + if (nextLiked) 1 else -1).coerceAtLeast(0),
                    updateAt = System.currentTimeMillis(),
                )
            )
        }
    }

    suspend fun toggleRepost(postId: String) {
        repository.getById(postId)?.let { post ->
            val next = !post.repostedByUser
            repository.upsert(
                post.copy(
                    repostedByUser = next,
                    repostCount = (post.repostCount + if (next) 1 else -1).coerceAtLeast(0),
                    updateAt = System.currentTimeMillis(),
                )
            )
        }
    }

    suspend fun toggleBookmark(postId: String) {
        repository.getById(postId)?.let { post ->
            repository.upsert(
                post.copy(
                    bookmarkedByUser = !post.bookmarkedByUser,
                    updateAt = System.currentTimeMillis(),
                )
            )
        }
    }

    suspend fun markViewed(postId: String) {
        repository.getById(postId)?.let { post ->
            repository.upsert(post.copy(viewCount = post.viewCount + 1, updateAt = System.currentTimeMillis()))
        }
    }

    suspend fun getFeedSnapshot(limit: Int = 12): List<XPostEntity> = repository.getRecentFeed(limit)

    suspend fun setLike(postId: String, liked: Boolean): XPostEntity? {
        val post = repository.getById(postId) ?: return null
        val shouldChange = post.likedByUser != liked
        if (!shouldChange) return post
        val updated = post.copy(
            likedByUser = liked,
            likeCount = (post.likeCount + if (liked) 1 else -1).coerceAtLeast(0),
            updateAt = System.currentTimeMillis(),
        )
        repository.upsert(updated)
        return updated
    }

    suspend fun setRepost(postId: String, reposted: Boolean): XPostEntity? {
        val post = repository.getById(postId) ?: return null
        val shouldChange = post.repostedByUser != reposted
        if (!shouldChange) return post
        val updated = post.copy(
            repostedByUser = reposted,
            repostCount = (post.repostCount + if (reposted) 1 else -1).coerceAtLeast(0),
            updateAt = System.currentTimeMillis(),
        )
        repository.upsert(updated)
        return updated
    }

    suspend fun setBookmark(postId: String, bookmarked: Boolean): XPostEntity? {
        val post = repository.getById(postId) ?: return null
        val shouldChange = post.bookmarkedByUser != bookmarked
        if (!shouldChange) return post
        val updated = post.copy(
            bookmarkedByUser = bookmarked,
            updateAt = System.currentTimeMillis(),
        )
        repository.upsert(updated)
        return updated
    }

    private suspend fun ensureSeedData() {
        if (seeded || repository.countAll() > 0) {
            seeded = true
            return
        }
        val settings = settingsStore.settingsFlow.first()
        val initialPosts = buildList {
            val welcomeId = Uuid.random().toString()
            add(
                XPostEntity(
                    id = welcomeId,
                    rootPostId = welcomeId,
                    authorKind = SYSTEM_KIND,
                    authorName = "Z-Phone",
                    authorHandle = "@zphone",
                    content = "欢迎来到 Z-Phone 时间线。这里的发帖、回复、点赞和 bot 互动都会被本地持久化保存。",
                    likeCount = 18,
                    replyCount = 0,
                    repostCount = 4,
                    viewCount = 132,
                    createAt = System.currentTimeMillis() - 1000L * 60L * 38L,
                    updateAt = System.currentTimeMillis() - 1000L * 60L * 38L,
                )
            )
            settings.getBotAssistants().take(2).forEachIndexed { index, assistant ->
                val id = Uuid.random().toString()
                add(
                    XPostEntity(
                        id = id,
                        rootPostId = id,
                        assistantId = assistant.id.toString(),
                        authorKind = BOT_KIND,
                        authorName = assistant.name.ifBlank { "Bot ${index + 1}" },
                        authorHandle = assistant.toHandle(),
                        content = fallbackAutoPost(assistant, emptyList()),
                        likeCount = 12 + index * 8,
                        replyCount = 0,
                        repostCount = 3 + index,
                        viewCount = 240 + index * 120,
                        createAt = System.currentTimeMillis() - 1000L * 60L * (26L - index * 8L),
                        updateAt = System.currentTimeMillis() - 1000L * 60L * (26L - index * 8L),
                    )
                )
            }
        }
        repository.insertAll(initialPosts)
        seeded = true
    }

    private suspend fun ensureRecentBotActivity() {
        val lastBotPost = repository.getLatestBotPost()
        if (lastBotPost == null || System.currentTimeMillis() - lastBotPost.createAt > 1000L * 60L * 20L) {
            generateAssistantPostNow()
        }
    }

    private fun startAutoPostingIfNeeded() {
        if (autoPostJob?.isActive == true) return
        autoPostJob = appScope.launch {
            while (isActive) {
                delay(95_000L)
                runCatching {
                    generateAssistantPostNow()
                }.onFailure {
                    Log.e(TAG, "auto post failed", it)
                }
            }
        }
    }

    private fun scheduleBotReplies(post: XPostEntity, force: Boolean = false) {
        if (post.authorKind != USER_KIND && !force) return
        appScope.launch {
            val settings = settingsStore.settingsFlow.first()
            val bots = settings.getBotAssistants()
            if (bots.isEmpty()) return@launch
            bots.forEachIndexed { index, assistant ->
                launch {
                    delay(1400L * (index + 1))
                    runCatching {
                        createAssistantReply(post, assistant, force = force)
                    }.onFailure {
                        Log.e(TAG, "bot reply failed: ${assistant.name}", it)
                    }
                }
            }
        }
    }

    private suspend fun createAssistantReply(post: XPostEntity, assistant: Assistant, force: Boolean = false) {
        if (!force && repository.countRepliesByAssistant(post.id, assistant.id.toString()) > 0) return
        val settings = settingsStore.settingsFlow.first()
        val prompt = buildString {
            append("请以你自己的账号口吻，回复下面这条帖子。")
            append("输出一条自然、简短、有真人感的回复，不要解释，不要 Markdown，不超过 180 字。")
            append("\n\n原帖作者：${post.authorName} (${post.authorHandle})")
            append("\n原帖内容：${post.content}")
        }
        val content = generateAssistantCopy(settings, assistant, prompt)
        createAssistantPost(assistant = assistant, content = content, replyToId = post.id)
    }

    private suspend fun createAssistantPost(
        assistant: Assistant,
        content: String,
        replyToId: String? = null,
    ): String? {
        val normalized = sanitizeGeneratedText(content)
        if (normalized.isBlank()) return null
        val parent = replyToId?.let { repository.getById(it) }
        val id = Uuid.random().toString()
        val now = System.currentTimeMillis()
        val post = XPostEntity(
            id = id,
            parentPostId = parent?.id,
            rootPostId = parent?.rootPostId ?: id,
            assistantId = assistant.id.toString(),
            authorKind = BOT_KIND,
            authorName = assistant.name.ifBlank { "Bot" },
            authorHandle = assistant.toHandle(),
            content = normalized,
            likeCount = if (parent == null) randomLikes() else 0,
            replyCount = 0,
            repostCount = if (parent == null) randomReposts() else 0,
            viewCount = if (parent == null) randomViews(userPost = false) else 0,
            createAt = now,
            updateAt = now,
        )
        repository.upsert(post)
        parent?.let { repository.upsert(it.copy(replyCount = it.replyCount + 1, updateAt = now)) }
        return id
    }

    private suspend fun generateAssistantCopy(
        settings: Settings,
        assistant: Assistant,
        prompt: String,
    ): String {
        val memories = runCatching {
            memoryRepository.getMemoriesOfAssistant(assistant.id.toString())
        }.getOrDefault(emptyList())
        val memoryText = memories.take(4).joinToString("\n") { "- ${it.content}" }
        val systemPrompt = buildString {
            append("你正在运营一个类似 X/Twitter 的社交媒体账号。")
            append("请只输出最终帖子正文，不要解释，不要 Markdown，不要列表，不要引号。")
            append("语气要像真人，内容短，适合手机社交平台。")
            if (assistant.systemPrompt.isNotBlank()) {
                append("\n\n账号设定：${assistant.systemPrompt}")
            }
            if (memoryText.isNotBlank()) {
                append("\n\n角色记忆：\n$memoryText")
            }
        }
        val model = settings.findModelById(assistant.chatModelId ?: settings.chatModelId)
        val provider = model?.findProvider(settings.providers)
        if (model == null || provider == null) {
            return fallbackGeneratedText(assistant, prompt)
        }
        return runCatching {
            val providerHandler = providerManager.getProviderByType(provider)
            val result = providerHandler.generateText(
                providerSetting = provider,
                messages = listOf(
                    UIMessage.system(prompt = systemPrompt),
                    UIMessage.user(prompt = prompt),
                ),
                params = TextGenerationParams(
                    model = model,
                    temperature = assistant.temperature,
                    topP = assistant.topP,
                    maxTokens = assistant.maxTokens ?: 256,
                    thinkingBudget = assistant.thinkingBudget?.coerceAtMost(256) ?: 0,
                    customHeaders = buildList {
                        addAll(assistant.customHeaders)
                        addAll(model.customHeaders)
                    },
                    customBody = buildList {
                        addAll(assistant.customBodies)
                        addAll(model.customBodies)
                    }
                )
            )
            sanitizeGeneratedText(result.choices.firstOrNull()?.message?.toText().orEmpty())
        }.getOrElse {
            Log.e(TAG, "generateAssistantCopy failed: ${assistant.name}", it)
            fallbackGeneratedText(assistant, prompt)
        }
    }

    private suspend fun pickAutomationAssistant(settings: Settings): Assistant? {
        val bots = settings.getBotAssistants()
        if (bots.isEmpty()) return null
        var selected: Assistant? = null
        var oldestPostTime = Long.MAX_VALUE
        bots.forEach { assistant ->
            val latestTime = repository.getLatestPostByAssistant(assistant.id.toString())?.createAt ?: 0L
            if (selected == null || latestTime < oldestPostTime) {
                selected = assistant
                oldestPostTime = latestTime
            }
        }
        return selected
    }

    private fun sanitizeUserText(text: String): String {
        return text.trim().replace("\r\n", "\n").replace(Regex("\n{3,}"), "\n\n").take(560)
    }

    private fun sanitizeGeneratedText(text: String): String {
        return text
            .trim()
            .removePrefix("\"")
            .removeSuffix("\"")
            .removePrefix("“")
            .removeSuffix("”")
            .replace(Regex("""```[\s\S]*?```"""), "")
            .replace(Regex("""^[-*\d.\s]+"""), "")
            .replace("#", "")
            .replace("\r\n", "\n")
            .replace(Regex("\n{3,}"), "\n\n")
            .take(220)
            .trim()
    }

    private fun fallbackGeneratedText(assistant: Assistant, prompt: String): String {
        val subject = prompt.substringAfterLast("原帖内容：", prompt).trim().take(56).ifBlank { "这条动态" }
        return when ((assistant.name.hashCode().absoluteValue + prompt.length) % 4) {
            0 -> "这个点我同意，尤其是 ${subject.take(24)} 这部分，后续值得继续跟进。"
            1 -> "我更关心的是执行层面怎么落地，${subject.take(30)} 其实还有不少可展开的空间。"
            2 -> "这条我会先记一下，${subject.take(28)} 的反馈很有代表性，也说明大家真的在用。"
            else -> "如果从实际体验看，${subject.take(26)} 已经挺说明问题了，继续观察后续数据。"
        }
    }

    private fun fallbackAutoPost(assistant: Assistant, recentPosts: List<XPostEntity>): String {
        val topic = recentPosts.firstOrNull()?.content?.take(22)?.ifBlank { null }
        val base = listOf(
            "今天继续把交互细节往真实产品打磨，速度和手感都比堆功能更重要。",
            "如果一个功能不能自然融进主链路，那它就还没准备好上线。",
            "很多体验问题不是缺功能，而是状态切换、反馈节奏和默认值没想透。",
            "最近最明显的感受是：越像真实产品，越需要把微小交互做到位。",
        )
        return topic?.let { "${base[(assistant.name.hashCode().absoluteValue) % base.size]} 另外，${it} 这件事我也还在继续观察。" }
            ?: base[(assistant.name.hashCode().absoluteValue) % base.size]
    }

    private fun Assistant.toHandle(): String {
        val cleaned = name.lowercase()
            .replace(Regex("[^a-z0-9\u4e00-\u9fa5]+"), "")
            .ifBlank { "bot${id.toString().take(6)}" }
        return "@${cleaned.take(18)}"
    }

    private fun randomLikes(): Int = Random.nextInt(6, 64)

    private fun randomReposts(): Int = Random.nextInt(1, 18)

    private fun randomViews(userPost: Boolean): Int = if (userPost) Random.nextInt(8, 48) else Random.nextInt(96, 520)
}
