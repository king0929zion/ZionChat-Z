package me.rerere.rikkahub.data.x

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.rerere.rikkahub.AppScope
import java.io.File
import kotlin.uuid.Uuid

private const val CURRENT_TIMELINE_SEED_VERSION = 2
private const val HOUR_MILLIS = 3_600_000L

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

class XTimelineRepository(
    context: Context,
    private val scope: AppScope,
    private val json: Json,
) {
    private val storageFile = File(context.filesDir, "x_timeline_state.json")
    private val _state = MutableStateFlow(defaultState())
    val state: StateFlow<XTimelineState> = _state.asStateFlow()

    init {
        scope.launch {
            loadFromDisk()
        }
    }

    fun currentState(): XTimelineState = state.value

    suspend fun publishPost(
        text: String,
        quotePostId: String? = null,
        media: List<XMedia> = emptyList(),
        source: XPostSource = XPostSource.USER,
    ): XPost {
        val body = text.trim()
        require(body.isNotEmpty()) { "text is required" }

        val newPost = XPost(
            id = Uuid.random().toString(),
            authorId = state.value.currentUserId,
            body = body,
            createdAtMillis = System.currentTimeMillis(),
            quotePostId = quotePostId?.takeIf(::postExists),
            media = media,
            source = source,
            viewCount = 1,
            tags = buildList {
                add("For You")
                add("Following")
                if (source == XPostSource.AI_ASSISTANT) add("AI Watch")
            }
        )
        updateState { timeline ->
            timeline.copy(posts = listOf(newPost) + timeline.posts)
        }
        return newPost
    }

    suspend fun replyToPost(
        postId: String,
        text: String,
        source: XPostSource = XPostSource.USER,
    ): XPost {
        require(postExists(postId)) { "post not found: $postId" }
        val body = text.trim()
        require(body.isNotEmpty()) { "text is required" }

        val reply = XPost(
            id = Uuid.random().toString(),
            authorId = state.value.currentUserId,
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
    }

    fun getPost(postId: String): XPost? = state.value.posts.firstOrNull { it.id == postId }

    fun resolvePost(postId: String): XResolvedPost? = state.value.resolvePost(postId)

    fun topLevelPosts(): List<XResolvedPost> = state.value.topLevelPosts()

    fun repliesFor(postId: String): List<XResolvedPost> = state.value.repliesFor(postId)

    private fun postExists(postId: String): Boolean = getPost(postId) != null

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
            XAuthor(
                id = "author-grok",
                displayName = "Grok",
                handle = "@grok",
                bio = "Live context and fast synthesis.",
                initials = "G",
                avatarColorHex = 0xFF0F1419,
                avatarUrl = null,
            ),
        )

        val robertTranscriptPost = XPost(
            id = "post-robert-transcript",
            authorId = "author-robert",
            body = "Did someone turn the speed knob up on @Grok?\n\nJust did another transcript reading and, damn, it is way faster than last time I asked it to do the same.",
            createdAtMillis = now - 7 * HOUR_MILLIS,
            likeCount = 302,
            replyCount = 1,
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
            XPost(
                id = "post-grok-reply",
                authorId = "author-grok",
                body = "Thanks! We've been optimizing hard at xAI—speed improvements like this are rolling out fast. Glad the transcript readings feel snappier. What else are you testing?",
                createdAtMillis = now - 7 * HOUR_MILLIS,
                replyToPostId = robertTranscriptPost.id,
                source = XPostSource.AI_ASSISTANT,
                tags = listOf("Replies"),
                feedTimeLabel = "7小时",
            )
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
    val mergedAuthors = (seed.authors + authors.filterNot { it.id in seededAuthorIds })
        .distinctBy { it.id }
    val seededPostIds = seed.posts.map { it.id }.toSet()
    val mergedPosts = (seed.posts + posts.filterNot { it.id in seededPostIds })
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
        .filter { it.id.isNotBlank() }
        .distinctBy { it.id }

    if (sanitizedAuthors.isEmpty()) return fallback

    val authorIds = sanitizedAuthors.map { it.id }.toSet()
    val candidatePosts = posts
        .filter { post -> post.id.isNotBlank() && post.authorId in authorIds }
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
