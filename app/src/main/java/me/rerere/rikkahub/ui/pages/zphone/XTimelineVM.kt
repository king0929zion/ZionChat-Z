package me.rerere.rikkahub.ui.pages.zphone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.rerere.rikkahub.data.datastore.Settings
import me.rerere.rikkahub.data.datastore.SettingsStore
import me.rerere.rikkahub.data.datastore.getBotAssistants
import me.rerere.rikkahub.data.db.entity.XPostEntity
import me.rerere.rikkahub.service.XTimelineService

class XTimelineVM(
    private val settingsStore: SettingsStore,
    private val timelineService: XTimelineService,
) : ViewModel() {
    private var bootstrapped = false

    val settings: StateFlow<Settings> = settingsStore.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, Settings.dummy())

    val bots = settingsStore.settingsFlow
        .map { it.getBotAssistants() }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val feed: StateFlow<List<XPostEntity>> = timelineService.observeFeed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())

    private val _selectedPostId = MutableStateFlow<String?>(null)
    val selectedPostId: StateFlow<String?> = _selectedPostId

    val selectedPost: StateFlow<XPostEntity?> = _selectedPostId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else timelineService.observePost(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), null)

    val replies: StateFlow<List<XPostEntity>> = _selectedPostId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else timelineService.observeReplies(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())

    private val _aiPosting = MutableStateFlow(false)
    val aiPosting: StateFlow<Boolean> = _aiPosting

    private val _submitting = MutableStateFlow(false)
    val submitting: StateFlow<Boolean> = _submitting

    private val _requestingBotReplies = MutableStateFlow(false)
    val requestingBotReplies: StateFlow<Boolean> = _requestingBotReplies

    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()

    fun bootstrap() {
        if (bootstrapped) return
        bootstrapped = true
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
            viewModelScope.launch { _events.emit("帖子内容不能为空") }
            return
        }
        viewModelScope.launch {
            _submitting.value = true
            val id = timelineService.createUserPost(normalized)
            _submitting.value = false
            if (id != null) {
                openPost(id)
                _events.emit("帖子已发布")
            } else {
                _events.emit("帖子发布失败")
            }
        }
    }

    fun submitReply(text: String) {
        val parentId = _selectedPostId.value ?: return
        val normalized = text.trim()
        if (normalized.isBlank()) {
            viewModelScope.launch { _events.emit("回复内容不能为空") }
            return
        }
        viewModelScope.launch {
            _submitting.value = true
            val id = timelineService.createUserPost(normalized, replyToId = parentId)
            _submitting.value = false
            if (id != null) {
                _events.emit("回复已发布")
            } else {
                _events.emit("回复发布失败")
            }
        }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch { timelineService.toggleLike(postId) }
    }

    fun toggleRepost(postId: String) {
        viewModelScope.launch { timelineService.toggleRepost(postId) }
    }

    fun toggleBookmark(postId: String) {
        viewModelScope.launch { timelineService.toggleBookmark(postId) }
    }

    fun generateAiPost() {
        viewModelScope.launch {
            _aiPosting.value = true
            val id = timelineService.generateAssistantPostNow()
            _aiPosting.value = false
            if (id != null) {
                openPost(id)
                _events.emit("AI 已代发一条新帖")
            } else {
                _events.emit("当前没有可用 bot 生成帖子")
            }
        }
    }

    fun requestBotReplies() {
        val postId = _selectedPostId.value ?: return
        viewModelScope.launch {
            _requestingBotReplies.value = true
            timelineService.requestBotReplies(postId, force = true)
            _requestingBotReplies.value = false
            _events.emit("已请求 bots 继续回复")
        }
    }
}
