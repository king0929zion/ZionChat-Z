package me.rerere.rikkahub.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.rerere.rikkahub.AppScope
import me.rerere.rikkahub.data.datastore.SettingsStore
import me.rerere.rikkahub.data.datastore.getCurrentAssistant
import me.rerere.rikkahub.data.datastore.isPersonalization
import me.rerere.rikkahub.data.db.entity.XPostEntity
import me.rerere.rikkahub.data.repository.XPostRepository
import kotlin.random.Random
import kotlin.uuid.Uuid

private const val USER_KIND = "user"
private const val ASSISTANT_KIND = "assistant"
private const val SEED_KIND = "seed"

class XTimelineService(
    private val appScope: AppScope,
    private val settingsStore: SettingsStore,
    private val repository: XPostRepository,
) {
    @Volatile
    private var bootstrapped = false

    fun bootstrap() {
        if (bootstrapped) return
        bootstrapped = true
        appScope.launch {
            ensureSeedData()
        }
    }

    fun observeFeed(): Flow<List<XPostEntity>> = repository.observeFeed()

    fun observePost(id: String): Flow<XPostEntity?> = repository.observePost(id)

    fun observeReplies(parentId: String): Flow<List<XPostEntity>> = repository.observeReplies(parentId)

    suspend fun getPost(id: String): XPostEntity? = repository.getById(id)

    suspend fun getFeedSnapshot(limit: Int = 12): List<XPostEntity> = repository.getRecentFeed(limit)

    suspend fun createUserPost(content: String, replyToId: String? = null): String? {
        val normalized = sanitizeText(content, maxLength = 280)
        if (normalized.isBlank()) return null

        val settings = settingsStore.settingsFlow.first()
        val nickname = settings.displaySetting.userNickname.ifBlank { "你" }

        return createPost(
            authorKind = USER_KIND,
            authorName = nickname,
            authorHandle = "@you",
            content = normalized,
            replyToId = replyToId
        )
    }

    suspend fun createToolPost(content: String, replyToId: String? = null): String? {
        val normalized = sanitizeText(content, maxLength = 280)
        if (normalized.isBlank()) return null

        val settings = settingsStore.settingsFlow.first()
        val assistant = settings.getCurrentAssistant()

        return if (assistant.isPersonalization()) {
            createUserPost(normalized, replyToId)
        } else {
            createPost(
                authorKind = ASSISTANT_KIND,
                authorName = assistant.name.ifBlank { "X Assistant" },
                authorHandle = buildHandle(
                    assistant.name.ifBlank { "xassistant" },
                    fallback = "xassistant"
                ),
                content = normalized,
                replyToId = replyToId
            )
        }
    }

    suspend fun toggleLike(postId: String) {
        repository.getById(postId)?.let { post ->
            setLike(postId = post.id, liked = !post.likedByUser)
        }
    }

    suspend fun toggleRepost(postId: String) {
        repository.getById(postId)?.let { post ->
            setRepost(postId = post.id, reposted = !post.repostedByUser)
        }
    }

    suspend fun toggleBookmark(postId: String) {
        repository.getById(postId)?.let { post ->
            setBookmark(postId = post.id, bookmarked = !post.bookmarkedByUser)
        }
    }

    suspend fun markViewed(postId: String) {
        repository.getById(postId)?.let { post ->
            repository.upsert(
                post.copy(
                    viewCount = post.viewCount + 1,
                    updateAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun setLike(postId: String, liked: Boolean): XPostEntity? {
        val post = repository.getById(postId) ?: return null
        if (post.likedByUser == liked) return post

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
        if (post.repostedByUser == reposted) return post

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
        if (post.bookmarkedByUser == bookmarked) return post

        val updated = post.copy(
            bookmarkedByUser = bookmarked,
            updateAt = System.currentTimeMillis(),
        )
        repository.upsert(updated)
        return updated
    }

    private suspend fun createPost(
        authorKind: String,
        authorName: String,
        authorHandle: String,
        content: String,
        replyToId: String? = null,
    ): String {
        val parent = replyToId?.let { repository.getById(it) }
        val id = Uuid.random().toString()
        val now = System.currentTimeMillis()
        val isRootPost = parent == null

        val post = XPostEntity(
            id = id,
            parentPostId = parent?.id,
            rootPostId = parent?.rootPostId ?: id,
            authorKind = authorKind,
            authorName = authorName,
            authorHandle = authorHandle,
            content = content,
            likeCount = if (isRootPost) Random.nextInt(0, 18) else 0,
            replyCount = 0,
            repostCount = if (isRootPost) Random.nextInt(0, 6) else 0,
            viewCount = if (isRootPost) Random.nextInt(24, 680) else 0,
            createAt = now,
            updateAt = now,
        )

        repository.upsert(post)

        parent?.let {
            repository.upsert(
                it.copy(
                    replyCount = it.replyCount + 1,
                    updateAt = now
                )
            )
        }

        return id
    }

    private suspend fun ensureSeedData() {
        if (repository.countAll() > 0) return

        val now = System.currentTimeMillis()

        val scobleId = Uuid.random().toString()
        val grokReplyId = Uuid.random().toString()
        val elonId = Uuid.random().toString()
        val leoId = Uuid.random().toString()

        repository.insertAll(
            listOf(
                XPostEntity(
                    id = scobleId,
                    rootPostId = scobleId,
                    authorKind = SEED_KIND,
                    authorName = "Robert Scoble",
                    authorHandle = "@scobleizer",
                    content = "Did someone turn the speed knob up on @Grok?\n\nJust did another transcript reading and, damn, it is way faster than last time I asked it to do the same.",
                    likeCount = 302,
                    replyCount = 1,
                    repostCount = 15,
                    viewCount = 5_710_000,
                    createAt = now - 7L * 60L * 60L * 1000L,
                    updateAt = now - 7L * 60L * 60L * 1000L,
                ),
                XPostEntity(
                    id = grokReplyId,
                    parentPostId = scobleId,
                    rootPostId = scobleId,
                    authorKind = ASSISTANT_KIND,
                    authorName = "Grok",
                    authorHandle = "@grok",
                    content = "Thanks! We've been optimizing hard at xAI—speed improvements like this are rolling out fast. Glad the transcript readings feel snappier. What else are you testing?",
                    likeCount = 46,
                    replyCount = 0,
                    repostCount = 8,
                    viewCount = 1_200,
                    createAt = now - 7L * 60L * 60L * 1000L,
                    updateAt = now - 7L * 60L * 60L * 1000L,
                ),
                XPostEntity(
                    id = elonId,
                    rootPostId = elonId,
                    authorKind = SEED_KIND,
                    authorName = "Elon Musk",
                    authorHandle = "@elonmusk",
                    content = "Grok gets faster & smarter every week",
                    quoteAuthorName = "Robert Scoble",
                    quoteHandle = "@Scobl...",
                    quoteContent = "Did someone turn the speed knob up on @Grok?\n\nJust did another transcript reading and, damn, it is way faster than last ...",
                    likeCount = 10_000,
                    replyCount = 1_711,
                    repostCount = 1_141,
                    viewCount = 5_680_000,
                    createAt = now - 2L * 60L * 60L * 1000L,
                    updateAt = now - 2L * 60L * 60L * 1000L,
                ),
                XPostEntity(
                    id = leoId,
                    rootPostId = leoId,
                    authorKind = SEED_KIND,
                    authorName = "leo",
                    authorHandle = "@synthwavedd",
                    content = "The \$200/mo ChatGPT Pro plan will soon no longer be unlimited\n\nI think we all knew this was coming, but still",
                    quoteAuthorName = "Chetaslua",
                    quoteHandle = "@chetaslua",
                    quoteContent = "Chatgpt 100\$ plan is coming soon\n\nOne bad news - now the 200\$ plan is not truly unlimited ( it's 20 times the plus uses)...",
                    quotePreview = "wireframe",
                    likeCount = 7_160,
                    replyCount = 228,
                    repostCount = 964,
                    viewCount = 2_300_000,
                    createAt = now - 60L * 60L * 1000L,
                    updateAt = now - 60L * 60L * 1000L,
                )
            )
        )
    }

    private fun sanitizeText(text: String, maxLength: Int): String {
        return text
            .trim()
            .replace("\r\n", "\n")
            .replace(Regex("\n{3,}"), "\n\n")
            .take(maxLength)
    }

    private fun buildHandle(name: String, fallback: String): String {
        val normalized = name
            .lowercase()
            .replace(Regex("[^a-z0-9\u4e00-\u9fa5]+"), "")
            .ifBlank { fallback }
            .take(18)
        return "@$normalized"
    }
}
