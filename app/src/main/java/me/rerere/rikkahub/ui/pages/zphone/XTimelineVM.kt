package me.rerere.rikkahub.ui.pages.zphone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.rerere.rikkahub.data.datastore.Settings
import me.rerere.rikkahub.data.datastore.SettingsStore
import me.rerere.rikkahub.data.db.entity.XPostEntity
import me.rerere.rikkahub.service.XTimelineService

sealed interface XTimelineEvent {
    data class Error(val message: String) : XTimelineEvent

    data class PostPublished(val postId: String) : XTimelineEvent

    data object ReplyPublished : XTimelineEvent
}

class XTimelineVM(
    private val settingsStore: SettingsStore,
    private val timelineService: XTimelineService,
) : ViewModel() {
    val settings: StateFlow<Settings> = settingsStore.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, Settings.dummy())

    val feed: StateFlow<List<XPostEntity>> = timelineService.observeFeed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())

    private val _selectedPostId = MutableStateFlow<String?>(null)
    val selectedPostId: StateFlow<String?> = _selectedPostId

    val selectedPost: StateFlow<XPostEntity?> = _selectedPostId
        .flatMapLatest { id ->
            if (id == null) {
                flowOf(null)
            } else {
                timelineService.observePost(id)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), null)

    val replies: StateFlow<List<XPostEntity>> = _selectedPostId
        .flatMapLatest { id ->
            if (id == null) {
                flowOf(emptyList())
            } else {
                timelineService.observeReplies(id)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())

    private val _submitting = MutableStateFlow(false)
    val submitting: StateFlow<Boolean> = _submitting

    private val _events = MutableSharedFlow<XTimelineEvent>()
    val events = _events.asSharedFlow()

    fun bootstrap() {
        timelineService.bootstrap()
    }

    fun openPost(postId: String) {
        _selectedPostId.value = postId
        viewModelScope.launch {
            timelineService.markViewed(postId)
        }
    }

    fun closeDetail() {
        _selectedPostId.value = null
    }

    fun submitPost(text: String) {
        val normalized = text.trim()
        if (normalized.isBlank()) {
            viewModelScope.launch {
                _events.emit(XTimelineEvent.Error("帖子内容不能为空"))
            }
            return
        }

        viewModelScope.launch {
            _submitting.value = true
            val postId = timelineService.createUserPost(normalized)
            _submitting.value = false

            if (postId == null) {
                _events.emit(XTimelineEvent.Error("帖子发布失败"))
                return@launch
            }

            openPost(postId)
            _events.emit(XTimelineEvent.PostPublished(postId))
        }
    }

    fun submitReply(text: String) {
        val parentId = _selectedPostId.value ?: return
        val normalized = text.trim()
        if (normalized.isBlank()) {
            viewModelScope.launch {
                _events.emit(XTimelineEvent.Error("回复内容不能为空"))
            }
            return
        }

        viewModelScope.launch {
            _submitting.value = true
            val replyId = timelineService.createUserPost(normalized, replyToId = parentId)
            _submitting.value = false

            if (replyId == null) {
                _events.emit(XTimelineEvent.Error("回复发布失败"))
                return@launch
            }

            _events.emit(XTimelineEvent.ReplyPublished)
        }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            timelineService.toggleLike(postId)
        }
    }

    fun toggleRepost(postId: String) {
        viewModelScope.launch {
            timelineService.toggleRepost(postId)
        }
    }

    fun toggleBookmark(postId: String) {
        viewModelScope.launch {
            timelineService.toggleBookmark(postId)
        }
    }
}
