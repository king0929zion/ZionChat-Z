package me.rerere.rikkahub.ui.pages.zphone

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dokar.sonner.ToastType
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.Add01
import me.rerere.hugeicons.stroke.ArrowLeft01
import me.rerere.hugeicons.stroke.BubbleChatQuestion
import me.rerere.hugeicons.stroke.Cancel01
import me.rerere.hugeicons.stroke.Favourite
import me.rerere.hugeicons.stroke.Image03
import me.rerere.hugeicons.stroke.MoreVertical
import me.rerere.hugeicons.stroke.Search01
import me.rerere.hugeicons.stroke.Share04
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.datastore.Settings
import me.rerere.rikkahub.data.db.entity.XPostEntity
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalToaster
import me.rerere.rikkahub.utils.getActivity
import org.koin.androidx.compose.koinViewModel
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private enum class XLayer {
    Feed,
    Detail,
    ComposePost,
    ComposeReply,
}

private val XOuter = Color(0xFF242D34)
private val XPanel = Color.White
private val XText = Color(0xFF0F1419)
private val XSubText = Color(0xFF536471)
private val XBlue = Color(0xFF1D9BF0)
private val XDivider = Color(0xFFEFF3F4)
private val XCard = Color(0xFFF7F9FA)
private val XGreen = Color(0xFF00BA7C)
private val XDanger = Color(0xFFF4212E)
private val XWarn = Color(0xFFFFD400)
private val ComposerLimit = 280
private val AbsoluteTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日 HH:mm")

@Composable
fun XTimelinePage(vm: XTimelineVM = koinViewModel()) {
    val context = LocalContext.current
    val activity = remember(context) { context.getActivity() }
    val toaster = LocalToaster.current

    val settings by vm.settings.collectAsStateWithLifecycle()
    val bots by vm.bots.collectAsStateWithLifecycle()
    val feed by vm.feed.collectAsStateWithLifecycle()
    val selectedPostId by vm.selectedPostId.collectAsStateWithLifecycle()
    val selectedPost by vm.selectedPost.collectAsStateWithLifecycle()
    val replies by vm.replies.collectAsStateWithLifecycle()
    val aiPosting by vm.aiPosting.collectAsStateWithLifecycle()
    val submitting by vm.submitting.collectAsStateWithLifecycle()
    val requestingBotReplies by vm.requestingBotReplies.collectAsStateWithLifecycle()

    var currentLayer by rememberSaveable { mutableStateOf(XLayer.Feed) }
    var selectedTopTab by rememberSaveable { mutableStateOf(0) }
    var selectedBottomTab by rememberSaveable { mutableStateOf(0) }
    var composePostText by rememberSaveable { mutableStateOf("") }
    var composeReplyText by rememberSaveable { mutableStateOf("") }
    var awaitingPostOpen by rememberSaveable { mutableStateOf(false) }
    var awaitingReplyClose by rememberSaveable { mutableStateOf(false) }
    var awaitingAiOpen by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(activity) {
        val window = activity?.window
        if (window == null) {
            onDispose { }
        } else {
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
            onDispose {
                controller.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    LaunchedEffect(Unit) {
        vm.bootstrap()
        vm.events.collect { message ->
            toaster.show(
                message,
                type = if (message.contains("失败") || message.contains("不能为空") || message.contains("没有可用")) {
                    ToastType.Info
                } else {
                    ToastType.Success
                }
            )
        }
    }

    LaunchedEffect(awaitingPostOpen, selectedPostId, submitting) {
        if (awaitingPostOpen && !submitting && selectedPostId != null) {
            composePostText = ""
            currentLayer = XLayer.Detail
            awaitingPostOpen = false
        }
        if (awaitingPostOpen && !submitting && selectedPostId == null) {
            awaitingPostOpen = false
        }
    }

    LaunchedEffect(awaitingReplyClose, submitting) {
        if (awaitingReplyClose && !submitting) {
            composeReplyText = ""
            currentLayer = XLayer.Detail
            awaitingReplyClose = false
        }
    }

    LaunchedEffect(awaitingAiOpen, aiPosting, selectedPostId) {
        if (awaitingAiOpen && !aiPosting) {
            if (selectedPostId != null) {
                currentLayer = XLayer.Detail
            }
            awaitingAiOpen = false
        }
    }

    LaunchedEffect(selectedPost, currentLayer) {
        if (selectedPost == null && (currentLayer == XLayer.Detail || currentLayer == XLayer.ComposeReply)) {
            currentLayer = XLayer.Feed
        }
    }

    BackHandler(enabled = currentLayer != XLayer.Feed) {
        currentLayer = when (currentLayer) {
            XLayer.Detail -> {
                vm.closeDetail()
                XLayer.Feed
            }

            XLayer.ComposePost -> XLayer.Feed
            XLayer.ComposeReply -> XLayer.Detail
            XLayer.Feed -> XLayer.Feed
        }
    }

    val filteredFeed = remember(feed, selectedTopTab) {
        when (selectedTopTab) {
            1 -> feed.filter { it.authorKind != "system" }
            2 -> feed.filter { it.authorKind != "user" }
            else -> feed
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(XOuter)
    ) {
        val pageWidth = maxWidth
        val pageHeight = maxHeight

        val feedOffsetX by animateDpAsState(
            targetValue = if (currentLayer == XLayer.Detail || currentLayer == XLayer.ComposeReply) -pageWidth else 0.dp,
            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
            label = "feed_offset"
        )
        val detailOffsetX by animateDpAsState(
            targetValue = if (currentLayer == XLayer.Detail || currentLayer == XLayer.ComposeReply) 0.dp else pageWidth,
            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
            label = "detail_offset"
        )
        val composePostOffsetY by animateDpAsState(
            targetValue = if (currentLayer == XLayer.ComposePost) 0.dp else pageHeight,
            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
            label = "compose_post_offset"
        )
        val composeReplyOffsetY by animateDpAsState(
            targetValue = if (currentLayer == XLayer.ComposeReply) 0.dp else pageHeight,
            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
            label = "compose_reply_offset"
        )

        XFeedLayer(
            settings = settings,
            posts = filteredFeed,
            allPosts = feed,
            bots = bots.map { it.name.ifBlank { "Bot" } },
            selectedTopTab = selectedTopTab,
            selectedBottomTab = selectedBottomTab,
            aiPosting = aiPosting,
            onSelectTopTab = { selectedTopTab = it },
            onSelectBottomTab = { index ->
                if (index == 2) {
                    currentLayer = XLayer.ComposePost
                } else {
                    selectedBottomTab = index
                }
            },
            onOpenPost = {
                vm.openPost(it)
                currentLayer = XLayer.Detail
            },
            onCompose = {
                currentLayer = XLayer.ComposePost
            },
            onGenerateAiPost = {
                awaitingAiOpen = true
                vm.generateAiPost()
            },
            onToggleLike = vm::toggleLike,
            onToggleRepost = vm::toggleRepost,
            onToggleBookmark = vm::toggleBookmark,
            modifier = Modifier
                .fillMaxSize()
                .offset(x = feedOffsetX)
                .zIndex(0f)
        )

        XDetailLayer(
            post = selectedPost,
            replies = replies,
            requestingBotReplies = requestingBotReplies,
            onBack = {
                vm.closeDetail()
                currentLayer = XLayer.Feed
            },
            onReply = {
                currentLayer = XLayer.ComposeReply
            },
            onToggleLike = { selectedPost?.id?.let(vm::toggleLike) },
            onToggleRepost = { selectedPost?.id?.let(vm::toggleRepost) },
            onToggleBookmark = { selectedPost?.id?.let(vm::toggleBookmark) },
            onOpenPost = {
                vm.openPost(it)
                currentLayer = XLayer.Detail
            },
            onToggleLikeForReply = vm::toggleLike,
            onToggleRepostForReply = vm::toggleRepost,
            onToggleBookmarkForReply = vm::toggleBookmark,
            onRequestBotReplies = vm::requestBotReplies,
            modifier = Modifier
                .fillMaxSize()
                .offset(x = detailOffsetX)
                .zIndex(1f)
        )

        XComposeLayer(
            title = "发帖",
            text = composePostText,
            buttonText = if (submitting) "发布中" else "发帖",
            submitting = submitting,
            hint = "有什么新鲜事？",
            onClose = { currentLayer = XLayer.Feed },
            onTextChange = { composePostText = it },
            onSubmit = {
                if (composePostText.isNotBlank() && composePostText.length <= ComposerLimit && !submitting) {
                    awaitingPostOpen = true
                    vm.submitPost(composePostText)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .offset(y = composePostOffsetY)
                .zIndex(2f)
        )

        XReplyComposeLayer(
            post = selectedPost,
            text = composeReplyText,
            buttonText = if (submitting) "回复中" else "回复",
            submitting = submitting,
            onClose = { currentLayer = XLayer.Detail },
            onTextChange = { composeReplyText = it },
            onSubmit = {
                if (composeReplyText.isNotBlank() && composeReplyText.length <= ComposerLimit && !submitting) {
                    awaitingReplyClose = true
                    vm.submitReply(composeReplyText)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .offset(y = composeReplyOffsetY)
                .zIndex(3f)
        )
    }
}

@Composable
private fun XFeedLayer(
    settings: Settings,
    posts: List<XPostEntity>,
    allPosts: List<XPostEntity>,
    bots: List<String>,
    selectedTopTab: Int,
    selectedBottomTab: Int,
    aiPosting: Boolean,
    onSelectTopTab: (Int) -> Unit,
    onSelectBottomTab: (Int) -> Unit,
    onOpenPost: (String) -> Unit,
    onCompose: () -> Unit,
    onGenerateAiPost: () -> Unit,
    onToggleLike: (String) -> Unit,
    onToggleRepost: (String) -> Unit,
    onToggleBookmark: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = XPanel,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.displayCutout)
        ) {
            XHeader(
                onPrimaryAction = onCompose,
                onSecondaryAction = onGenerateAiPost,
                aiPosting = aiPosting
            )
            XTopTabs(selectedTopTab = selectedTopTab, onSelectTopTab = onSelectTopTab)
            Box(modifier = Modifier.weight(1f)) {
                when (selectedBottomTab) {
                    1 -> XExplorePanel(posts = allPosts, onOpenPost = onOpenPost)
                    3 -> XActivityPanel(posts = allPosts, onOpenPost = onOpenPost)
                    4 -> XMessagesPanel(posts = allPosts, bots = bots, onOpenPost = onOpenPost)
                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 154.dp)
                    ) {
                        item {
                            QuickComposerCard(
                                nickname = settings.displaySetting.userNickname.ifBlank { "你" },
                                botCount = bots.size,
                                aiPosting = aiPosting,
                                onCompose = onCompose,
                                onGenerateAiPost = onGenerateAiPost
                            )
                        }
                        if (posts.isEmpty()) {
                            item {
                                EmptyStateCard(
                                    title = "时间线还是空的",
                                    subtitle = "现在发第一条，或者让已配置好的 bots 自动开始活跃。"
                                )
                            }
                        }
                        items(posts, key = { it.id }) { post ->
                            TimelinePostCard(
                                post = post,
                                onOpen = { onOpenPost(post.id) },
                                onReply = { onOpenPost(post.id) },
                                onToggleLike = { onToggleLike(post.id) },
                                onToggleRepost = { onToggleRepost(post.id) },
                                onToggleBookmark = { onToggleBookmark(post.id) }
                            )
                            HorizontalDivider(color = XDivider)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 18.dp, bottom = 92.dp)
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(XBlue)
                        .pressableScale(pressedScale = 0.9f, onClick = onCompose),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = HugeIcons.Add01,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            XBottomTabs(
                selectedIndex = selectedBottomTab,
                onSelect = onSelectBottomTab
            )
        }
    }
}

@Composable
private fun XDetailLayer(
    post: XPostEntity?,
    replies: List<XPostEntity>,
    requestingBotReplies: Boolean,
    onBack: () -> Unit,
    onReply: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleRepost: () -> Unit,
    onToggleBookmark: () -> Unit,
    onOpenPost: (String) -> Unit,
    onToggleLikeForReply: (String) -> Unit,
    onToggleRepostForReply: (String) -> Unit,
    onToggleBookmarkForReply: (String) -> Unit,
    onRequestBotReplies: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = XPanel,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.displayCutout)
        ) {
            DetailTopBar(onBack = onBack)
            if (post == null) {
                EmptyStateCard(
                    title = "帖子不存在",
                    subtitle = "这条内容可能还没加载好，返回时间线再试一次。",
                    modifier = Modifier.padding(24.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 108.dp)
                ) {
                    item {
                        DetailHeroCard(
                            post = post,
                            replyCount = replies.size,
                            requestingBotReplies = requestingBotReplies,
                            onReply = onReply,
                            onToggleLike = onToggleLike,
                            onToggleRepost = onToggleRepost,
                            onToggleBookmark = onToggleBookmark,
                            onRequestBotReplies = onRequestBotReplies
                        )
                    }
                    item {
                        Surface(color = XPanel) {
                            Text(
                                text = if (replies.isEmpty()) "还没有回复" else "${replies.size} 条回复",
                                color = XText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
                            )
                        }
                        HorizontalDivider(color = XDivider)
                    }
                    if (replies.isEmpty()) {
                        item {
                            EmptyStateCard(
                                title = "这里还很安静",
                                subtitle = "你可以先回一条，已配置好的 bots 也会自动跟进。",
                                modifier = Modifier.padding(20.dp)
                            )
                        }
                    }
                    items(replies, key = { it.id }) { reply ->
                        TimelinePostCard(
                            post = reply,
                            onOpen = { onOpenPost(reply.rootPostId) },
                            onReply = { onOpenPost(post.id) },
                            onToggleLike = { onToggleLikeForReply(reply.id) },
                            onToggleRepost = { onToggleRepostForReply(reply.id) },
                            onToggleBookmark = { onToggleBookmarkForReply(reply.id) }
                        )
                        HorizontalDivider(color = XDivider)
                    }
                }
                ReplyEntryBar(
                    onReply = onReply,
                    onRequestBotReplies = onRequestBotReplies,
                    requestingBotReplies = requestingBotReplies
                )
            }
        }
    }
}

@Composable
private fun XComposeLayer(
    title: String,
    text: String,
    buttonText: String,
    submitting: Boolean,
    hint: String,
    onClose: () -> Unit,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val overLimit = text.length > ComposerLimit
    val canSubmit = text.isNotBlank() && !overLimit && !submitting
    Surface(
        modifier = modifier,
        color = XPanel,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.displayCutout)
                .imePadding()
        ) {
            ComposerTopBar(
                title = title,
                buttonText = buttonText,
                canSubmit = canSubmit,
                submitting = submitting,
                onClose = onClose,
                onSubmit = onSubmit
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                UserAvatarBubble(modifier = Modifier.size(44.dp))
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    VisibilityChip()
                    Spacer(modifier = Modifier.height(14.dp))
                    ComposerTextArea(
                        text = text,
                        hint = hint,
                        onTextChange = onTextChange,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
            }
            ComposerFooter(
                textLength = text.length,
                overLimit = overLimit
            )
        }
    }
}

@Composable
private fun XReplyComposeLayer(
    post: XPostEntity?,
    text: String,
    buttonText: String,
    submitting: Boolean,
    onClose: () -> Unit,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val overLimit = text.length > ComposerLimit
    val canSubmit = text.isNotBlank() && !overLimit && !submitting
    Surface(
        modifier = modifier,
        color = XPanel,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.displayCutout)
                .imePadding()
        ) {
            ComposerTopBar(
                title = "回复",
                buttonText = buttonText,
                canSubmit = canSubmit,
                submitting = submitting,
                onClose = onClose,
                onSubmit = onSubmit
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                if (post != null) {
                    ReplyContextCard(post = post)
                    Spacer(modifier = Modifier.height(18.dp))
                }
                Row {
                    UserAvatarBubble(modifier = Modifier.size(44.dp))
                    Spacer(modifier = Modifier.width(14.dp))
                    ComposerTextArea(
                        text = text,
                        hint = "发布你的回复",
                        onTextChange = onTextChange,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            ComposerFooter(
                textLength = text.length,
                overLimit = overLimit
            )
        }
    }
}

@Composable
private fun XHeader(
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit,
    aiPosting: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatarBubble(modifier = Modifier.size(34.dp))
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(R.drawable.zphone_x_logo),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HeaderAction(
                icon = HugeIcons.Search01,
                onClick = onPrimaryAction
            )
            HeaderAction(
                icon = HugeIcons.MoreVertical,
                onClick = onSecondaryAction,
                loading = aiPosting
            )
        }
    }
}

@Composable
private fun HeaderAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    loading: Boolean = false,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(XCard)
            .pressableScale(pressedScale = 0.92f, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = XBlue,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = XText,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun XTopTabs(selectedTopTab: Int, onSelectTopTab: (Int) -> Unit) {
    val tabs = listOf("为你推荐", "正在关注", "AI 动态")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .background(XPanel)
    ) {
        tabs.forEachIndexed { index, title ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clickable { onSelectTopTab(index) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = if (selectedTopTab == index) XText else XSubText,
                    fontSize = 15.sp,
                    fontWeight = if (selectedTopTab == index) FontWeight.Bold else FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .width(56.dp)
                        .height(3.dp)
                        .clip(CircleShape)
                        .background(if (selectedTopTab == index) XBlue else Color.Transparent)
                )
            }
        }
    }
    HorizontalDivider(color = XDivider)
}

@Composable
private fun QuickComposerCard(
    nickname: String,
    botCount: Int,
    aiPosting: Boolean,
    onCompose: () -> Unit,
    onGenerateAiPost: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = XCard,
        shadowElevation = 2.dp,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                UserAvatarBubble(modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = nickname,
                        color = XText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (botCount > 0) "$botCount 个 bots 正在自动活跃和回复" else "还没有可自动发帖的 bots",
                        color = XSubText,
                        fontSize = 13.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "完整支持本地持久化发帖、bots 自动回复、AI 工具代发和自动发帖。",
                color = XText,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onCompose,
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("马上发一条")
                }
                Button(
                    onClick = onGenerateAiPost,
                    enabled = !aiPosting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = XBlue,
                        contentColor = Color.White,
                        disabledContainerColor = XBlue.copy(alpha = 0.45f),
                        disabledContentColor = Color.White
                    ),
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (aiPosting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("AI 代发")
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelinePostCard(
    post: XPostEntity,
    onOpen: () -> Unit,
    onReply: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleRepost: () -> Unit,
    onToggleBookmark: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            PostAvatar(post = post, modifier = Modifier.size(42.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = post.authorName,
                        color = XText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (post.authorKind != "user") {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = XBlue.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Text(
                                text = if (post.authorKind == "system") "官方" else "BOT",
                                color = XBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = post.authorHandle,
                        color = XSubText,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = " · ${formatRelativeTime(post.createAt)}",
                        color = XSubText,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = post.content,
                    color = XText,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
                if (!post.quoteContent.isNullOrBlank() || !post.quotePreview.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    QuotePreviewCard(post = post)
                }
                Spacer(modifier = Modifier.height(14.dp))
                PostActionsRow(
                    post = post,
                    onReply = onReply,
                    onToggleLike = onToggleLike,
                    onToggleRepost = onToggleRepost,
                    onToggleBookmark = onToggleBookmark
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = HugeIcons.MoreVertical,
                contentDescription = null,
                tint = XSubText,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun QuotePreviewCard(post: XPostEntity) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = XPanel,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(XCard)
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = post.quoteAuthorName ?: "引用内容",
                    color = XText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                if (!post.quoteHandle.isNullOrBlank()) {
                    Text(
                        text = "  ${post.quoteHandle}",
                        color = XSubText,
                        fontSize = 13.sp
                    )
                }
            }
            if (!post.quoteContent.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = post.quoteContent,
                    color = XText,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (!post.quotePreview.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFE5F2F9)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(92.dp)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = post.quotePreview,
                            color = XSubText,
                            fontSize = 13.sp,
                            modifier = Modifier.align(Alignment.BottomStart)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PostActionsRow(
    post: XPostEntity,
    onReply: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleRepost: () -> Unit,
    onToggleBookmark: () -> Unit,
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ActionChip(
            icon = HugeIcons.BubbleChatQuestion,
            label = formatMetric(post.replyCount),
            tint = XSubText,
            onClick = onReply
        )
        ActionChip(
            icon = HugeIcons.Share04,
            label = formatMetric(post.repostCount),
            tint = if (post.repostedByUser) XGreen else XSubText,
            onClick = onToggleRepost
        )
        ActionChip(
            icon = HugeIcons.Favourite,
            label = formatMetric(post.likeCount),
            tint = if (post.likedByUser) XDanger else XSubText,
            onClick = onToggleLike
        )
        MetricChip(label = "浏览 ${formatMetric(post.viewCount)}")
        MetricChip(
            label = if (post.bookmarkedByUser) "已收藏" else "收藏",
            tint = if (post.bookmarkedByUser) XBlue else XSubText,
            onClick = onToggleBookmark
        )
    }
}

@Composable
private fun ActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Surface(
        color = tint.copy(alpha = 0.08f),
        shape = RoundedCornerShape(999.dp),
        modifier = Modifier.pressableScale(pressedScale = 0.94f, onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                color = tint,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun MetricChip(
    label: String,
    tint: Color = XSubText,
    onClick: (() -> Unit)? = null,
) {
    val modifier = if (onClick == null) {
        Modifier
    } else {
        Modifier.pressableScale(pressedScale = 0.94f, onClick = onClick)
    }
    Surface(
        color = tint.copy(alpha = 0.08f),
        shape = RoundedCornerShape(999.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = tint,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun DetailTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(XCard)
                .pressableScale(pressedScale = 0.92f, onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = HugeIcons.ArrowLeft01,
                contentDescription = null,
                tint = XText,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = "帖子",
                color = XText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "真实发帖、回复和 bots 互动",
                color = XSubText,
                fontSize = 12.sp
            )
        }
    }
    HorizontalDivider(color = XDivider)
}

@Composable
private fun DetailHeroCard(
    post: XPostEntity,
    replyCount: Int,
    requestingBotReplies: Boolean,
    onReply: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleRepost: () -> Unit,
    onToggleBookmark: () -> Unit,
    onRequestBotReplies: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
        Row(verticalAlignment = Alignment.Top) {
            PostAvatar(post = post, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.authorName,
                    color = XText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = post.authorHandle,
                    color = XSubText,
                    fontSize = 15.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = post.content,
            color = XText,
            fontSize = 18.sp,
            lineHeight = 28.sp
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "${formatAbsoluteTime(post.createAt)} · ${formatMetric(post.viewCount)} 查看",
            color = XSubText,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = XDivider)
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricChip(label = "${formatMetric(post.repostCount)} 转发")
            MetricChip(label = "${formatMetric(post.likeCount)} 喜欢")
            MetricChip(label = "$replyCount 回复")
        }
        Spacer(modifier = Modifier.height(16.dp))
        PostActionsRow(
            post = post,
            onReply = onReply,
            onToggleLike = onToggleLike,
            onToggleRepost = onToggleRepost,
            onToggleBookmark = onToggleBookmark
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = onRequestBotReplies,
                enabled = !requestingBotReplies,
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (requestingBotReplies) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = XBlue,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("催 bots 回复")
                }
            }
            MetricChip(
                label = if (post.bookmarkedByUser) "已收藏到本地" else "收藏到本地",
                tint = if (post.bookmarkedByUser) XBlue else XSubText,
                onClick = onToggleBookmark
            )
        }
    }
    HorizontalDivider(color = XDivider)
}

@Composable
private fun ReplyEntryBar(
    onReply: () -> Unit,
    onRequestBotReplies: () -> Unit,
    requestingBotReplies: Boolean,
) {
    Surface(
        color = XPanel,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatarBubble(modifier = Modifier.size(34.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(999.dp))
                    .background(XCard)
                    .clickable(onClick = onReply)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "发布你的回复",
                    color = XSubText,
                    fontSize = 15.sp
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            OutlinedButton(
                onClick = onRequestBotReplies,
                enabled = !requestingBotReplies,
                shape = RoundedCornerShape(999.dp)
            ) {
                if (requestingBotReplies) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        color = XBlue,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Bots")
                }
            }
        }
    }
}

@Composable
private fun ComposerTopBar(
    title: String,
    buttonText: String,
    canSubmit: Boolean,
    submitting: Boolean,
    onClose: () -> Unit,
    onSubmit: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(XCard)
                .pressableScale(pressedScale = 0.92f, onClick = onClose),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = HugeIcons.Cancel01,
                contentDescription = null,
                tint = XText,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            color = XText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Button(
            onClick = onSubmit,
            enabled = canSubmit,
            colors = ButtonDefaults.buttonColors(
                containerColor = XBlue,
                contentColor = Color.White,
                disabledContainerColor = XBlue.copy(alpha = 0.42f),
                disabledContentColor = Color.White
            ),
            shape = RoundedCornerShape(999.dp)
        ) {
            if (submitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(buttonText)
            }
        }
    }
    HorizontalDivider(color = XDivider)
}

@Composable
private fun VisibilityChip() {
    Surface(
        color = XPanel,
        shape = RoundedCornerShape(999.dp),
        modifier = Modifier.clip(RoundedCornerShape(999.dp))
    ) {
        Text(
            text = "每个人都能看到",
            color = XBlue,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .background(XBlue.copy(alpha = 0.09f))
                .padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun ComposerTextArea(
    text: String,
    hint: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        textStyle = TextStyle(
            color = XText,
            fontSize = 22.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.Normal
        ),
        modifier = modifier.fillMaxWidth(),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                if (text.isBlank()) {
                    Text(
                        text = hint,
                        color = XSubText,
                        fontSize = 22.sp,
                        lineHeight = 30.sp
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun ComposerFooter(textLength: Int, overLimit: Boolean) {
    Column {
        HorizontalDivider(color = XDivider)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ComposerActionChip(icon = HugeIcons.Image03, label = "图片")
            MetricChip(label = "文件")
            MetricChip(label = "位置")
            MetricChip(label = "草稿")
        }
        HorizontalDivider(color = XDivider)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "所有人都可以回复",
                color = XBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            CharacterProgress(
                length = textLength,
                overLimit = overLimit
            )
        }
    }
}

@Composable
private fun ComposerActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
) {
    Surface(
        color = XBlue.copy(alpha = 0.08f),
        shape = RoundedCornerShape(999.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = XBlue,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                color = XBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun CharacterProgress(length: Int, overLimit: Boolean) {
    val progress = (length.coerceAtMost(ComposerLimit)).toFloat() / ComposerLimit.toFloat()
    val color = when {
        overLimit -> XDanger
        length > 220 -> XWarn
        length > 0 -> XBlue
        else -> XDivider
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(26.dp)) {
            val strokeWidth = 2.5.dp.toPx()
            drawCircle(
                color = XDivider,
                radius = size.minDimension / 2f - strokeWidth,
                style = Stroke(width = strokeWidth)
            )
            if (length > 0) {
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = progress * 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "$length/$ComposerLimit",
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ReplyContextCard(post: XPostEntity) {
    Row {
        PostAvatar(post = post, modifier = Modifier.size(42.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${post.authorName} ${post.authorHandle}",
                color = XText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = post.content,
                color = XText,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "回复给 ${post.authorHandle}",
                color = XBlue,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun XExplorePanel(posts: List<XPostEntity>, onOpenPost: (String) -> Unit) {
    val keywords = remember(posts) {
        posts.flatMap { post ->
            post.content
                .split(Regex("[\\s\\n，。！？,.!?:：]+"))
                .map { it.trim() }
                .filter { it.length in 2..14 }
        }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(6)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        item {
            SectionIntroCard(
                title = "探索",
                subtitle = "从你自己的时间线里提炼热点，点进去可以直接回到原帖。"
            )
        }
        if (keywords.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "还没有足够的内容生成热点",
                    subtitle = "先发几条帖子，探索页会开始自己长出来。"
                )
            }
        }
        items(keywords, key = { it.key }) { entry ->
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = XCard,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                val target = posts.firstOrNull { it.content.contains(entry.key) }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { target?.let { onOpenPost(it.id) } }
                        .padding(18.dp)
                ) {
                    Text(
                        text = "趋势话题",
                        color = XSubText,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = entry.key,
                        color = XText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "被提到 ${entry.value} 次",
                        color = XSubText,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun XActivityPanel(posts: List<XPostEntity>, onOpenPost: (String) -> Unit) {
    val activityItems = remember(posts) {
        posts.take(8)
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        item {
            SectionIntroCard(
                title = "提醒",
                subtitle = "这里会汇总最新的发帖、回复和互动信号。"
            )
        }
        if (activityItems.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "暂时没有提醒",
                    subtitle = "时间线有新动态后，这里会同步更新。"
                )
            }
        }
        items(activityItems, key = { it.id }) { post ->
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = XCard,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenPost(post.rootPostId) }
                        .padding(18.dp)
                ) {
                    Text(
                        text = "${post.authorName} ${if (post.parentPostId == null) "发布了新动态" else "参与了这条讨论"}",
                        color = XText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = post.content,
                        color = XSubText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatRelativeTime(post.createAt),
                        color = XSubText,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun XMessagesPanel(
    posts: List<XPostEntity>,
    bots: List<String>,
    onOpenPost: (String) -> Unit,
) {
    val conversations = remember(posts, bots) {
        val botPosts = posts.filter { it.authorKind == "bot" }.take(8)
        if (botPosts.isNotEmpty()) {
            botPosts
        } else {
            posts.take(4)
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        item {
            SectionIntroCard(
                title = "私信",
                subtitle = if (bots.isNotEmpty()) "已配置 bots 可以在这里形成更像真实产品的会话入口。" else "先配置 bots，私信页会更像真实社交应用。"
            )
        }
        if (conversations.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "私信列表还是空的",
                    subtitle = "等 bots 和时间线都活跃起来后，这里会自然丰富。"
                )
            }
        }
        items(conversations, key = { it.id }) { post ->
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = XCard,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenPost(post.rootPostId) }
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PostAvatar(post = post, modifier = Modifier.size(44.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = post.authorName,
                            color = XText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = post.content,
                            color = XSubText,
                            fontSize = 14.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = formatRelativeTime(post.createAt),
                        color = XSubText,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionIntroCard(title: String, subtitle: String) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = XCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                color = XText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = XSubText,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )
        }
    }
}

@Composable
private fun EmptyStateCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = XCard,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = XText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                color = XSubText,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun XBottomTabs(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    val labels = listOf("首页", "探索", "发帖", "提醒", "私信")
    HorizontalDivider(color = XDivider)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        labels.forEachIndexed { index, label ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clickable { onSelect(index) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    color = if (selectedIndex == index && index != 2) XText else XSubText,
                    fontSize = 13.sp,
                    fontWeight = if (selectedIndex == index && index != 2) FontWeight.Bold else FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .width(if (index == 2) 34.dp else 24.dp)
                        .height(3.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                index == 2 -> XBlue.copy(alpha = 0.22f)
                                selectedIndex == index -> XText
                                else -> Color.Transparent
                            }
                        )
                )
            }
        }
    }
}

@Composable
private fun PostAvatar(post: XPostEntity, modifier: Modifier = Modifier) {
    val background = when (post.authorKind) {
        "bot" -> Color.Black
        "system" -> XBlue
        else -> Color(0xFFE7ECF0)
    }
    val textColor = if (post.authorKind == "user") XText else Color.White
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = post.authorName.take(1).ifBlank { "Z" },
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun UserAvatarBubble(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0xFFE7ECF0)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "你",
            color = XText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatRelativeTime(timestamp: Long): String {
    val duration = Duration.between(Instant.ofEpochMilli(timestamp), Instant.now())
    val minutes = duration.toMinutes().coerceAtLeast(0)
    return when {
        minutes < 1 -> "刚刚"
        minutes < 60 -> "${minutes}分钟"
        minutes < 24 * 60 -> "${minutes / 60}小时"
        minutes < 24 * 60 * 7 -> "${minutes / (24 * 60)}天"
        else -> "${minutes / (24 * 60 * 7)}周"
    }
}

private fun formatAbsoluteTime(timestamp: Long): String {
    return AbsoluteTimeFormatter.format(Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()))
}

private fun formatMetric(value: Int): String {
    return when {
        value >= 10_000 -> {
            val formatted = String.format("%.1f", value / 10_000f)
                .removeSuffix(".0")
            "${formatted}万"
        }

        value >= 1_000 -> {
            val formatted = String.format("%.1f", value / 1_000f)
                .removeSuffix(".0")
            "${formatted}k"
        }

        else -> value.toString()
    }
}
