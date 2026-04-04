package me.rerere.rikkahub.ui.pages.xapp

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dokar.sonner.ToastType
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.data.datastore.SettingsStore
import me.rerere.rikkahub.data.x.XAuthor
import me.rerere.rikkahub.data.x.XPostSource
import me.rerere.rikkahub.data.x.XResolvedPost
import me.rerere.rikkahub.data.x.XTimelineState
import me.rerere.rikkahub.ui.components.ui.HeaderActionButton
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.context.LocalToaster
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.theme.SourceSans3
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import kotlin.math.absoluteValue

private val XPage = Color(0xFFFCFCFC)
private val XSurface = Color(0xFFFFFFFF)
private val XMuted = Color(0xFFF1F1F1)
private val XSubtle = Color(0xFFF5F5F7)
private val XBorder = Color(0xFFE5E5EA)
private val XPrimary = Color(0xFF1C1C1E)
private val XSecondary = Color(0xFF8E8E93)
private val XAccent = Color(0xFF007AFF)
private val XAccentSoft = Color(0xFFE8F4FD)
private val XLike = Color(0xFFF91880)
private val XRepost = Color(0xFF34C759)
private const val XComposerReply = "reply"
private const val XComposerPost = "post"

private enum class XPane {
    Feed,
    Detail,
    Composer,
}

@Composable
fun XAppPage(vm: XAppVM = koinViewModel()) {
    val navController = LocalNavController.current
    val toaster = LocalToaster.current
    val settingsStore: SettingsStore = koinInject()
    val settings by settingsStore.settingsFlow.collectAsStateWithLifecycle()
    val timeline by vm.timeline.collectAsStateWithLifecycle()
    val currentUser = remember(timeline) { runCatching { vm.currentUser(timeline) }.getOrNull() }

    var selectedTab by rememberSaveable { mutableStateOf(XFeedTab.FOR_YOU) }
    var selectedPostId by rememberSaveable { mutableStateOf<String?>(null) }
    var composerVisible by rememberSaveable { mutableStateOf(false) }
    var composerMode by rememberSaveable { mutableStateOf(XComposerPost) }
    var replyTargetId by rememberSaveable { mutableStateOf<String?>(null) }
    var quotePostId by rememberSaveable { mutableStateOf<String?>(null) }

    val selectedPost = remember(timeline, selectedPostId) {
        runCatching { vm.selectedPost(timeline, selectedPostId) }.getOrNull()
    }
    val composerQuotePost = remember(timeline, quotePostId) {
        runCatching { vm.selectedPost(timeline, quotePostId) }.getOrNull()
    }
    val replyTargetPost = remember(timeline, replyTargetId) {
        runCatching { vm.selectedPost(timeline, replyTargetId) }.getOrNull()
    }
    val filteredPosts = remember(timeline, selectedTab) {
        runCatching { vm.topLevelPosts(timeline, selectedTab) }.getOrElse { emptyList() }
    }
    val detailReplies = remember(timeline, selectedPostId) {
        runCatching { vm.repliesFor(timeline, selectedPostId) }.getOrElse { emptyList() }
    }
    val pane = when {
        composerVisible -> XPane.Composer
        selectedPostId != null -> XPane.Detail
        else -> XPane.Feed
    }

    LaunchedEffect(selectedPostId, composerVisible) {
        val currentPostId = selectedPostId
        if (!composerVisible && currentPostId != null) {
            vm.recordView(currentPostId)
        }
    }

    LaunchedEffect(selectedPostId, selectedPost, composerVisible) {
        if (!composerVisible && selectedPostId != null && selectedPost == null) {
            selectedPostId = null
        }
    }

    fun openComposerForPost(quoteId: String? = null) {
        composerVisible = true
        composerMode = XComposerPost
        replyTargetId = null
        quotePostId = quoteId
    }

    fun openComposerForReply(postId: String) {
        selectedPostId = postId
        composerVisible = true
        composerMode = XComposerReply
        replyTargetId = postId
        quotePostId = null
    }

    fun closeComposer() {
        composerVisible = false
        composerMode = XComposerPost
        replyTargetId = null
        quotePostId = null
    }

    Box(modifier = Modifier.fillMaxSize().background(XPage)) {
        when (pane) {
            XPane.Feed -> XFeedPane(
                timeline = timeline,
                posts = filteredPosts,
                currentUser = currentUser,
                selectedTab = selectedTab,
                pluginEnabled = settings.pluginSettings.x.enabled,
                enabledToolCount = settings.pluginSettings.x.enabledToolCount(),
                onBack = { navController.popBackStack() },
                onOpenPlugin = { navController.navigate(Screen.SettingXPlugin) },
                onTabSelected = { selectedTab = it },
                onOpenPost = { selectedPostId = it },
                onCompose = { openComposerForPost() },
                onReply = { openComposerForReply(it) },
                onQuote = { openComposerForPost(it) },
                onLike = vm::toggleLike,
                onRepost = vm::toggleRepost,
                onBookmark = vm::toggleBookmark,
            )

            XPane.Detail -> {
                val post = selectedPost
                if (post != null) {
                    XDetailPane(
                        currentUser = currentUser,
                        post = post,
                        replies = detailReplies,
                        onBack = { selectedPostId = null },
                        onOpenPlugin = { navController.navigate(Screen.SettingXPlugin) },
                        onReply = { openComposerForReply(post.post.id) },
                        onQuote = { openComposerForPost(post.post.id) },
                        onOpenPost = { selectedPostId = it },
                        onLike = vm::toggleLike,
                        onRepost = vm::toggleRepost,
                        onBookmark = vm::toggleBookmark,
                    )
                }
            }

            XPane.Composer -> XComposerPane(
                currentUser = currentUser,
                mode = composerMode,
                replyTarget = replyTargetPost,
                quotePost = composerQuotePost,
                onClose = { closeComposer() },
                onSubmit = { text ->
                    val currentReplyTargetId = replyTargetId
                    if (composerMode == XComposerReply && currentReplyTargetId != null) {
                        vm.replyToPost(currentReplyTargetId, text) {
                            toaster.show("回复已发出", type = ToastType.Success)
                            selectedPostId = currentReplyTargetId
                            closeComposer()
                        }
                    } else {
                        vm.publishPost(text = text, quotePostId = quotePostId) { post ->
                            toaster.show("帖子已发布", type = ToastType.Success)
                            selectedPostId = post.id
                            closeComposer()
                        }
                    }
                },
            )
        }
    }
}

@Composable
private fun XFeedPane(
    timeline: XTimelineState,
    posts: List<XResolvedPost>,
    currentUser: XAuthor?,
    selectedTab: XFeedTab,
    pluginEnabled: Boolean,
    enabledToolCount: Int,
    onBack: () -> Unit,
    onOpenPlugin: () -> Unit,
    onTabSelected: (XFeedTab) -> Unit,
    onOpenPost: (String) -> Unit,
    onCompose: () -> Unit,
    onReply: (String) -> Unit,
    onQuote: (String) -> Unit,
    onLike: (String) -> Unit,
    onRepost: (String) -> Unit,
    onBookmark: (String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 142.dp, bottom = 116.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item("pluginBanner") {
                XPluginBanner(
                    pluginEnabled = pluginEnabled,
                    enabledToolCount = enabledToolCount,
                    onClick = onOpenPlugin
                )
            }
            if (currentUser != null) {
                item("meCard") {
                    XMeCard(author = currentUser, timelineCount = timeline.posts.size)
                }
            }
            if (posts.isEmpty()) {
                item("empty") {
                    XEmptyState(
                        title = "这里还没有内容",
                        subtitle = "切换一下分栏，或者先发一条新的帖子。",
                        action = "发帖",
                        onAction = onCompose
                    )
                }
            } else {
                items(posts, key = { it.post.id }) { post ->
                    XPostCard(
                        post = post,
                        onOpen = { onOpenPost(post.post.id) },
                        onReply = { onReply(post.post.id) },
                        onQuote = { onQuote(post.post.id) },
                        onLike = { onLike(post.post.id) },
                        onRepost = { onRepost(post.post.id) },
                        onBookmark = { onBookmark(post.post.id) },
                    )
                }
            }
        }

        Column(
            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth()
        ) {
            XTopBar(
                onBack = onBack,
                onOpenPlugin = onOpenPlugin,
                title = {
                    XWordmark()
                }
            )
            XTabBar(selectedTab = selectedTab, onTabSelected = onTabSelected)
        }

        XFloatingComposer(
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 92.dp),
            onClick = onCompose
        )
    }
}

@Composable
private fun XDetailPane(
    currentUser: XAuthor?,
    post: XResolvedPost,
    replies: List<XResolvedPost>,
    onBack: () -> Unit,
    onOpenPlugin: () -> Unit,
    onReply: () -> Unit,
    onQuote: () -> Unit,
    onOpenPost: (String) -> Unit,
    onLike: (String) -> Unit,
    onRepost: (String) -> Unit,
    onBookmark: (String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 100.dp, bottom = 116.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item("detailHeader") {
                XSectionHeader(
                    title = "帖子详情",
                    subtitle = "查看互动、回复和引用上下文"
                )
            }
            item("mainPost") {
                XPostCard(
                    post = post,
                    emphasized = true,
                    onOpen = {},
                    onReply = onReply,
                    onQuote = onQuote,
                    onLike = { onLike(post.post.id) },
                    onRepost = { onRepost(post.post.id) },
                    onBookmark = { onBookmark(post.post.id) },
                )
            }
            item("repliesHeader") {
                XSectionHeader(
                    title = "回复 ${replies.size}",
                    subtitle = if (currentUser != null) "以 ${currentUser.handle} 的身份继续参与讨论" else "继续参与讨论"
                )
            }
            if (replies.isEmpty()) {
                item("emptyReplies") {
                    XEmptyState(
                        title = "还没有回复",
                        subtitle = "写下你的第一条回复，或者让 AI 在审批后代你完成。",
                        action = "写回复",
                        onAction = onReply
                    )
                }
            } else {
                items(replies, key = { it.post.id }) { reply ->
                    XPostCard(
                        post = reply,
                        compact = true,
                        onOpen = { onOpenPost(reply.post.id) },
                        onReply = onReply,
                        onQuote = onQuote,
                        onLike = { onLike(reply.post.id) },
                        onRepost = { onRepost(reply.post.id) },
                        onBookmark = { onBookmark(reply.post.id) },
                    )
                }
            }
        }

        XTopBar(
            modifier = Modifier.align(Alignment.TopCenter),
            onBack = onBack,
            onOpenPlugin = onOpenPlugin,
            title = {
                Text(
                    text = post.author.displayName,
                    color = XPrimary,
                    fontFamily = SourceSans3,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        )

        XBottomReplyBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            currentUser = currentUser,
            onReply = onReply
        )
    }
}

@Composable
private fun XComposerPane(
    currentUser: XAuthor?,
    mode: String,
    replyTarget: XResolvedPost?,
    quotePost: XResolvedPost?,
    onClose: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    var text by rememberSaveable(mode, replyTarget?.post?.id, quotePost?.post?.id) { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(XPage)) {
        XComposerTopBar(
            canSubmit = text.trim().isNotEmpty(),
            submitLabel = if (mode == XComposerReply) "回复" else "发布",
            onBack = onClose,
            onSubmit = { onSubmit(text.trim()) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .imePadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentUser != null) {
                XComposerIdentity(author = currentUser, mode = mode)
            }
            if (replyTarget != null) {
                XContextPreview(title = "回复目标", post = replyTarget)
            }
            if (quotePost != null) {
                XContextPreview(title = "引用帖子", post = quotePost)
            }

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = XSurface)) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth().height(280.dp),
                    placeholder = {
                        Text(
                            text = if (mode == XComposerReply) "写下你的回复内容" else "分享一个新想法，或引用一条帖子继续展开",
                            color = XSecondary
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                    maxLines = 10,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = XSurface,
                        unfocusedContainerColor = XSurface,
                        disabledContainerColor = XSurface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = XAccent
                    )
                )
            }

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = XSubtle)) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "工具协同说明",
                        color = XPrimary,
                        fontFamily = SourceSans3,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = "这里的发帖和回复是原生交互。聊天里的 AI 若要执行同类动作，仍会经过单独审批，不会绕过你的确认。",
                        color = XSecondary,
                        fontFamily = SourceSans3,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun XTopBar(
    onBack: () -> Unit,
    onOpenPlugin: () -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth().background(XPage)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderActionButton(
                onClick = onBack,
                icon = ZionAppIcons.Back,
                contentDescription = "返回"
            )
            Box(contentAlignment = Alignment.Center) {
                title()
            }
            HeaderActionButton(
                onClick = onOpenPlugin,
                icon = ZionAppIcons.Settings,
                contentDescription = "X 插件设置"
            )
        }
        HorizontalDivider(color = XBorder)
    }
}

@Composable
private fun XTabBar(
    selectedTab: XFeedTab,
    onTabSelected: (XFeedTab) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(XPage)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(XSubtle)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            XFeedTab.entries.forEach { tab ->
                val selected = tab == selectedTab
                val background by animateColorAsState(
                    targetValue = if (selected) XSurface else Color.Transparent,
                    animationSpec = tween(180, easing = FastOutSlowInEasing),
                    label = "tabBackground"
                )
                val textColor by animateColorAsState(
                    targetValue = if (selected) XPrimary else XSecondary,
                    animationSpec = tween(180, easing = FastOutSlowInEasing),
                    label = "tabColor"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(background)
                        .pressableScale(pressedScale = 0.98f) { onTabSelected(tab) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.title,
                        color = textColor,
                        fontFamily = SourceSans3,
                        fontSize = 14.sp,
                        fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

@Composable
private fun XPluginBanner(
    pluginEnabled: Boolean,
    enabledToolCount: Int,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = if (pluginEnabled) XAccentSoft else XSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pressableScale(pressedScale = 0.98f, onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (pluginEnabled) "AI 已接入 X" else "启用 X 插件",
                    color = XPrimary,
                    fontFamily = SourceSans3,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = if (pluginEnabled) {
                        "当前有 $enabledToolCount 个工具可主动调用，写入类动作会单独审批。"
                    } else {
                        "开启后，聊天里的 AI 可以读取时间线、查看帖子详情并触发互动动作。"
                    },
                    color = XSecondary,
                    fontFamily = SourceSans3,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(XSurface)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "配置",
                    color = XPrimary,
                    fontFamily = SourceSans3,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

@Composable
private fun XMeCard(
    author: XAuthor,
    timelineCount: Int,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = XSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            XAvatar(author = author, size = 46.dp)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = author.displayName,
                    color = XPrimary,
                    fontFamily = SourceSans3,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "${author.handle} · 本地时间线共 $timelineCount 条帖子",
                    color = XSecondary,
                    fontFamily = SourceSans3,
                    fontSize = 14.sp,
                )
                Text(
                    text = author.bio,
                    color = XPrimary.copy(alpha = 0.82f),
                    fontFamily = SourceSans3,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun XPostCard(
    post: XResolvedPost,
    compact: Boolean = false,
    emphasized: Boolean = false,
    onOpen: () -> Unit,
    onReply: () -> Unit,
    onQuote: () -> Unit,
    onLike: () -> Unit,
    onRepost: () -> Unit,
    onBookmark: () -> Unit,
) {
    val horizontalPadding = if (compact) 14.dp else 16.dp
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = if (emphasized) XSurface else XSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .pressableScale(pressedScale = 0.985f, onClick = onOpen)
                .padding(horizontal = horizontalPadding, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                XAvatar(author = post.author, size = if (compact) 40.dp else 44.dp)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = post.author.displayName,
                            color = XPrimary,
                            fontFamily = SourceSans3,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        if (post.author.verified) {
                            XStatusChip(text = "认证", background = XAccentSoft, content = XAccent)
                        }
                        if (post.post.source == XPostSource.AI_ASSISTANT) {
                            XStatusChip(text = "AI", background = XMuted, content = XPrimary)
                        }
                    }
                    Text(
                        text = "${post.author.handle} · ${relativeTime(post.post.createdAtMillis)}",
                        color = XSecondary,
                        fontFamily = SourceSans3,
                        fontSize = 13.sp,
                    )
                }
            }

            Text(
                text = post.post.body,
                color = XPrimary,
                fontFamily = SourceSans3,
                fontSize = if (compact) 15.sp else 16.sp,
                lineHeight = if (compact) 21.sp else 24.sp,
            )

            if (post.quotedPost != null) {
                XQuoteCard(post = post.quotedPost)
            }

            if (post.post.media.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    post.post.media.forEach { media ->
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = xColor(media.tintHex, XAccentSoft))
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = media.label,
                                    color = XPrimary,
                                    fontFamily = SourceSans3,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                                if (media.subtitle.isNotBlank()) {
                                    Text(
                                        text = media.subtitle,
                                        color = XPrimary.copy(alpha = 0.72f),
                                        fontFamily = SourceSans3,
                                        fontSize = 13.sp,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = XBorder)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                XMetricAction("评", countLabel(post.post.replyCount), XSecondary, onReply)
                XMetricAction(
                    "转",
                    countLabel(post.post.repostCount),
                    if (post.post.repostedByMe) XRepost else XSecondary,
                    onRepost
                )
                XMetricAction(
                    "赞",
                    countLabel(post.post.likeCount),
                    if (post.post.likedByMe) XLike else XSecondary,
                    onLike
                )
                XMetricAction(
                    "藏",
                    countLabel(post.post.bookmarkCount),
                    if (post.post.bookmarkedByMe) XPrimary else XSecondary,
                    onBookmark
                )
                XMetricAction("引", countLabel(post.post.viewCount), XSecondary, onQuote)
            }
        }
    }
}

@Composable
private fun XQuoteCard(
    post: XResolvedPost,
) {
    Card(colors = CardDefaults.cardColors(containerColor = XSubtle)) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                XAvatar(author = post.author, size = 28.dp)
                Text(
                    text = "${post.author.displayName} ${post.author.handle}",
                    color = XPrimary,
                    fontFamily = SourceSans3,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            Text(
                text = post.post.body,
                color = XPrimary.copy(alpha = 0.88f),
                fontFamily = SourceSans3,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun XMetricAction(
    prefix: String,
    label: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .pressableScale(pressedScale = 0.95f, onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$prefix $label",
            color = tint,
            fontFamily = SourceSans3,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun XBottomReplyBar(
    currentUser: XAuthor?,
    onReply: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(XPage)
    ) {
        HorizontalDivider(color = XBorder)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .background(XSurface, RoundedCornerShape(24.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentUser != null) {
                XAvatar(author = currentUser, size = 34.dp)
            }
            Text(
                text = "写回复",
                color = XSecondary,
                fontFamily = SourceSans3,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(XPrimary)
                    .pressableScale(pressedScale = 0.95f, onClick = onReply)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "回复",
                    color = Color.White,
                    fontFamily = SourceSans3,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun XComposerTopBar(
    canSubmit: Boolean,
    submitLabel: String,
    onBack: () -> Unit,
    onSubmit: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().background(XPage)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderActionButton(onClick = onBack, icon = ZionAppIcons.Back, contentDescription = "返回")
            Text(
                text = if (submitLabel == "回复") "写回复" else "写帖子",
                color = XPrimary,
                fontFamily = SourceSans3,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (canSubmit) XPrimary else XMuted)
                    .pressableScale(enabled = canSubmit, pressedScale = 0.95f, onClick = onSubmit)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = submitLabel,
                    color = if (canSubmit) Color.White else XSecondary,
                    fontFamily = SourceSans3,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
        HorizontalDivider(color = XBorder)
    }
}

@Composable
private fun XComposerIdentity(
    author: XAuthor,
    mode: String,
) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = XSurface)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            XAvatar(author = author, size = 40.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = author.displayName,
                    color = XPrimary,
                    fontFamily = SourceSans3,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = if (mode == XComposerReply) "将以 ${author.handle} 的身份发送回复" else "将以 ${author.handle} 的身份发布帖子",
                    color = XSecondary,
                    fontFamily = SourceSans3,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

@Composable
private fun XContextPreview(
    title: String,
    post: XResolvedPost,
) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = XSubtle)) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = title, color = XSecondary, fontFamily = SourceSans3, fontSize = 13.sp)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                XAvatar(author = post.author, size = 28.dp)
                Text(
                    text = "${post.author.displayName} ${post.author.handle}",
                    color = XPrimary,
                    fontFamily = SourceSans3,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            Text(
                text = post.post.body,
                color = XPrimary.copy(alpha = 0.82f),
                fontFamily = SourceSans3,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun XSectionHeader(
    title: String,
    subtitle: String,
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            color = XPrimary,
            fontFamily = SourceSans3,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = subtitle,
            color = XSecondary,
            fontFamily = SourceSans3,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun XEmptyState(
    title: String,
    subtitle: String,
    action: String,
    onAction: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = XSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                color = XPrimary,
                fontFamily = SourceSans3,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                color = XSecondary,
                fontFamily = SourceSans3,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(XPrimary)
                    .pressableScale(pressedScale = 0.95f, onClick = onAction)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = action,
                    color = Color.White,
                    fontFamily = SourceSans3,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun XFloatingComposer(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(XPrimary)
            .pressableScale(pressedScale = 0.94f, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "写",
            color = Color.White,
            fontFamily = SourceSans3,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun XWordmark() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "X",
            color = Color.White,
            fontFamily = SourceSans3,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun XAvatar(
    author: XAuthor,
    size: androidx.compose.ui.unit.Dp,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(xColor(author.avatarColorHex, XAccent)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = author.initials,
            color = Color.White,
            fontFamily = SourceSans3,
            fontSize = (size.value * 0.34f).sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun xColor(hex: Long, fallback: Color): Color {
    return runCatching { Color(hex.toULong()) }.getOrElse { fallback }
}

@Composable
private fun XStatusChip(
    text: String,
    background: Color,
    content: Color,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = content,
            fontFamily = SourceSans3,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

private fun relativeTime(createdAtMillis: Long): String {
    val diff = (System.currentTimeMillis() - createdAtMillis).coerceAtLeast(0L)
    val minutes = diff / 60_000L
    val hours = diff / 3_600_000L
    val days = diff / 86_400_000L
    return when {
        minutes < 1 -> "刚刚"
        minutes < 60 -> "${minutes} 分钟前"
        hours < 24 -> "${hours} 小时前"
        else -> "${days} 天前"
    }
}

private fun countLabel(value: Int): String {
    return when {
        value >= 1_000_000 -> "${((value / 100_000).toFloat() / 10f).trimZero()}M"
        value >= 1_000 -> "${((value / 100).toFloat() / 10f).trimZero()}K"
        else -> value.toString()
    }
}

private fun Float.trimZero(): String {
    val rounded = ((this * 10).toInt() / 10f)
    return if ((rounded % 1f).absoluteValue < 0.001f) {
        rounded.toInt().toString()
    } else {
        rounded.toString()
    }
}
