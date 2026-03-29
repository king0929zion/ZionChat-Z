package me.rerere.rikkahub.ui.pages.zphone

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dokar.sonner.ToastType
import me.rerere.rikkahub.data.datastore.Settings
import me.rerere.rikkahub.data.db.entity.XPostEntity
import me.rerere.rikkahub.ui.components.ui.UIAvatar
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalToaster
import me.rerere.rikkahub.utils.getActivity
import org.koin.androidx.compose.koinViewModel
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
private val XGreen = Color(0xFF00BA7C)
private val XPink = Color(0xFFF91880)
private val XWarn = Color(0xFFFFD400)
private const val ComposerLimit = 280
private val DetailTimeFormatter = DateTimeFormatter.ofPattern("yy年M月d日, HH:mm")

@Composable
fun XTimelinePage(vm: XTimelineVM = koinViewModel()) {
    val context = LocalContext.current
    val activity = remember(context) { context.getActivity() }
    val toaster = LocalToaster.current

    val settings by vm.settings.collectAsStateWithLifecycle()
    val feed by vm.feed.collectAsStateWithLifecycle()
    val selectedPostId by vm.selectedPostId.collectAsStateWithLifecycle()
    val selectedPost by vm.selectedPost.collectAsStateWithLifecycle()
    val replies by vm.replies.collectAsStateWithLifecycle()
    val aiPosting by vm.aiPosting.collectAsStateWithLifecycle()
    val submitting by vm.submitting.collectAsStateWithLifecycle()

    var currentLayer by rememberSaveable { mutableStateOf(XLayer.Feed) }
    var selectedTopTab by rememberSaveable { mutableStateOf(0) }
    var selectedBottomTab by rememberSaveable { mutableStateOf(0) }
    var composePostText by rememberSaveable { mutableStateOf("") }
    var composeReplyText by rememberSaveable { mutableStateOf("") }
    var awaitingPostOpen by rememberSaveable { mutableStateOf(false) }
    var awaitingReplyClose by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(activity) {
        val window = activity?.window
        if (window != null) {
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.show(WindowInsetsCompat.Type.systemBars())
            controller.isAppearanceLightStatusBars = true
            controller.isAppearanceLightNavigationBars = true
        }
        onDispose { }
    }

    LaunchedEffect(Unit) {
        vm.bootstrap()
        vm.events.collect { message ->
            if (message.contains("失败") || message.contains("不能为空") || message.contains("没有可用")) {
                toaster.show(message, type = ToastType.Info)
            }
        }
    }

    LaunchedEffect(awaitingPostOpen, selectedPostId, submitting) {
        if (awaitingPostOpen && !submitting) {
            if (selectedPostId != null) {
                composePostText = ""
                currentLayer = XLayer.Detail
            }
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

    LaunchedEffect(selectedPost, currentLayer) {
        if (selectedPost == null && currentLayer != XLayer.Feed && currentLayer != XLayer.ComposePost) {
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

    val topTabPosts = remember(feed, selectedTopTab) {
        when (selectedTopTab) {
            1 -> feed.filter { it.authorKind == "user" }
            2 -> feed.filter { it.authorKind != "user" }
            else -> feed
        }
    }

    val activeFeed = remember(topTabPosts) {
        topTabPosts
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
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
            label = "x_feed_offset"
        )
        val detailOffsetX by animateDpAsState(
            targetValue = if (currentLayer == XLayer.Detail || currentLayer == XLayer.ComposeReply) 0.dp else pageWidth,
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
            label = "x_detail_offset"
        )
        val composePostOffsetY by animateDpAsState(
            targetValue = if (currentLayer == XLayer.ComposePost) 0.dp else pageHeight,
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
            label = "x_compose_post_offset"
        )
        val composeReplyOffsetY by animateDpAsState(
            targetValue = if (currentLayer == XLayer.ComposeReply) 0.dp else pageHeight,
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
            label = "x_compose_reply_offset"
        )

        XFeedLayer(
            settings = settings,
            selectedTopTab = selectedTopTab,
            selectedBottomTab = selectedBottomTab,
            posts = activeFeed,
            aiPosting = aiPosting,
            modifier = Modifier.offset(x = feedOffsetX),
            onTopTabChange = { selectedTopTab = it },
            onBottomTabChange = { index ->
                if (index == 0 || index == 1 || index == 2 || index == 3 || index == 4) {
                    selectedBottomTab = index
                }
            },
            onOpenPost = { id ->
                vm.openPost(id)
                currentLayer = XLayer.Detail
            },
            onCompose = {
                currentLayer = XLayer.ComposePost
            },
            onLike = vm::toggleLike,
            onRepost = vm::toggleRepost,
            onBookmark = vm::toggleBookmark,
        )

        XDetailLayer(
            settings = settings,
            post = selectedPost,
            replies = replies,
            modifier = Modifier.offset(x = detailOffsetX),
            onBack = {
                vm.closeDetail()
                currentLayer = XLayer.Feed
            },
            onComposeReply = {
                currentLayer = XLayer.ComposeReply
            },
            onOpenPost = vm::openPost,
            onLike = vm::toggleLike,
            onRepost = vm::toggleRepost,
            onBookmark = vm::toggleBookmark,
        )

        XComposePostLayer(
            settings = settings,
            text = composePostText,
            posting = submitting,
            modifier = Modifier.offset(y = composePostOffsetY),
            onClose = { currentLayer = XLayer.Feed },
            onTextChange = { composePostText = it },
            onPost = {
                awaitingPostOpen = true
                vm.submitPost(composePostText)
            },
        )

        XComposeReplyLayer(
            settings = settings,
            post = selectedPost,
            text = composeReplyText,
            posting = submitting,
            modifier = Modifier.offset(y = composeReplyOffsetY),
            onClose = { currentLayer = XLayer.Detail },
            onTextChange = { composeReplyText = it },
            onReply = {
                awaitingReplyClose = true
                vm.submitReply(composeReplyText)
            },
        )
    }
}

@Composable
private fun XFeedLayer(
    settings: Settings,
    selectedTopTab: Int,
    selectedBottomTab: Int,
    posts: List<XPostEntity>,
    aiPosting: Boolean,
    modifier: Modifier = Modifier,
    onTopTabChange: (Int) -> Unit,
    onBottomTabChange: (Int) -> Unit,
    onOpenPost: (String) -> Unit,
    onCompose: () -> Unit,
    onLike: (String) -> Unit,
    onRepost: (String) -> Unit,
    onBookmark: (String) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(XPanel)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            XFeedHeader(
                settings = settings,
                selectedTopTab = selectedTopTab,
                onTopTabChange = onTopTabChange,
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 112.dp)
            ) {
                items(posts, key = { it.id }) { post ->
                    XPostCard(
                        post = post,
                        onOpenPost = { onOpenPost(post.id) },
                        onLike = { onLike(post.id) },
                        onRepost = { onRepost(post.id) },
                        onBookmark = { onBookmark(post.id) },
                    )
                }
            }
        }

        XFloatingComposeButton(
            aiPosting = aiPosting,
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = onCompose,
        )
        XBottomNavBar(
            selectedIndex = selectedBottomTab,
            modifier = Modifier.align(Alignment.BottomCenter),
            onSelect = onBottomTabChange,
        )
    }
}

@Composable
private fun XFeedHeader(
    settings: Settings,
    selectedTopTab: Int,
    onTopTabChange: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(XPanel.copy(alpha = 0.92f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .height(53.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UIAvatar(
                name = settings.displaySetting.userNickname.ifBlank { "你" },
                value = settings.displaySetting.userAvatar,
                modifier = Modifier.size(32.dp)
            )
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = XCloneIcons.XLogo,
                    contentDescription = null,
                    tint = XText,
                    modifier = Modifier.size(22.dp)
                )
            }
            Icon(
                imageVector = XCloneIcons.More,
                contentDescription = null,
                tint = XSubText,
                modifier = Modifier.size(20.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .background(XPanel)
        ) {
            listOf("为你推荐", "正在关注", "AI — Rumors & Insights").forEachIndexed { index, title ->
                val selected = index == selectedTopTab
                Box(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .defaultMinSize(minWidth = 120.dp)
                        .height(53.dp)
                        .clickable { onTopTabChange(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (selected) XText else XSubText,
                        fontSize = 15.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (selected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .width(56.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(XBlue)
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = XDivider)
    }
}

@Composable
private fun XPostCard(
    post: XPostEntity,
    onOpenPost: () -> Unit,
    onLike: () -> Unit,
    onRepost: () -> Unit,
    onBookmark: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenPost)
            .background(XPanel)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PostAvatar(post = post)
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = post.authorName,
                            color = XText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (post.authorKind != "user") {
                            Icon(
                                imageVector = XCloneIcons.Verified,
                                contentDescription = null,
                                tint = XBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        if (post.authorKind == "bot") {
                            Icon(
                                imageVector = XCloneIcons.XLogo,
                                contentDescription = null,
                                tint = XText,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Text(
                            text = post.authorHandle,
                            color = XSubText,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(text = "·", color = XSubText, fontSize = 15.sp)
                        Text(
                            text = relativeTime(post.createAt),
                            color = XSubText,
                            fontSize = 15.sp,
                        )
                    }
                    Icon(
                        imageVector = XCloneIcons.More,
                        contentDescription = null,
                        tint = XSubText,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = post.content,
                    color = XText,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                )
                if (!post.quoteContent.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    QuoteCard(post = post)
                }
                Spacer(modifier = Modifier.height(10.dp))
                XPostActionRow(
                    post = post,
                    onLike = onLike,
                    onRepost = onRepost,
                    onBookmark = onBookmark,
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = XDivider)
    }
}

@Composable
private fun QuoteCard(post: XPostEntity) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, XDivider, RoundedCornerShape(18.dp))
            .clickable { }
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1F2937)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (post.quoteAuthorName ?: "R").take(1).uppercase(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = post.quoteAuthorName ?: "引用帖子",
                color = XText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = post.quoteHandle ?: "@quote",
                color = XSubText,
                fontSize = 14.sp,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = post.quoteContent.orEmpty(),
            color = XText,
            fontSize = 15.sp,
            lineHeight = 20.sp,
        )
    }
}

@Composable
private fun XPostActionRow(
    post: XPostEntity,
    onLike: () -> Unit,
    onRepost: () -> Unit,
    onBookmark: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ActionMetric(icon = XCloneIcons.Reply, count = post.replyCount, tint = XSubText, onClick = {})
        ActionMetric(icon = XCloneIcons.Repost, count = post.repostCount, tint = if (post.repostedByUser) XGreen else XSubText, onClick = onRepost)
        ActionMetric(icon = XCloneIcons.Like, count = post.likeCount, tint = if (post.likedByUser) XPink else XSubText, onClick = onLike)
        ActionMetric(icon = XCloneIcons.Stats, count = post.viewCount, tint = XSubText, onClick = {})
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            MiniAction(icon = XCloneIcons.Bookmark, tint = if (post.bookmarkedByUser) XBlue else XSubText, onClick = onBookmark)
            MiniAction(icon = XCloneIcons.Share, tint = XSubText, onClick = {})
        }
    }
}

@Composable
private fun ActionMetric(
    icon: ImageVector,
    count: Int,
    tint: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = compactCount(count),
            color = tint,
            fontSize = 13.sp,
        )
    }
}

@Composable
private fun MiniAction(
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun XDetailLayer(
    settings: Settings,
    post: XPostEntity?,
    replies: List<XPostEntity>,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onComposeReply: () -> Unit,
    onOpenPost: (String) -> Unit,
    onLike: (String) -> Unit,
    onRepost: (String) -> Unit,
    onBookmark: (String) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(XPanel)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .height(53.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = XCloneIcons.Back,
                        contentDescription = null,
                        tint = XText,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "发帖",
                    color = XText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }
            if (post != null) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    item(post.id) {
                        XDetailPostCard(
                            post = post,
                            onLike = { onLike(post.id) },
                            onRepost = { onRepost(post.id) },
                            onBookmark = { onBookmark(post.id) },
                        )
                    }
                    items(replies, key = { it.id }) { reply ->
                        XPostCard(
                            post = reply,
                            onOpenPost = { onOpenPost(reply.id) },
                            onLike = { onLike(reply.id) },
                            onRepost = { onRepost(reply.id) },
                            onBookmark = { onBookmark(reply.id) },
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(XPanel)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .imePadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UIAvatar(
                name = settings.displaySetting.userNickname.ifBlank { "你" },
                value = settings.displaySetting.userAvatar,
                modifier = Modifier.size(32.dp)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(999.dp))
                    .background(XDivider)
                    .clickable(onClick = onComposeReply)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "发布你的回复",
                    color = XSubText,
                    fontSize = 15.sp,
                )
            }
        }
    }
}

@Composable
private fun XDetailPostCard(
    post: XPostEntity,
    onLike: () -> Unit,
    onRepost: () -> Unit,
    onBookmark: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PostAvatar(post = post, size = 40.dp)
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = post.authorName, color = XText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        if (post.authorKind != "user") {
                            Icon(
                                imageVector = XCloneIcons.Verified,
                                contentDescription = null,
                                tint = XBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(text = post.authorHandle, color = XSubText, fontSize = 15.sp)
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(XText)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = "订阅", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = post.content,
            color = XText,
            fontSize = 17.sp,
            lineHeight = 24.sp,
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = Instant.ofEpochMilli(post.createAt)
                    .atZone(ZoneId.systemDefault())
                    .format(DetailTimeFormatter),
                color = XSubText,
                fontSize = 15.sp,
            )
            Text(text = "·", color = XSubText, fontSize = 15.sp)
            Text(text = compactCount(post.viewCount), color = XText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(text = "查看", color = XSubText, fontSize = 15.sp)
        }
        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = XDivider)
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            StatPair(title = "转帖", value = compactCount(post.repostCount))
            StatPair(title = "喜欢", value = compactCount(post.likeCount))
            StatPair(title = "书签", value = if (post.bookmarkedByUser) "已存" else "0")
        }
        HorizontalDivider(color = XDivider)
        Text(
            text = "最相关的回复",
            color = XText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        HorizontalDivider(color = XDivider)
        Spacer(modifier = Modifier.height(8.dp))
        XPostActionRow(
            post = post,
            onLike = onLike,
            onRepost = onRepost,
            onBookmark = onBookmark,
        )
    }
}

@Composable
private fun StatPair(title: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = value, color = XText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text(text = title, color = XSubText, fontSize = 15.sp)
    }
}

@Composable
private fun XComposePostLayer(
    settings: Settings,
    text: String,
    posting: Boolean,
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    onTextChange: (String) -> Unit,
    onPost: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(XPanel)
            .imePadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            XComposerHeader(
                actionText = "发帖",
                enabled = text.isNotBlank() && text.length <= ComposerLimit && !posting,
                onClose = onClose,
                onAction = onPost,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    UIAvatar(
                        name = settings.displaySetting.userNickname.ifBlank { "你" },
                        value = settings.displaySetting.userAvatar,
                        modifier = Modifier.size(40.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        AudienceChip()
                        Spacer(modifier = Modifier.height(10.dp))
                        ComposerField(
                            value = text,
                            placeholder = "有什么新鲜事？",
                            onValueChange = onTextChange,
                        )
                    }
                }
            }
            XComposerBottomTray(textLength = text.length)
        }
    }
}

@Composable
private fun XComposeReplyLayer(
    settings: Settings,
    post: XPostEntity?,
    text: String,
    posting: Boolean,
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    onTextChange: (String) -> Unit,
    onReply: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(XPanel)
            .imePadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            XComposerHeader(
                actionText = "回复",
                enabled = text.isNotBlank() && text.length <= ComposerLimit && !posting,
                onClose = onClose,
                onAction = onReply,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                if (post != null) {
                    ReplyContextCard(post = post)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    UIAvatar(
                        name = settings.displaySetting.userNickname.ifBlank { "你" },
                        value = settings.displaySetting.userAvatar,
                        modifier = Modifier.size(40.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        ComposerField(
                            value = text,
                            placeholder = "发布你的回复",
                            onValueChange = onTextChange,
                        )
                    }
                }
            }
            XComposerBottomTray(textLength = text.length)
        }
    }
}

@Composable
private fun XComposerHeader(
    actionText: String,
    enabled: Boolean,
    onClose: () -> Unit,
    onAction: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(53.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable(onClick = onClose),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = XCloneIcons.Close,
                contentDescription = null,
                tint = XText,
                modifier = Modifier.size(20.dp)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "草稿", color = XBlue, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (enabled) XBlue else XBlue.copy(alpha = 0.45f))
                    .clickable(enabled = enabled, onClick = onAction)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = actionText, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AudienceChip() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFCFD9DE), RoundedCornerShape(999.dp))
            .clickable { }
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = "每个人", color = XBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Icon(
            imageVector = XCloneIcons.ChevronDown,
            contentDescription = null,
            tint = XBlue,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun ComposerField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 120.dp),
        textStyle = TextStyle(
            color = XText,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
        ),
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.fillMaxWidth()) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = XSubText,
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun XComposerBottomTray(textLength: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(XPanel)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AttachmentPickerCard()
            AttachmentPreviewCard(title = "Dog Meme", background = Color(0xFFE6DBCC))
            AttachmentPreviewCard(title = "Quote", background = Color.White)
            AttachmentPreviewCard(title = "Thread", background = Color.White)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = XCloneIcons.Info,
                contentDescription = null,
                tint = XBlue,
                modifier = Modifier.size(18.dp)
            )
            Text(text = "所有人可以回复", color = XBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider(color = XDivider)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(53.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                ComposerToolbarIcon(icon = XCloneIcons.Image)
                ComposerToolbarIcon(icon = XCloneIcons.Poll)
                ComposerToolbarIcon(icon = XCloneIcons.Lines)
                ComposerToolbarIcon(icon = XCloneIcons.Location)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProgressRing(progress = textLength.coerceAtMost(ComposerLimit) / ComposerLimit.toFloat())
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(Color(0xFFC4CDD5))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = XCloneIcons.Plus,
                        contentDescription = null,
                        tint = XBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AttachmentPickerCard() {
    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFCFD9DE), RoundedCornerShape(18.dp))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = XCloneIcons.Image,
            contentDescription = null,
            tint = XBlue,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun AttachmentPreviewCard(title: String, background: Color) {
    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(background)
            .border(1.dp, Color(0xFFCFD9DE), RoundedCornerShape(18.dp))
            .padding(10.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Text(
            text = title,
            color = if (background == Color.White) XSubText else XText.copy(alpha = 0.55f),
            fontSize = 8.sp,
            lineHeight = 10.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ComposerToolbarIcon(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = XBlue,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun ProgressRing(progress: Float) {
    Canvas(modifier = Modifier.size(28.dp)) {
        drawCircle(color = XDivider, style = Stroke(width = 2.dp.toPx()))
        if (progress > 0f) {
            drawArc(
                color = if (progress > 0.8f) XWarn else XBlue,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
private fun ReplyContextCard(post: XPostEntity) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            PostAvatar(post = post, size = 40.dp)
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(86.dp)
                    .background(Color(0xFFC4CDD5))
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = post.authorName, color = XText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                if (post.authorKind != "user") {
                    Icon(
                        imageVector = XCloneIcons.Verified,
                        contentDescription = null,
                        tint = XBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(text = post.authorHandle, color = XSubText, fontSize = 15.sp)
                Text(text = "·", color = XSubText, fontSize = 15.sp)
                Text(text = relativeTime(post.createAt), color = XSubText, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = post.content, color = XText, fontSize = 15.sp, lineHeight = 20.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "回复给 ${post.authorHandle}", color = XSubText, fontSize = 15.sp)
        }
    }
}

@Composable
private fun XFloatingComposeButton(
    aiPosting: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .padding(end = 16.dp, bottom = 72.dp)
            .size(56.dp)
            .clip(CircleShape)
            .background(if (aiPosting) XBlue.copy(alpha = 0.72f) else XBlue)
            .pressableScale(pressedScale = 0.92f, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = XCloneIcons.Plus,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun XBottomNavBar(
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    onSelect: (Int) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(XPanel)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(53.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val icons = listOf(
            XCloneIcons.Home,
            XCloneIcons.Search,
            XCloneIcons.Spark,
            XCloneIcons.Bell,
            XCloneIcons.Mail,
        )
        icons.forEachIndexed { index, icon ->
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (index == selectedIndex) XText else XText.copy(alpha = 0.88f),
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
private fun PostAvatar(post: XPostEntity, size: androidx.compose.ui.unit.Dp = 40.dp) {
    val background = when (post.authorKind) {
        "system" -> Color(0xFF111214)
        "bot" -> Color(0xFF1F2937)
        else -> Color(0xFFE5E7EB)
    }
    val foreground = if (background == Color(0xFFE5E7EB)) XText else Color.White
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        if (post.authorKind == "user") {
            Text(
                text = post.authorName.take(1).uppercase(),
                color = foreground,
                fontSize = (size.value * 0.42f).sp,
                fontWeight = FontWeight.Bold,
            )
        } else {
            Icon(
                imageVector = if (post.authorKind == "system") XCloneIcons.XLogo else XCloneIcons.XLogo,
                contentDescription = null,
                tint = foreground,
                modifier = Modifier.size(size * 0.48f)
            )
        }
    }
}

private fun relativeTime(timestamp: Long): String {
    val diffMinutes = ((System.currentTimeMillis() - timestamp) / 60_000L).coerceAtLeast(0)
    return when {
        diffMinutes < 1 -> "刚刚"
        diffMinutes < 60 -> "${diffMinutes}分钟"
        diffMinutes < 60 * 24 -> "${diffMinutes / 60}小时"
        else -> "${diffMinutes / (60 * 24)}天"
    }
}

private fun compactCount(count: Int): String {
    if (count <= 0) return "0"
    return when {
        count >= 10_000 -> {
            val value = count / 10_000f
            if (value >= 100f || value % 1f == 0f) {
                "${value.toInt()}万"
            } else {
                String.format("%.1f万", value)
            }
        }
        count >= 1_000 -> String.format("%.1fK", count / 1_000f)
        else -> count.toString()
    }
}

private object XCloneIcons {
    val XLogo = filledIcon(
        name = "x_logo",
        pathData = "M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z"
    )
    val More = filledIcon(
        name = "more",
        pathData = "M3 12c0-1.1.9-2 2-2s2 .9 2 2-.9 2-2 2-2-.9-2-2zm9 2c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm7 0c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2z"
    )
    val Home = filledIcon(
        name = "home",
        pathData = "M12 1.696L.622 8.807l1.06 1.696L3 9.679V19.5C3 20.881 4.119 22 5.5 22h13c1.381 0 2.5-1.119 2.5-2.5V9.679l1.318.824 1.06-1.696L12 1.696z"
    )
    val Search = filledIcon(
        name = "search",
        pathData = "M10.25 3.75c-3.59 0-6.5 2.91-6.5 6.5s2.91 6.5 6.5 6.5c1.795 0 3.419-.726 4.596-1.904 1.178-1.177 1.904-2.801 1.904-4.596 0-3.59-2.91-6.5-6.5-6.5zm-8.5 6.5c0-4.694 3.806-8.5 8.5-8.5s8.5 3.806 8.5 8.5c0 1.986-.682 3.815-1.824 5.262l4.781 4.781-1.414 1.414-4.781-4.781c-1.447 1.142-3.276 1.824-5.262 1.824-4.694 0-8.5-3.806-8.5-8.5z"
    )
    val Spark = filledIcon(
        name = "spark",
        pathData = "M18.15 3h-2.31l-7.7 18h2.31l7.7-18z"
    )
    val Bell = filledIcon(
        name = "bell",
        pathData = "M12 22c1.868 0 3.395-1.432 3.49-3.264l.01-.236h-7l.01.236C8.605 20.568 10.132 22 12 22zM21 16.5v-1.132l-2-2.667V8.5C19 4.91 16.09 2 12.5 2h-1C7.91 2 5 4.91 5 8.5v4.201l-2 2.667V16.5h18z"
    )
    val Mail = filledIcon(
        name = "mail",
        pathData = "M1.998 5.5c0-1.381 1.119-2.5 2.5-2.5h15c1.381 0 2.5 1.119 2.5 2.5v13c0 1.381-1.119 2.5-2.5 2.5h-15c-1.381 0-2.5-1.119-2.5-2.5v-13zm2.5-.5c-.276 0-.5.224-.5.5v1.441l8 5.714 8-5.714V5.5c0-.276-.224-.5-.5-.5h-15z"
    )
    val Reply = filledIcon(
        name = "reply",
        pathData = "M1.751 10c0-4.42 3.584-8 8.005-8h4.366c4.49 0 8.129 3.64 8.129 8.13 0 2.96-1.607 5.68-4.196 7.11l-8.054 4.46v-3.69h-.067c-4.49.1-8.183-3.51-8.183-8.01zm8.005-6c-3.317 0-6.005 2.69-6.005 6 0 3.37 2.77 6.08 6.138 6.01l.351-.01h1.761v2.3l5.087-2.81c1.951-1.08 3.163-3.13 3.163-5.36 0-3.39-2.744-6.13-6.129-6.13H9.756z"
    )
    val Repost = filledIcon(
        name = "repost",
        pathData = "M4.5 3.88l4.432 4.14-1.364 1.46L5.5 7.55V16c0 1.1.896 2 2 2H13v2H7.5c-2.209 0-4-1.79-4-4V7.55L1.432 9.48.068 8.02 4.5 3.88zM16.5 6H11V4h5.5c2.209 0 4 1.79 4 4v8.45l2.068-1.93 1.364 1.46-4.432 4.14-4.432-4.14 1.364-1.46 2.068 1.93V8c0-1.1-.896-2-2-2z"
    )
    val Like = filledIcon(
        name = "like",
        pathData = "M16.697 5.5c-1.222-.06-2.679.51-3.89 2.16l-.805 1.09-.806-1.09C9.984 6.01 8.526 5.44 7.304 5.5c-1.243.07-2.349.78-2.91 1.91-.552 1.12-.633 2.78.479 4.82 1.074 1.97 3.257 4.27 7.129 6.61 3.87-2.34 6.052-4.64 7.126-6.61 1.111-2.04 1.03-3.7.477-4.82-.561-1.13-1.666-1.84-2.908-1.91zm4.187 7.69c-1.351 2.48-4.001 5.12-8.379 7.67l-.503.3-.504-.3c-4.379-2.55-7.029-5.19-8.382-7.67-1.36-2.5-1.41-4.86-.514-6.67.887-1.79 2.647-2.91 4.601-3.01 1.651-.09 3.368.56 4.798 2.01 1.429-1.45 3.146-2.1 4.796-2.01 1.954.1 3.714 1.22 4.601 3.01.896 1.81.846 4.17-.514 6.67z"
    )
    val Stats = filledIcon(
        name = "stats",
        pathData = "M8.75 21V3h2v18h-2zM18 21V8.5h2V21h-2zM4 21l.004-10h2L6 21H4zm9.248 0v-7h2v7h-2z"
    )
    val Bookmark = filledIcon(
        name = "bookmark",
        pathData = "M4 4.5C4 3.12 5.119 2 6.5 2h11C18.881 2 20 3.12 20 4.5v18.44l-8-5.71-8 5.71V4.5zM6.5 4c-.276 0-.5.22-.5.5v14.56l6-4.29 6 4.29V4.5c0-.28-.224-.5-.5-.5h-11z"
    )
    val Share = filledIcon(
        name = "share",
        pathData = "M12 2.59l5.7 5.7-1.41 1.42L13 6.41V16h-2V6.41l-3.3 3.3-1.41-1.42L12 2.59zM21 15l-.02 3.51c0 1.38-1.12 2.49-2.5 2.49H5.5C4.11 21 3 19.88 3 18.5V15h2v3.5c0 .28.22.5.5.5h12.98c.28 0 .5-.22.5-.5L19 15h2z"
    )
    val Back = filledIcon(
        name = "back",
        pathData = "M7.414 13l5.043 5.04-1.414 1.42L3.586 12l7.457-7.46 1.414 1.42L7.414 11H21v2H7.414z"
    )
    val Close = filledIcon(
        name = "close",
        pathData = "M10.59 12L4.54 5.96l1.42-1.42L12 10.59l6.04-6.05 1.42 1.42L13.41 12l6.05 6.04-1.42 1.42L12 13.41l-6.04 6.05-1.42-1.42L10.59 12z"
    )
    val Plus = strokedIcon(
        name = "plus",
        pathData = "M12 4.5v15m7.5-7.5h-15"
    )
    val Image = filledIcon(
        name = "image",
        pathData = "M3 5.5C3 4.119 4.119 3 5.5 3h13C19.881 3 21 4.119 21 5.5v13c0 1.381-1.119 2.5-2.5 2.5h-13C4.119 21 3 19.881 3 18.5v-13zM5.5 5c-.276 0-.5.224-.5.5v9.086l3-3 3 3 5-5 3 3V5.5c0-.276-.224-.5-.5-.5h-13zM19 15.414l-3-3-5 5-3-3-3 3V18.5c0 .276.224.5.5.5h13c.276 0 .5-.224.5-.5v-3.086zM9.75 7C8.784 7 8 7.784 8 8.75s.784 1.75 1.75 1.75 1.75-.784 1.75-1.75S10.716 7 9.75 7z"
    )
    val Poll = filledIcon(
        name = "poll",
        pathData = "M3 5.5C3 4.119 4.119 3 5.5 3h13C19.881 3 21 4.119 21 5.5v13c0 1.381-1.119 2.5-2.5 2.5h-13C4.119 21 3 19.881 3 18.5v-13zM5.5 5c-.276 0-.5.224-.5.5v13c0 .276.224.5.5.5h13c.276 0 .5-.224.5-.5v-13c0-.276-.224-.5-.5-.5h-13zM7.5 14h2.25v-2.25H7.5v2.25zM16 14h-1.5V9.5H16V14zm-4.75 0H9v-4.5h2.25v1.25H10.5v2H11.25V14z"
    )
    val Lines = filledIcon(
        name = "lines",
        pathData = "M4 4h16v2H4V4zm0 6h16v2H4v-2zm0 6h16v2H4v-2z"
    )
    val Location = filledIcon(
        name = "location",
        pathData = "M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"
    )
    val Verified = ImageVector.Builder(
        name = "verified",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            pathData = addPathNodes("M22.5 12.5c0-1.58-.875-2.95-2.148-3.6.154-.435.238-.905.238-1.4 0-2.21-1.71-3.998-3.918-3.998-.47 0-.92.084-1.336.25C14.818 2.415 13.51 1.5 12 1.5s-2.816.917-3.337 2.25c-.416-.165-.866-.25-1.336-.25-2.21 0-3.918 1.79-3.918 4 0 .495.084.965.238 1.4-1.273.65-2.148 2.02-2.148 3.6 0 1.46.74 2.746 1.867 3.447.016.15.025.3.025.453 0 2.21 1.71 4 3.918 4 .47 0 .92-.086 1.336-.25.52 1.334 1.826 2.25 3.337 2.25s2.816-.916 3.337-2.25c.416.164.866.25 1.336.25 2.21 0 3.918-1.79 3.918-4 0-.153-.01-.303-.026-.453C21.76 15.247 22.5 13.96 22.5 12.5zm-11.23 4.25l-3.328-3.326.896-.897 2.433 2.43 5.432-5.43.896.896-6.328 6.327z"),
            fill = SolidColor(Color(0xFF1D9BF0)),
            pathFillType = PathFillType.NonZero
        )
    }.build()
    val ChevronDown = filledIcon(
        name = "chevron_down",
        pathData = "M12 15.25l-7-7 1.5-1.5L12 12.25l5.5-5.5 1.5 1.5-7 7z"
    )
    val Info = filledIcon(
        name = "info",
        pathData = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 14H9v-2h2v2zm0-4H9V7h2v5z"
    )

    private fun filledIcon(name: String, pathData: String) = ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            pathData = addPathNodes(pathData),
            fill = SolidColor(Color.Black),
            pathFillType = PathFillType.NonZero
        )
    }.build()

    private fun strokedIcon(name: String, pathData: String) = ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            pathData = addPathNodes(pathData),
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathFillType = PathFillType.NonZero
        )
    }.build()
}
