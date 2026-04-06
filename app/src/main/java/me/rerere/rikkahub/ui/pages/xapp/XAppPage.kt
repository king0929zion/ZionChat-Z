package me.rerere.rikkahub.ui.pages.xapp

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.dokar.sonner.ToastType
import kotlinx.coroutines.delay
import me.rerere.rikkahub.R
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.data.model.Avatar
import me.rerere.rikkahub.data.x.XAuthor
import me.rerere.rikkahub.data.x.XMedia
import me.rerere.rikkahub.data.x.XPost
import me.rerere.rikkahub.data.x.XPostSource
import me.rerere.rikkahub.data.x.XResolvedPost
import me.rerere.rikkahub.ui.components.ui.Tag
import me.rerere.rikkahub.ui.components.ui.TagType
import me.rerere.rikkahub.ui.components.ui.UIAvatar
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.context.LocalToaster
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.theme.CustomColors
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.min

private val XOuter = Color(0xFF242D34)
private val XBlack = Color(0xFF000000)
private val XWhite = Color(0xFFFFFFFF)
private val XText = Color(0xFF0F1419)
private val XSubText = Color(0xFF536471)
private val XBorder = Color(0xFFEFF3F4)
private val XBlue = Color(0xFF1D9BF0)
private val XBlueSoft = Color(0x1A1D9BF0)
private val XGreen = Color(0xFF00BA7C)
private val XGreenSoft = Color(0x1A00BA7C)
private val XPink = Color(0xFFF91880)
private val XPinkSoft = Color(0x1AF91880)
private val XYellow = Color(0xFFFFD400)
private val XInput = Color(0xFFEFF3F4)
private val XThread = Color(0xFFCFD9DE)
private val XDisabledBlue = Color(0xFF8ECDF8)
private val XCode = Color(0xFF1C2128)
private const val XComposePost = "compose-post"
private const val XComposeReply = "compose-reply"
private const val XMaxChars = 280

private enum class XBottomBarItem {
    Home,
    Search,
    Pulse,
    Alerts,
    Messages,
}

@Composable
fun XAppPage(vm: XAppVM = koinViewModel()) {
    val navController = LocalNavController.current
    val toaster = LocalToaster.current
    val timeline by vm.timeline.collectAsStateWithLifecycle()
    val currentUser = remember(timeline) { vm.currentUser(timeline) }

    var selectedTab by rememberSaveable { mutableStateOf(XFeedTab.FOR_YOU) }
    var selectedBottomBar by rememberSaveable { mutableStateOf(XBottomBarItem.Home) }
    var detailPostId by rememberSaveable { mutableStateOf<String?>(null) }
    var renderedDetailPostId by rememberSaveable { mutableStateOf<String?>(null) }
    var composerMode by rememberSaveable { mutableStateOf<String?>(null) }
    var renderedComposerMode by rememberSaveable { mutableStateOf<String?>(null) }
    var replyTargetId by rememberSaveable { mutableStateOf<String?>(null) }
    var renderedReplyTargetId by rememberSaveable { mutableStateOf<String?>(null) }
    var quotePostId by rememberSaveable { mutableStateOf<String?>(null) }
    var renderedQuotePostId by rememberSaveable { mutableStateOf<String?>(null) }

    val feedPosts = remember(timeline, selectedTab) {
        vm.topLevelPosts(timeline, selectedTab)
    }
    val detailPost = remember(timeline, detailPostId) {
        vm.selectedPost(timeline, detailPostId)
    }
    val renderedDetailPost = remember(timeline, renderedDetailPostId) {
        vm.selectedPost(timeline, renderedDetailPostId)
    }
    val detailReplies = remember(timeline, renderedDetailPostId) {
        vm.repliesFor(timeline, renderedDetailPostId)
    }
    val replyTargetPost = remember(timeline, renderedReplyTargetId) {
        vm.selectedPost(timeline, renderedReplyTargetId)
    }
    val quotePost = remember(timeline, renderedQuotePostId) {
        vm.selectedPost(timeline, renderedQuotePostId)
    }

    fun focusPostId(post: XResolvedPost): String = post.post.detailFocusPostId ?: post.post.id

    fun openDetail(post: XResolvedPost) {
        detailPostId = focusPostId(post)
    }

    fun openReply(post: XResolvedPost) {
        val targetId = focusPostId(post)
        detailPostId = targetId
        replyTargetId = targetId
        quotePostId = null
        composerMode = XComposeReply
    }

    fun openQuoteComposer(post: XResolvedPost?) {
        quotePostId = post?.let(::focusPostId)
        replyTargetId = null
        composerMode = XComposePost
    }

    fun closeComposer() {
        composerMode = null
        replyTargetId = null
        quotePostId = null
    }

    LaunchedEffect(detailPostId) {
        if (detailPostId != null) {
            renderedDetailPostId = detailPostId
        } else {
            delay(260)
            if (detailPostId == null) renderedDetailPostId = null
        }
    }

    LaunchedEffect(composerMode, replyTargetId, quotePostId) {
        if (composerMode != null) {
            renderedComposerMode = composerMode
            renderedReplyTargetId = replyTargetId
            renderedQuotePostId = quotePostId
        } else {
            delay(260)
            if (composerMode == null) {
                renderedComposerMode = null
                renderedReplyTargetId = null
                renderedQuotePostId = null
            }
        }
    }

    LaunchedEffect(detailPostId) {
        detailPostId?.let(vm::recordView)
    }

    LaunchedEffect(detailPostId, detailPost) {
        if (detailPostId != null && detailPost == null) {
            detailPostId = null
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(XOuter)
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }
        val detailVisible = detailPostId != null
        val feedShift by animateFloatAsState(
            targetValue = if (detailVisible) -widthPx else 0f,
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
            label = "feedShift"
        )
        val detailShift by animateFloatAsState(
            targetValue = if (detailVisible) 0f else widthPx,
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
            label = "detailShift"
        )
        val composePostShift by animateFloatAsState(
            targetValue = if (composerMode == XComposePost) 0f else heightPx,
            animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
            label = "composePostShift"
        )
        val composeReplyShift by animateFloatAsState(
            targetValue = if (composerMode == XComposeReply) 0f else heightPx,
            animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
            label = "composeReplyShift"
        )

        XScreenLayer(translationX = feedShift, zIndex = 1f) {
            XFeedLayer(
                posts = feedPosts,
                currentUser = currentUser,
                selectedTab = selectedTab,
                selectedBottomBar = selectedBottomBar,
                onBack = { navController.popBackStack() },
                onOpenMore = { navController.navigate(Screen.SettingXPlugin) },
                onSelectTab = { selectedTab = it },
                onSelectBottomBar = { selectedBottomBar = it },
                onOpenPost = ::openDetail,
                onReplyToPost = ::openReply,
                onQuotePost = ::openQuoteComposer,
                onLike = { vm.toggleLike(it.post.id) },
                onRepost = { vm.toggleRepost(it.post.id) },
                onCompose = { openQuoteComposer(null) },
            )
        }

        if (renderedDetailPost != null) {
            XScreenLayer(translationX = detailShift, zIndex = 2f) {
                XDetailLayer(
                    currentUser = currentUser,
                    post = renderedDetailPost,
                    replies = detailReplies,
                    onBack = { detailPostId = null },
                    onOpenPost = { openDetail(it) },
                    onReply = { openReply(renderedDetailPost) },
                )
            }
        }

        if (renderedComposerMode == XComposePost) {
            XScreenLayer(translationY = composePostShift, zIndex = 3f) {
                XComposePostLayer(
                    currentUser = currentUser,
                    quotePost = quotePost,
                    onClose = ::closeComposer,
                    onSubmit = { text ->
                        vm.publishPost(text = text, quotePostId = quotePost?.post?.id) { post ->
                            toaster.show("帖子已发布", type = ToastType.Success)
                            detailPostId = post.id
                            closeComposer()
                        }
                    }
                )
            }
        }

        if (renderedComposerMode == XComposeReply) {
            XScreenLayer(translationY = composeReplyShift, zIndex = 4f) {
                XComposeReplyLayer(
                    currentUser = currentUser,
                    replyTarget = replyTargetPost,
                    onClose = ::closeComposer,
                    onSubmit = { text ->
                        replyTargetPost?.post?.id?.let { targetId ->
                            vm.replyToPost(targetId, text) {
                                toaster.show("回复已发布", type = ToastType.Success)
                                detailPostId = targetId
                                closeComposer()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun XScreenLayer(
    translationX: Float = 0f,
    translationY: Float = 0f,
    zIndex: Float,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(zIndex)
            .graphicsLayer {
                this.translationX = translationX
                this.translationY = translationY
            },
        content = content
    )
}

@Composable
private fun XFeedLayer(
    posts: List<XResolvedPost>,
    currentUser: XAuthor?,
    selectedTab: XFeedTab,
    selectedBottomBar: XBottomBarItem,
    onBack: () -> Unit,
    onOpenMore: () -> Unit,
    onSelectTab: (XFeedTab) -> Unit,
    onSelectBottomBar: (XBottomBarItem) -> Unit,
    onOpenPost: (XResolvedPost) -> Unit,
    onReplyToPost: (XResolvedPost) -> Unit,
    onQuotePost: (XResolvedPost?) -> Unit,
    onLike: (XResolvedPost) -> Unit,
    onRepost: (XResolvedPost) -> Unit,
    onCompose: () -> Unit,
) {
    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val topBarHeight = 53.dp
    val tabsHeight = 53.dp
    val topBarHeightPx = with(density) { topBarHeight.toPx() }
    val collapseProgressTarget = if (listState.firstVisibleItemIndex > 0) {
        1f
    } else {
        (listState.firstVisibleItemScrollOffset.toFloat() / topBarHeightPx).coerceIn(0f, 1f)
    }
    val topBarCollapseProgress by animateFloatAsState(
        targetValue = collapseProgressTarget,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "feedTopBarCollapse"
    )
    val topBarCollapsePx = topBarHeightPx * topBarCollapseProgress
    val topBarVisibleHeightDp = with(density) { (topBarHeightPx * (1f - topBarCollapseProgress)).toDp() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(XBlack)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(XWhite)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = topInset + tabsHeight + topBarVisibleHeightDp,
                    bottom = bottomInset + 92.dp
                )
            ) {
                items(posts, key = { it.post.id }) { post ->
                    XFeedPostCard(
                        post = post,
                        onOpen = { onOpenPost(post) },
                        onReply = { onReplyToPost(post) },
                        onOpenQuote = { quoted -> onOpenPost(quoted) },
                        onLike = { onLike(post) },
                        onRepost = { onRepost(post) },
                        onShare = { onQuotePost(post) },
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .background(XWhite)
            ) {
                Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(topBarVisibleHeightDp)
                        .clipToBounds()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer { translationY = -topBarCollapsePx }
                    ) {
                        XFeedTopBar(
                            currentUser = currentUser,
                            onBack = onBack,
                            onOpenMore = onOpenMore,
                        )
                    }
                }
                XFeedTabs(
                    selectedTab = selectedTab,
                    onSelectTab = onSelectTab,
                )
            }

            XFloatingComposeButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = bottomInset + 72.dp),
                onClick = onCompose
            )

            XBottomBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                selectedItem = selectedBottomBar,
                onSelect = onSelectBottomBar,
            )
        }
    }
}

@Composable
private fun XFeedTopBar(
    currentUser: XAuthor?,
    onBack: () -> Unit,
    onOpenMore: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(53.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        XAvatarButton(author = currentUser, onClick = onBack)
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "X",
                color = XText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        XCircleHitTarget(onClick = onOpenMore) {
            XMoreIcon(tint = XSubText)
        }
    }
}

@Composable
private fun XFeedTabs(
    selectedTab: XFeedTab,
    onSelectTab: (XFeedTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .background(XWhite)
    ) {
        XFeedTab.values().forEach { tab ->
            val selected = tab == selectedTab
            val textColor by animateColorAsState(
                targetValue = if (selected) XText else XSubText,
                animationSpec = tween(durationMillis = 180),
                label = "tabColor"
            )
            Box(
                modifier = Modifier
                    .widthIn(min = if (tab == XFeedTab.AI_WATCH) 190.dp else 122.dp)
                    .height(53.dp)
                    .pressableScale(pressedScale = 0.98f) { onSelectTab(tab) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.title,
                    color = textColor,
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
    HorizontalDivider(color = XBorder)
}

@Composable
private fun XFeedPostCard(
    post: XResolvedPost,
    onOpen: () -> Unit,
    onReply: () -> Unit,
    onOpenQuote: (XResolvedPost) -> Unit,
    onLike: () -> Unit,
    onRepost: () -> Unit,
    onShare: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(XWhite)
            .pressableScale(pressedScale = 0.99f, onClick = onOpen)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            XAvatar(author = post.author, size = 40.dp)
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        XAuthorLine(author = post.author, timeLabel = displayFeedTime(post.post))
                    }
                    XCircleHitTarget(size = 28.dp, onClick = {}) {
                        XMoreIcon(tint = XSubText)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                  Text(
                      text = post.post.body,
                      color = XText,
                      fontSize = 15.sp,
                      lineHeight = 20.sp,
                  )
                  post.quotedPost?.let { quoted ->
                      Spacer(modifier = Modifier.height(12.dp))
                      XQuoteCard(
                          post = quoted,
                        onOpen = { onOpenQuote(quoted) }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                XActionRow(
                    post = post.post,
                    onReply = onReply,
                    onRepost = onRepost,
                    onLike = onLike,
                    onView = onOpen,
                    onShare = onShare,
                )
            }
        }
    }
    HorizontalDivider(color = XBorder)
}

@Composable
private fun XAuthorLine(
    author: XAuthor,
    timeLabel: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = author.displayName,
            color = XText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (author.verified) {
            XVerifiedBadge()
        }
        if (author.handle == "@elonmusk") {
            XMiniXBadge()
        }
        Text(
            text = author.handle,
            color = XSubText,
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(text = "·", color = XSubText, fontSize = 15.sp)
        Text(
            text = timeLabel,
            color = XSubText,
            fontSize = 15.sp,
        )
    }
}

@Composable
private fun XQuoteCard(
    post: XResolvedPost,
    onOpen: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(width = 1.dp, color = XBorder, shape = RoundedCornerShape(16.dp))
            .background(XWhite)
            .pressableScale(pressedScale = 0.985f, onClick = onOpen)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                XAvatar(author = post.author, size = 20.dp)
                Text(
                    text = post.author.displayName,
                    color = XText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
                if (post.author.verified) {
                    XVerifiedBadge(size = 15.dp)
                }
                Text(
                    text = post.author.handle,
                    color = XSubText,
                    fontSize = 15.sp,
                )
                Text(text = "·", color = XSubText, fontSize = 15.sp)
                Text(
                    text = displayFeedTime(post.post),
                    color = XSubText,
                    fontSize = 15.sp,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = post.post.body,
                color = XText,
                fontSize = 15.sp,
                lineHeight = 20.sp,
            )
        }
        post.post.media.firstOrNull()?.let { media ->
            XQuoteMediaPreview(media = media)
        }
    }
}

@Composable
private fun XQuoteMediaPreview(media: XMedia) {
    when (media.template) {
        "article_preview" -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(xColor(media.tintHex, Color(0xFFBDE3F3)))
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .height(110.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .background(XWhite)
                        .border(
                            width = 1.dp,
                            color = XBorder,
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .width(96.dp)
                                .height(10.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color(0xFFE5E7EB))
                        )
                        Box(
                            modifier = Modifier
                                .width(128.dp)
                                .height(10.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color(0xFFE5E7EB))
                        )
                    }
                }
            }
        }

        else -> {
            media.imageUrl?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}

@Composable
private fun XActionRow(
    post: XPost,
    onReply: () -> Unit,
    onRepost: () -> Unit,
    onLike: () -> Unit,
    onView: () -> Unit,
    onShare: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        XActionIconButton(
            tint = XSubText,
            count = formatMetric(post.replyCount),
            onClick = onReply,
        ) {
            XReplyIcon(tint = it)
        }
        XActionIconButton(
            tint = if (post.repostedByMe) XGreen else XSubText,
            count = formatMetric(post.repostCount),
            background = if (post.repostedByMe) XGreenSoft else Color.Transparent,
            onClick = onRepost,
        ) {
            XRepostIcon(tint = it)
        }
        XActionIconButton(
            tint = if (post.likedByMe) XPink else XSubText,
            count = formatMetric(post.likeCount),
            background = if (post.likedByMe) XPinkSoft else Color.Transparent,
            onClick = onLike,
        ) {
            XLikeIcon(tint = it)
        }
        XActionIconButton(
            tint = XSubText,
            count = formatMetric(post.viewCount),
            onClick = onView,
        ) {
            XViewIcon(tint = it)
        }
        XActionIconButton(
            tint = XSubText,
            onClick = onShare,
        ) {
            XShareIcon(tint = it)
        }
    }
}

@Composable
private fun XActionIconButton(
    tint: Color,
    count: String? = null,
    background: Color = Color.Transparent,
    onClick: () -> Unit,
    icon: @Composable (Color) -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .pressableScale(pressedScale = 0.95f, onClick = onClick)
            .padding(horizontal = 2.dp, vertical = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(background),
            contentAlignment = Alignment.Center
        ) {
            icon(tint)
        }
        if (count != null) {
            Text(
                text = count,
                color = tint,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun XBottomBar(
    selectedItem: XBottomBarItem,
    onSelect: (XBottomBarItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(XWhite)
    ) {
        HorizontalDivider(color = XBorder)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(53.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            XBottomBarButton(
                selected = selectedItem == XBottomBarItem.Home,
                onClick = { onSelect(XBottomBarItem.Home) }
            ) {
                XHomeIcon(tint = XText, filled = selectedItem == XBottomBarItem.Home)
            }
            XBottomBarButton(
                selected = selectedItem == XBottomBarItem.Search,
                onClick = { onSelect(XBottomBarItem.Search) }
            ) {
                Icon(
                    imageVector = ZionAppIcons.Search,
                    contentDescription = null,
                    tint = XText,
                    modifier = Modifier.size(24.dp)
                )
            }
            XBottomBarButton(
                selected = selectedItem == XBottomBarItem.Pulse,
                onClick = { onSelect(XBottomBarItem.Pulse) }
            ) {
                XPulseIcon(tint = XText)
            }
            XBottomBarButton(
                selected = selectedItem == XBottomBarItem.Alerts,
                onClick = { onSelect(XBottomBarItem.Alerts) }
            ) {
                XBellIcon(tint = XText)
            }
            XBottomBarButton(
                selected = selectedItem == XBottomBarItem.Messages,
                onClick = { onSelect(XBottomBarItem.Messages) }
            ) {
                XMailIcon(tint = XText)
            }
        }
        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}

@Composable
private fun XBottomBarButton(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
    val alpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0.92f,
        animationSpec = tween(durationMillis = 160),
        label = "bottomAlpha"
    )
    Box(
        modifier = Modifier
            .size(40.dp)
            .graphicsLayer { this.alpha = alpha }
            .pressableScale(pressedScale = 0.95f, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

@Composable
private fun XFloatingComposeButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(XBlue)
            .pressableScale(pressedScale = 0.95f, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ZionAppIcons.Plus,
            contentDescription = null,
            tint = XWhite,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun XDetailLayer(
    currentUser: XAuthor?,
    post: XResolvedPost,
    replies: List<XResolvedPost>,
    onBack: () -> Unit,
    onOpenPost: (XResolvedPost) -> Unit,
    onReply: () -> Unit,
) {
    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(XWhite)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = topInset + 53.dp,
                bottom = bottomInset + 80.dp
            )
        ) {
              item {
                  XDetailPostContent(
                      post = post,
                  )
              }
            item {
                HorizontalDivider(color = XBorder, modifier = Modifier.padding(horizontal = 16.dp))
                XDetailStats(post = post.post)
                HorizontalDivider(color = XBorder)
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "最相关的回复",
                        color = XText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                HorizontalDivider(color = XBorder)
            }
            items(replies, key = { it.post.id }) { reply ->
                XDetailReplyCard(
                    reply = reply,
                    parentHandle = post.author.handle,
                    onOpen = { onOpenPost(reply) }
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(XWhite)
        ) {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(53.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                XCircleHitTarget(onClick = onBack) {
                    Icon(
                        imageVector = ZionAppIcons.Back,
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
        }

        XReplyInputBar(
            currentUser = currentUser,
            modifier = Modifier.align(Alignment.BottomCenter),
            onReply = onReply,
        )
    }
}

@Composable
private fun XDetailPostContent(
    post: XResolvedPost,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                XAvatar(author = post.author, size = 40.dp)
                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = post.author.displayName,
                            color = XText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        if (post.author.verified) {
                            XVerifiedBadge()
                        }
                    }
                    Text(
                        text = post.author.handle,
                        color = XSubText,
                        fontSize = 15.sp,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(XText)
                    .pressableScale(pressedScale = 0.95f, onClick = {})
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "订阅",
                    color = XWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = post.post.body,
            color = XText,
            fontSize = 17.sp,
            lineHeight = 24.sp,
        )
        if (post.quotedPost != null) {
            Spacer(modifier = Modifier.height(14.dp))
            XQuoteCard(post = post.quotedPost, onOpen = {})
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayDetailTime(post.post),
                color = XSubText,
                fontSize = 15.sp,
            )
        }
    }
}

@Composable
private fun XDetailStats(post: XPost) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        XStatLabel(value = formatMetric(post.repostCount), label = "转帖")
        XStatLabel(value = formatMetric(post.likeCount), label = "喜欢")
    }
}

@Composable
private fun XStatLabel(
    value: String,
    label: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value,
            color = XText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            color = XSubText,
            fontSize = 15.sp,
        )
    }
}

@Composable
private fun XDetailReplyCard(
    reply: XResolvedPost,
    parentHandle: String,
    onOpen: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(XWhite)
            .pressableScale(pressedScale = 0.99f, onClick = onOpen)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            XAvatar(author = reply.author, size = 40.dp)
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    XAuthorLine(
                        author = reply.author,
                        timeLabel = displayFeedTime(reply.post),
                    )
                    XCircleHitTarget(size = 28.dp, onClick = {}) {
                        XMoreIcon(tint = XSubText)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "回复给 $parentHandle",
                    color = XSubText,
                    fontSize = 15.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = reply.post.body,
                    color = XText,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                )
            }
        }
    }
    HorizontalDivider(color = XBorder)
}

@Composable
private fun XReplyInputBar(
    currentUser: XAuthor?,
    onReply: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(XWhite)
    ) {
        HorizontalDivider(color = XBorder)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            XAvatar(author = currentUser, size = 32.dp)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(999.dp))
                    .background(XInput.copy(alpha = 0.5f))
                    .pressableScale(pressedScale = 0.99f, onClick = onReply)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "发布你的回复",
                    color = XSubText,
                    fontSize = 15.sp,
                )
            }
        }
        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}

@Composable
private fun XComposePostLayer(
    currentUser: XAuthor?,
    quotePost: XResolvedPost?,
    onClose: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    var text by rememberSaveable(quotePost?.post?.id) { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val canSubmit = text.trim().isNotEmpty() && text.length <= XMaxChars

    LaunchedEffect(Unit) {
        delay(280)
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(XWhite)
            .imePadding()
    ) {
        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        XComposePostTopBar(
            enabled = canSubmit,
            onClose = onClose,
            onSubmit = { onSubmit(text.trim()) }
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                XAvatar(author = currentUser, size = 40.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Spacer(modifier = Modifier.height(10.dp))
                    XAudiencePill()
                    Spacer(modifier = Modifier.height(10.dp))
                    XComposerInput(
                        value = text,
                        onValueChange = { text = it },
                        placeholder = "有什么新鲜事？",
                        focusRequester = focusRequester,
                    )
                }
            }
            if (quotePost != null) {
                Spacer(modifier = Modifier.height(18.dp))
                XInlineContextCard(
                    title = "引用的发帖",
                    post = quotePost,
                )
            }
        }
        XComposePostBottomPanel(textLength = text.length)
    }
}

@Composable
private fun XComposeReplyLayer(
    currentUser: XAuthor?,
    replyTarget: XResolvedPost?,
    onClose: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    var text by rememberSaveable(replyTarget?.post?.id) { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val canSubmit = text.trim().isNotEmpty() && text.length <= XMaxChars

    LaunchedEffect(Unit) {
        delay(280)
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(XWhite)
            .imePadding()
    ) {
        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        XComposeReplyTopBar(
            enabled = canSubmit,
            onClose = onClose,
            onSubmit = { onSubmit(text.trim()) }
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            replyTarget?.let {
                XReplyThreadContext(post = it)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                XAvatar(author = currentUser, size = 40.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Spacer(modifier = Modifier.height(10.dp))
                    XComposerInput(
                        value = text,
                        onValueChange = { text = it },
                        placeholder = "发布你的回复",
                        focusRequester = focusRequester,
                    )
                }
            }
        }
        XComposeReplyBottomPanel(textLength = text.length, replyTarget = replyTarget)
    }
}

@Composable
private fun XComposePostTopBar(
    enabled: Boolean,
    onClose: () -> Unit,
    onSubmit: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(53.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        XCircleHitTarget(onClick = onClose) {
            Icon(
                imageVector = ZionAppIcons.Close,
                contentDescription = null,
                tint = XText,
                modifier = Modifier.size(20.dp)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "草稿",
                color = XBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            XPrimaryPillButton(
                text = "发帖",
                enabled = enabled,
                onClick = onSubmit,
            )
        }
    }
    HorizontalDivider(color = XBorder)
}

@Composable
private fun XComposeReplyTopBar(
    enabled: Boolean,
    onClose: () -> Unit,
    onSubmit: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(53.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        XCircleHitTarget(onClick = onClose) {
            Icon(
                imageVector = ZionAppIcons.Close,
                contentDescription = null,
                tint = XText,
                modifier = Modifier.size(20.dp)
            )
        }
        XPrimaryPillButton(
            text = "回复",
            enabled = enabled,
            onClick = onSubmit,
        )
    }
    HorizontalDivider(color = XBorder)
}

@Composable
private fun XPrimaryPillButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (enabled) XBlue else XDisabledBlue)
            .pressableScale(enabled = enabled, pressedScale = 0.95f, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = XWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun XAudiencePill() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .border(1.dp, Color(0xFFCFD9DE), RoundedCornerShape(999.dp))
            .background(XWhite)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "每个人",
            color = XBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
        XCaretDownIcon(tint = XBlue)
    }
}

@Composable
private fun XComposerInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    focusRequester: FocusRequester,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        textStyle = TextStyle(
            color = XText,
            fontSize = 20.sp,
            lineHeight = 24.sp,
        ),
        cursorBrush = SolidColor(XBlue),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
        minLines = 5,
        maxLines = 10,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
            ) {
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
private fun XInlineContextCard(
    title: String,
    post: XResolvedPost,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, XBorder, RoundedCornerShape(16.dp))
            .background(XWhite)
            .padding(12.dp)
    ) {
        Text(
            text = title,
            color = XSubText,
            fontSize = 13.sp,
        )
        Spacer(modifier = Modifier.height(6.dp))
        XAuthorLine(author = post.author, timeLabel = displayFeedTime(post.post))
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = post.post.body,
            color = XText,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun XReplyThreadContext(post: XResolvedPost) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier.width(40.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            XAvatar(author = post.author, size = 40.dp)
            Box(
                modifier = Modifier
                    .padding(top = 40.dp)
                    .width(2.dp)
                    .height(86.dp)
                    .background(XThread)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            XAuthorLine(author = post.author, timeLabel = displayFeedTime(post.post))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = post.post.body,
                color = XText,
                fontSize = 15.sp,
                lineHeight = 20.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "回复给 ${post.author.handle} 和 @grok",
                color = XSubText,
                fontSize = 15.sp,
            )
        }
    }
}

@Composable
private fun XComposePostBottomPanel(textLength: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(XWhite)
    ) {
        XComposePostAttachmentStrip()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, XBorder)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            XInfoIcon(tint = XBlue)
            Text(
                text = "所有人可以回复",
                color = XBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        XComposeToolbar(textLength = textLength)
    }
}

@Composable
private fun XComposeReplyBottomPanel(
    textLength: Int,
    replyTarget: XResolvedPost?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(XWhite)
    ) {
        XComposeReplyAttachmentStrip(replyTarget = replyTarget)
        XComposeToolbar(textLength = textLength)
    }
}

@Composable
private fun XComposePostAttachmentStrip() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        XSquareAttachmentButton {
            Icon(
                imageVector = ZionAppIcons.Camera,
                contentDescription = null,
                tint = XBlue,
                modifier = Modifier.size(28.dp)
            )
        }
        XImageAttachmentCard(
            imageUrl = "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=300&q=80",
            background = Color(0xFFE6DBCC)
        )
        XSkeletonAttachmentCard(
            lines = listOf(0.55f, 0.75f),
            background = XWhite,
        )
        XSkeletonAttachmentCard(
            lines = listOf(0.72f, 0.44f, 0.62f),
            background = XWhite,
        )
    }
}

@Composable
private fun XComposeReplyAttachmentStrip(replyTarget: XResolvedPost?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        XSquareAttachmentButton {
            Icon(
                imageVector = ZionAppIcons.Camera,
                contentDescription = null,
                tint = XBlue,
                modifier = Modifier.size(28.dp)
            )
        }
        XSnippetAttachmentCard(
            title = replyTarget?.author?.displayName ?: "Robert Scoble",
            body = replyTarget?.post?.body ?: "Did someone turn the speed knob up on @Grok?",
        )
        XSnippetAttachmentCard(
            title = "leo 🐾",
            body = "The \$200/mo ChatGPT Pro plan will soon...",
        )
        XCodeAttachmentCard()
    }
}

@Composable
private fun XSquareAttachmentButton(
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, XThread, RoundedCornerShape(16.dp))
            .background(XWhite)
            .pressableScale(pressedScale = 0.98f, onClick = {}),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
private fun XImageAttachmentCard(
    imageUrl: String,
    background: Color,
) {
    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, XThread, RoundedCornerShape(16.dp))
            .background(background)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun XSkeletonAttachmentCard(
    lines: List<Float>,
    background: Color,
) {
    Column(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, XThread, RoundedCornerShape(16.dp))
            .background(background)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(Color(0xFFD1D5DB))
        )
        lines.forEach { widthFactor ->
            Spacer(
                modifier = Modifier
                    .fillMaxWidth(widthFactor)
                    .height(6.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFFE5E7EB))
            )
        }
    }
}

@Composable
private fun XSnippetAttachmentCard(
    title: String,
    body: String,
) {
    Column(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, XThread, RoundedCornerShape(16.dp))
            .background(if (title == "leo 🐾") XWhite else Color(0xFFF7F9F9))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            color = XText,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = body,
            color = XSubText,
            fontSize = 7.sp,
            lineHeight = 10.sp,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun XCodeAttachmentCard() {
    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, XThread, RoundedCornerShape(16.dp))
            .background(XCode)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "This code will let you recover your account...\n\nR68645",
            color = Color(0xFF8B949E),
            fontSize = 6.sp,
            lineHeight = 9.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun XComposeToolbar(textLength: Int) {
    val progressColor = when {
        textLength > XMaxChars -> Color(0xFFF4212E)
        textLength > (XMaxChars * 0.8f).toInt() -> XYellow
        textLength > 0 -> XBlue
        else -> XBorder
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(53.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            XToolbarIconButton {
                Icon(
                    imageVector = ZionAppIcons.Image,
                    contentDescription = null,
                    tint = XBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
            XToolbarIconButton {
                Icon(
                    imageVector = ZionAppIcons.Files,
                    contentDescription = null,
                    tint = XBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
            XToolbarIconButton {
                XListIcon(tint = XBlue)
            }
            XToolbarIconButton {
                XLocationIcon(tint = XBlue)
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            XProgressRing(
                progress = (textLength.coerceAtMost(XMaxChars).toFloat() / XMaxChars).coerceIn(0f, 1f),
                color = progressColor,
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(XThread)
            )
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .border(1.dp, XThread, CircleShape)
                    .background(XWhite)
                    .pressableScale(pressedScale = 0.95f, onClick = {}),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ZionAppIcons.Plus,
                    contentDescription = null,
                    tint = XBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
    Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
}

@Composable
private fun XToolbarIconButton(
    icon: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .pressableScale(pressedScale = 0.95f, onClick = {}),
        contentAlignment = Alignment.Center,
        content = icon
    )
}

@Composable
private fun XProgressRing(
    progress: Float,
    color: Color,
) {
    Canvas(modifier = Modifier.size(28.dp)) {
        val stroke = size.minDimension * 0.1f
        drawArc(
            color = XBorder,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = stroke)
        )
        if (progress > 0f) {
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = progress * 360f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
private fun XAvatarButton(
    author: XAuthor?,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .pressableScale(pressedScale = 0.95f, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        XAvatar(author = author, size = 32.dp)
    }
}

@Composable
private fun XAvatar(
    author: XAuthor?,
    size: Dp,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(xColor(author?.avatarColorHex ?: 0xFFCCCCCC, Color(0xFFDDDDDD))),
        contentAlignment = Alignment.Center
    ) {
        when {
            author?.avatarUrl != null -> {
                AsyncImage(
                    model = author.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            author?.handle == "@grok" -> {
                Text(
                    text = "X",
                    color = XWhite,
                    fontSize = (size.value * 0.45f).sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            author != null -> {
                Text(
                    text = author.initials.take(2),
                    color = XWhite,
                    fontSize = (size.value * 0.32f).sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun XTagPill(
    text: String,
    background: Color,
    content: Color,
    paddingHorizontal: Dp = 8.dp,
    paddingVertical: Dp = 4.dp,
    textSize: androidx.compose.ui.unit.TextUnit = 11.sp,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = paddingHorizontal, vertical = paddingVertical)
    ) {
        Text(
            text = text,
            color = content,
            fontSize = textSize,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun XVerifiedBadge(
    size: Dp = 16.dp,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(XBlue),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "✓",
            color = XWhite,
            fontSize = (size.value * 0.55f).sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun XGoldBadge(
    size: Dp = 16.dp,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(4.dp))
            .background(XYellow),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "✓",
            color = XBlack,
            fontSize = (size.value * 0.55f).sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun XMiniXBadge() {
    Text(
        text = "X",
        color = XText,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun XCircleHitTarget(
    size: Dp = 32.dp,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .pressableScale(pressedScale = 0.95f, onClick = onClick),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
private fun XMoreIcon(
    tint: Color,
    modifier: Modifier = Modifier.size(18.dp),
) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension * 0.12f
        val centerY = size.height / 2f
        listOf(0.22f, 0.5f, 0.78f).forEach { fraction ->
            drawCircle(
                color = tint,
                radius = radius,
                center = Offset(size.width * fraction, centerY)
            )
        }
    }
}

@Composable
private fun XReplyIcon(
    tint: Color,
    modifier: Modifier = Modifier.size(18.dp),
) {
    Icon(
        painter = painterResource(R.drawable.ic_x_action_reply),
        contentDescription = null,
        tint = tint,
        modifier = modifier
    )
}

@Composable
private fun XRepostIcon(
    tint: Color,
    modifier: Modifier = Modifier.size(18.dp),
) {
    Icon(
        painter = painterResource(R.drawable.ic_x_action_repost),
        contentDescription = null,
        tint = tint,
        modifier = modifier
    )
}

@Composable
private fun XLikeIcon(
    tint: Color,
    modifier: Modifier = Modifier.size(18.dp),
) {
    Icon(
        painter = painterResource(R.drawable.ic_x_action_like),
        contentDescription = null,
        tint = tint,
        modifier = modifier
    )
}

@Composable
private fun XViewIcon(
    tint: Color,
    modifier: Modifier = Modifier.size(18.dp),
) {
    Icon(
        painter = painterResource(R.drawable.ic_x_action_view),
        contentDescription = null,
        tint = tint,
        modifier = modifier
    )
}

@Composable
private fun XShareIcon(
    tint: Color,
    modifier: Modifier = Modifier.size(18.dp),
) {
    Icon(
        painter = painterResource(R.drawable.ic_x_action_share),
        contentDescription = null,
        tint = tint,
        modifier = modifier
    )
}

@Composable
private fun XHomeIcon(
    tint: Color,
    filled: Boolean,
    modifier: Modifier = Modifier.size(24.dp),
) {
    Canvas(modifier = modifier) {
        val stroke = size.minDimension * 0.09f
        val path = Path().apply {
            moveTo(size.width * 0.18f, size.height * 0.48f)
            lineTo(size.width * 0.5f, size.height * 0.2f)
            lineTo(size.width * 0.82f, size.height * 0.48f)
            lineTo(size.width * 0.82f, size.height * 0.8f)
            lineTo(size.width * 0.18f, size.height * 0.8f)
            close()
        }
        if (filled) drawPath(path, tint) else drawPath(path, tint, style = Stroke(width = stroke, join = StrokeJoin.Round))
    }
}

@Composable
private fun XPulseIcon(
    tint: Color,
    modifier: Modifier = Modifier.size(24.dp),
) {
    Canvas(modifier = modifier) {
        val stroke = size.minDimension * 0.1f
        drawLine(tint, Offset(size.width * 0.2f, size.height * 0.8f), Offset(size.width * 0.8f, size.height * 0.2f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
private fun XBellIcon(
    tint: Color,
    modifier: Modifier = Modifier.size(24.dp),
) {
    Canvas(modifier = modifier) {
        val stroke = size.minDimension * 0.09f
        drawArc(
            color = tint,
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(size.width * 0.18f, size.height * 0.14f),
            size = Size(size.width * 0.64f, size.height * 0.56f),
            style = Stroke(width = stroke)
        )
        drawLine(tint, Offset(size.width * 0.28f, size.height * 0.66f), Offset(size.width * 0.72f, size.height * 0.66f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawCircle(tint, radius = size.minDimension * 0.05f, center = Offset(size.width * 0.5f, size.height * 0.76f))
    }
}

@Composable
private fun XMailIcon(
    tint: Color,
    modifier: Modifier = Modifier.size(24.dp),
) {
    Canvas(modifier = modifier) {
        val stroke = size.minDimension * 0.09f
        drawRoundRect(
            color = tint,
            topLeft = Offset(size.width * 0.14f, size.height * 0.24f),
            size = Size(size.width * 0.72f, size.height * 0.52f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.minDimension * 0.08f),
            style = Stroke(width = stroke)
        )
        drawLine(tint, Offset(size.width * 0.16f, size.height * 0.28f), Offset(size.width * 0.5f, size.height * 0.54f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(tint, Offset(size.width * 0.84f, size.height * 0.28f), Offset(size.width * 0.5f, size.height * 0.54f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
private fun XCaretDownIcon(
    tint: Color,
    modifier: Modifier = Modifier.size(14.dp),
) {
    Canvas(modifier = modifier) {
        val stroke = size.minDimension * 0.12f
        drawLine(tint, Offset(size.width * 0.22f, size.height * 0.36f), Offset(size.width * 0.5f, size.height * 0.68f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(tint, Offset(size.width * 0.78f, size.height * 0.36f), Offset(size.width * 0.5f, size.height * 0.68f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
private fun XInfoIcon(
    tint: Color,
    modifier: Modifier = Modifier.size(18.dp),
) {
    Canvas(modifier = modifier) {
        val stroke = size.minDimension * 0.09f
        drawCircle(color = tint, radius = size.minDimension * 0.42f, style = Stroke(width = stroke))
        drawLine(tint, Offset(size.width * 0.5f, size.height * 0.42f), Offset(size.width * 0.5f, size.height * 0.68f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawCircle(color = tint, radius = size.minDimension * 0.04f, center = Offset(size.width * 0.5f, size.height * 0.28f))
    }
}

@Composable
private fun XListIcon(
    tint: Color,
    modifier: Modifier = Modifier.size(18.dp),
) {
    Canvas(modifier = modifier) {
        val stroke = size.minDimension * 0.1f
        listOf(0.26f, 0.5f, 0.74f).forEach { fraction ->
            drawLine(tint, Offset(size.width * 0.18f, size.height * fraction), Offset(size.width * 0.82f, size.height * fraction), strokeWidth = stroke, cap = StrokeCap.Round)
        }
    }
}

@Composable
private fun XLocationIcon(
    tint: Color,
    modifier: Modifier = Modifier.size(18.dp),
) {
    Canvas(modifier = modifier) {
        val stroke = size.minDimension * 0.1f
        val path = Path().apply {
            moveTo(size.width * 0.5f, size.height * 0.88f)
            cubicTo(size.width * 0.2f, size.height * 0.58f, size.width * 0.2f, size.height * 0.22f, size.width * 0.5f, size.height * 0.16f)
            cubicTo(size.width * 0.8f, size.height * 0.22f, size.width * 0.8f, size.height * 0.58f, size.width * 0.5f, size.height * 0.88f)
            close()
        }
        drawPath(path, tint, style = Stroke(width = stroke, join = StrokeJoin.Round))
        drawCircle(color = tint, radius = size.minDimension * 0.1f, center = Offset(size.width * 0.5f, size.height * 0.42f), style = Stroke(width = stroke))
    }
}

private fun displayFeedTime(post: XPost): String {
    return post.feedTimeLabel ?: relativeTime(post.createdAtMillis)
}

private fun displayDetailTime(post: XPost): String {
    return post.detailTimeLabel ?: DETAIL_TIME_FORMATTER.format(
        Instant.ofEpochMilli(post.createdAtMillis).atZone(ZoneId.systemDefault())
    )
}

private fun formatMetric(value: Int): String {
    return when {
        value >= 10_000 -> {
            val wanValue = value / 10_000f
            if (wanValue >= 100f || wanValue % 1f == 0f) "${wanValue.toInt()}万" else String.format(Locale.US, "%.1f万", wanValue)
        }

        else -> NumberFormat.getIntegerInstance(Locale.US).format(value)
    }
}

private fun relativeTime(createdAtMillis: Long): String {
    val diff = (System.currentTimeMillis() - createdAtMillis).coerceAtLeast(0L)
    val minutes = diff / 60_000L
    val hours = diff / 3_600_000L
    val days = diff / 86_400_000L
    return when {
        minutes < 1 -> "刚刚"
        minutes < 60 -> "${minutes}分钟"
        hours < 24 -> "${hours}小时"
        else -> "${days}天"
    }
}

private fun xColor(hex: Long, fallback: Color): Color {
    val argb = when {
        hex < 0L -> return fallback
        hex <= 0xFFFFFFL -> hex or 0xFF000000
        else -> hex and 0xFFFFFFFFL
    }
    return runCatching { Color(argb) }.getOrElse { fallback }
}

private val DETAIL_TIME_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yy年M月d日, HH:mm", Locale.CHINA)
