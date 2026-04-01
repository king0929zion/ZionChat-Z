package me.rerere.rikkahub.ui.pages.xapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.rerere.rikkahub.data.x.XAuthor
import me.rerere.rikkahub.data.x.XPost
import me.rerere.rikkahub.data.x.XResolvedPost
import me.rerere.rikkahub.data.x.XTimelineRepository
import me.rerere.rikkahub.data.x.XTimelineState
import me.rerere.rikkahub.data.x.findAuthor
import me.rerere.rikkahub.data.x.repliesFor
import me.rerere.rikkahub.data.x.resolvePost
import me.rerere.rikkahub.data.x.topLevelPosts

enum class XFeedTab(
    val title: String,
    val filterTag: String,
) {
    FOR_YOU("为你推荐", "For You"),
    FOLLOWING("正在关注", "Following"),
    AI_WATCH("AI 工具", "AI Watch"),
}

class XAppVM(
    private val repository: XTimelineRepository,
) : ViewModel() {
    val timeline: StateFlow<XTimelineState> = repository.state

    fun currentUser(state: XTimelineState): XAuthor? = state.findAuthor(state.currentUserId)

    fun topLevelPosts(
        state: XTimelineState,
        tab: XFeedTab,
    ): List<XResolvedPost> {
        return state.topLevelPosts().filter { resolved ->
            tab.filterTag in resolved.post.tags
        }
    }

    fun selectedPost(
        state: XTimelineState,
        postId: String?,
    ): XResolvedPost? {
        return postId?.let(state::resolvePost)
    }

    fun repliesFor(
        state: XTimelineState,
        postId: String?,
    ): List<XResolvedPost> {
        return postId?.let(state::repliesFor).orEmpty()
    }

    fun publishPost(
        text: String,
        quotePostId: String? = null,
        onPublished: (XPost) -> Unit,
    ) {
        viewModelScope.launch {
            onPublished(repository.publishPost(text = text, quotePostId = quotePostId))
        }
    }

    fun replyToPost(
        postId: String,
        text: String,
        onPublished: (XPost) -> Unit,
    ) {
        viewModelScope.launch {
            onPublished(repository.replyToPost(postId = postId, text = text))
        }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            repository.toggleLike(postId)
        }
    }

    fun toggleRepost(postId: String) {
        viewModelScope.launch {
            repository.toggleRepost(postId)
        }
    }

    fun toggleBookmark(postId: String) {
        viewModelScope.launch {
            repository.toggleBookmark(postId)
        }
    }

    fun recordView(postId: String) {
        viewModelScope.launch {
            repository.recordView(postId)
        }
    }
}
