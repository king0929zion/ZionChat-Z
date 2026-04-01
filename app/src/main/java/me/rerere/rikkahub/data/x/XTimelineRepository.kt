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

@Serializable
data class XAuthor(
    val id: String,
    val displayName: String,
    val handle: String,
    val bio: String,
    val initials: String,
    val avatarColorHex: Long,
    val verified: Boolean = false,
)

@Serializable
data class XMedia(
    val id: String,
    val label: String,
    val subtitle: String = "",
    val tintHex: Long,
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
)

@Serializable
data class XTimelineState(
    val currentUserId: String,
    val authors: List<XAuthor>,
    val posts: List<XPost>,
    val lastUpdatedAt: Long = System.currentTimeMillis(),
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
        val loaded = withContext(Dispatchers.IO) {
            if (!storageFile.exists()) {
                null
            } else {
                runCatching {
                    json.decodeFromString<XTimelineState>(storageFile.readText())
                }.getOrNull()
            }
        } ?: defaultState()
        val sanitized = loaded.sanitize(defaultState())

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
        val currentUser = XAuthor(
            id = "author-me",
            displayName = "Zion",
            handle = "@zion_ai",
            bio = "Building native AI tools and shipping fast.",
            initials = "Z",
            avatarColorHex = 0xFF1D9BF0,
            verified = false,
        )
        val authors = listOf(
            currentUser,
            XAuthor(
                id = "author-openai",
                displayName = "OpenAI",
                handle = "@OpenAI",
                bio = "Research and deployment company focused on safe AGI.",
                initials = "OA",
                avatarColorHex = 0xFF111214,
                verified = true,
            ),
            XAuthor(
                id = "author-grok",
                displayName = "Grok",
                handle = "@grok",
                bio = "Ask for live context, hot takes and fast synthesis.",
                initials = "G",
                avatarColorHex = 0xFF0F1419,
                verified = true,
            ),
            XAuthor(
                id = "author-design",
                displayName = "Rin Design",
                handle = "@rin_signal",
                bio = "Interface systems, motion and product details.",
                initials = "RD",
                avatarColorHex = 0xFFFF9F0A,
                verified = false,
            ),
            XAuthor(
                id = "author-builder",
                displayName = "Dev Notes",
                handle = "@devnotes",
                bio = "Shipping notes for multi-tool AI products.",
                initials = "DN",
                avatarColorHex = 0xFF34C759,
                verified = false,
            )
        )
        val now = System.currentTimeMillis()
        val quotedPost = XPost(
            id = "post-quoted-spec",
            authorId = "author-design",
            body = "A calm AI product UI should feel like one system: light surfaces, readable type, controlled accent usage, and motion that explains state.",
            createdAtMillis = now - 3_600_000L * 9,
            likeCount = 218,
            replyCount = 21,
            repostCount = 34,
            bookmarkCount = 18,
            viewCount = 8_200,
            tags = listOf("For You", "AI Watch")
        )
        val posts = listOf(
            quotedPost,
            XPost(
                id = "post-openai",
                authorId = "author-openai",
                body = "Tool calling feels much better when the UI shows intent, approval state and result summary in one place instead of hiding everything in raw JSON.",
                createdAtMillis = now - 3_600_000L * 2,
                quotePostId = quotedPost.id,
                likeCount = 1_420,
                replyCount = 83,
                repostCount = 265,
                bookmarkCount = 117,
                viewCount = 148_300,
                media = listOf(
                    XMedia(
                        id = "media-motion",
                        label = "Motion Spec",
                        subtitle = "120ms - 220ms / transform + opacity",
                        tintHex = 0xFFE8F4FD
                    )
                ),
                tags = listOf("For You", "Following", "AI Watch")
            ),
            XPost(
                id = "post-grok-speed",
                authorId = "author-grok",
                body = "If your app has tools, make them visible to the user. Invisible automation feels like magic until it breaks; visible automation feels trustworthy.",
                createdAtMillis = now - 3_600_000L * 4,
                likeCount = 893,
                replyCount = 44,
                repostCount = 133,
                bookmarkCount = 52,
                viewCount = 96_500,
                tags = listOf("For You", "AI Watch")
            ),
            XPost(
                id = "post-builder",
                authorId = "author-builder",
                body = "Rebuilding the X module natively. No legacy carry-over, one shared state for feed, plugin settings and AI actions.",
                createdAtMillis = now - 3_600_000L,
                likeCount = 302,
                replyCount = 19,
                repostCount = 41,
                bookmarkCount = 11,
                viewCount = 32_400,
                tags = listOf("For You", "Following")
            ),
            XPost(
                id = "post-reply-one",
                authorId = "author-design",
                body = "The key is to animate state change, not decorate the whole page. Small transitions make the app feel alive without stealing focus.",
                createdAtMillis = now - 1_800_000L,
                replyToPostId = "post-builder",
                likeCount = 71,
                replyCount = 0,
                repostCount = 5,
                bookmarkCount = 3,
                viewCount = 4_300,
                tags = listOf("Replies")
            )
        )
        return XTimelineState(
            currentUserId = currentUser.id,
            authors = authors,
            posts = posts,
            lastUpdatedAt = now,
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
    )
}
