package me.rerere.rikkahub.ui.pages.zphone

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.dokar.sonner.ToastType
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.Add01
import me.rerere.hugeicons.stroke.ArrowLeft01
import me.rerere.hugeicons.stroke.BubbleChatQuestion
import me.rerere.hugeicons.stroke.Cancel01
import me.rerere.hugeicons.stroke.Favourite
import me.rerere.hugeicons.stroke.FavouriteCircle
import me.rerere.hugeicons.stroke.Image03
import me.rerere.hugeicons.stroke.MoreVertical
import me.rerere.hugeicons.stroke.Search01
import me.rerere.hugeicons.stroke.Share04
import me.rerere.rikkahub.R
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalToaster
import me.rerere.rikkahub.utils.getActivity

private enum class XView {
    Feed,
    Detail,
    ComposePost,
    ComposeReply,
}

private data class FeedCard(
    val id: String,
    val author: String,
    val handle: String,
    val time: String,
    val content: String,
    val quoteTitle: String? = null,
    val quoteHandle: String? = null,
    val quoteContent: String? = null,
    val quotePreview: String? = null,
    val replies: String,
    val reposts: String,
    val likes: String,
    val views: String,
)

private val XOuter = Color(0xFF242D34)
private val XText = Color(0xFF0F1419)
private val XSubText = Color(0xFF536471)
private val XBlue = Color(0xFF1D9BF0)
private val XDivider = Color(0xFFEFF3F4)
private val XDanger = Color(0xFFF4212E)
private val XWarn = Color(0xFFFFD400)

@Composable
fun XTimelinePage() {
    val context = LocalContext.current
    val activity = remember(context) { context.getActivity() }
    val toaster = LocalToaster.current

    var currentView by rememberSaveable { mutableStateOf(XView.Feed) }
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    var selectedBottomTab by rememberSaveable { mutableStateOf(0) }
    var firstLiked by rememberSaveable { mutableStateOf(false) }
    var firstSaved by rememberSaveable { mutableStateOf(false) }
    var composePostText by rememberSaveable { mutableStateOf("") }
    var composeReplyText by rememberSaveable { mutableStateOf("") }

    val feedCards = remember {
        listOf(
            FeedCard(
                id = "elon",
                author = "Elon Musk",
                handle = "@elonmusk",
                time = "2小时",
                content = "Grok gets faster & smarter every week",
                quoteTitle = "Robert Scoble",
                quoteHandle = "@Scobleizer",
                quoteContent = "Did someone turn the speed knob up on @Grok?\n\nJust did another transcript reading and, damn, it is way faster than last ...",
                replies = "1,711",
                reposts = "1,141",
                likes = "1万",
                views = "568万"
            ),
            FeedCard(
                id = "leo",
                author = "leo 🐾",
                handle = "@synthwavedd",
                time = "1小时",
                content = "The $200/mo ChatGPT Pro plan will soon no longer be unlimited\n\nI think we all knew this was coming, but still 👎",
                quoteTitle = "Chetaslua",
                quoteHandle = "@chetaslua",
                quoteContent = "🚨 ChatGPT 100$ plan is coming soon\n\nOne bad news - now the 200$ plan is not truly unlimited (it's 20 times the plus uses)...",
                quotePreview = "订阅方案预览",
                replies = "682",
                reposts = "209",
                likes = "3,402",
                views = "112万"
            )
        )
    }

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

    BackHandler(enabled = currentView != XView.Feed) {
        currentView = when (currentView) {
            XView.Detail -> XView.Feed
            XView.ComposePost -> XView.Feed
            XView.ComposeReply -> XView.Detail
            XView.Feed -> XView.Feed
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
            targetValue = if (currentView == XView.Detail || currentView == XView.ComposeReply) -pageWidth else 0.dp,
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
            label = "x_feed_offset"
        )
        val detailOffsetX by animateDpAsState(
            targetValue = if (currentView == XView.Detail || currentView == XView.ComposeReply) 0.dp else pageWidth,
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
            label = "x_detail_offset"
        )
        val composePostOffsetY by animateDpAsState(
            targetValue = if (currentView == XView.ComposePost) 0.dp else pageHeight,
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
            label = "x_post_offset"
        )
        val composeReplyOffsetY by animateDpAsState(
            targetValue = if (currentView == XView.ComposeReply) 0.dp else pageHeight,
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
            label = "x_reply_offset"
        )

        XFeedView(
            cards = feedCards,
            selectedTab = selectedTab,
            selectedBottomTab = selectedBottomTab,
            firstLiked = firstLiked,
            firstSaved = firstSaved,
            onSelectTab = { selectedTab = it },
            onSelectBottomTab = { selectedBottomTab = it },
            onToggleFirstLike = { firstLiked = !firstLiked },
            onToggleFirstSave = { firstSaved = !firstSaved },
            onOpenDetail = { currentView = XView.Detail },
            onOpenCompose = { currentView = XView.ComposePost },
            modifier = Modifier
                .fillMaxSize()
                .offset(x = feedOffsetX)
                .zIndex(0f)
        )

        XDetailView(
            liked = firstLiked,
            saved = firstSaved,
            onBack = { currentView = XView.Feed },
            onToggleLike = { firstLiked = !firstLiked },
            onToggleSave = { firstSaved = !firstSaved },
            onReply = { currentView = XView.ComposeReply },
            modifier = Modifier
                .fillMaxSize()
                .offset(x = detailOffsetX)
                .zIndex(1f)
        )

        XComposePostView(
            text = composePostText,
            onTextChange = { composePostText = it },
            onClose = { currentView = XView.Feed },
            onSubmit = {
                toaster.show("帖子草稿已保存", type = ToastType.Success)
                composePostText = ""
                currentView = XView.Feed
            },
            modifier = Modifier
                .fillMaxSize()
                .offset(y = composePostOffsetY)
                .zIndex(2f)
        )

        XComposeReplyView(
            text = composeReplyText,
            onTextChange = { composeReplyText = it },
            onClose = { currentView = XView.Detail },
            onSubmit = {
                toaster.show("回复已保存", type = ToastType.Success)
                composeReplyText = ""
                currentView = XView.Detail
            },
            modifier = Modifier
                .fillMaxSize()
                .offset(y = composeReplyOffsetY)
                .zIndex(3f)
        )
    }
}

@Composable
private fun XFeedView(
    cards: List<FeedCard>,
    selectedTab: Int,
    selectedBottomTab: Int,
    firstLiked: Boolean,
    firstSaved: Boolean,
    onSelectTab: (Int) -> Unit,
    onSelectBottomTab: (Int) -> Unit,
    onToggleFirstLike: () -> Unit,
    onToggleFirstSave: () -> Unit,
    onOpenDetail: () -> Unit,
    onOpenCompose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.displayCutout)
        ) {
            XFeedHeader()
            XFeedTabs(selectedTab = selectedTab, onSelect = onSelectTab)
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 92.dp)
                ) {
                    items(cards) { card ->
                        XFeedCard(
                            card = card,
                            liked = if (card.id == "elon") firstLiked else false,
                            saved = if (card.id == "elon") firstSaved else false,
                            onToggleLike = if (card.id == "elon") onToggleFirstLike else ({}),
                            onToggleSave = if (card.id == "elon") onToggleFirstSave else ({}),
                            onOpen = if (card.id == "elon") onOpenDetail else ({})
                        )
                    }
                }
                XComposeFab(
                    onClick = onOpenCompose,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 88.dp)
                )
            }
            XBottomNav(selected = selectedBottomTab, onSelect = onSelectBottomTab)
        }
    }
}

@Composable
private fun XDetailView(
    liked: Boolean,
    saved: Boolean,
    onBack: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleSave: () -> Unit,
    onReply: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = Color.White,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.displayCutout)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(53.dp)
                        .padding(start = 8.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircleIconButton(onClick = onBack) {
                        Icon(HugeIcons.ArrowLeft01, contentDescription = null, tint = XText, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = "发帖",
                        color = XText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 76.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AvatarBubble(label = "RS", background = Color(0xFF111111), modifier = Modifier.size(40.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Robert Scoble", color = XText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        VerifiedBadge()
                                    }
                                    Text("@Scobleizer", color = XSubText, fontSize = 15.sp)
                                }
                            }
                            Surface(
                                color = XText,
                                shape = CircleShape,
                                modifier = Modifier.pressableScale(pressedScale = 0.96f, onClick = {})
                            ) {
                                Text(
                                    text = "订阅",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }

                        Text(
                            text = "Did someone turn the speed knob up on @Grok?\n\nJust did another transcript reading and, damn, it is way faster than last time I asked it to do the same.",
                            color = XText,
                            fontSize = 17.sp,
                            lineHeight = 24.sp,
                            modifier = Modifier.padding(top = 14.dp)
                        )

                        Text(
                            text = "26年3月28日, 13:35 · 571万 查看",
                            color = XSubText,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(top = 14.dp)
                        )
                    }

                    XDividerLine()
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Text("15 转帖", color = XText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text("302 喜欢", color = XText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text("19 书签", color = XText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                    XDividerLine()
                    TweetActionRow(
                        replies = "15",
                        reposts = "302",
                        likes = if (liked) "303" else "302",
                        views = "571万",
                        liked = liked,
                        saved = saved,
                        onToggleLike = onToggleLike,
                        onToggleSave = onToggleSave,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                    XDividerLine()
                    Text(
                        text = "最相关的回复",
                        color = XText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                    XDividerLine()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            AvatarBubble(label = "G", background = Color.Black, modifier = Modifier.size(40.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Grok", color = XText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Surface(
                                        color = Color(0xFFFFD700),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("𝕏I", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("@grok · 7小时", color = XSubText, fontSize = 14.sp)
                                }
                                Text(
                                    text = "回复给 @Scobleizer\n\nThanks! We've been optimizing hard at xAI—speed improvements like this are rolling out fast. Glad the transcript readings feel snappier.",
                                    color = XText,
                                    fontSize = 15.sp,
                                    lineHeight = 21.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = Color.White,
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AvatarBubble(label = "ME", background = Color(0xFF6C8CD5), modifier = Modifier.size(32.dp))
                    Surface(
                        color = Color(0xFFF4F7F9),
                        shape = CircleShape,
                        modifier = Modifier
                            .weight(1f)
                            .pressableScale(pressedScale = 0.98f, onClick = onReply)
                    ) {
                        Text(
                            text = "发布你的回复",
                            color = XSubText,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun XComposePostView(
    text: String,
    onTextChange: (String) -> Unit,
    onClose: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    XComposerScaffold(
        modifier = modifier,
        title = "草稿",
        actionText = "发帖",
        placeholder = "有什么新鲜事？",
        text = text,
        onTextChange = onTextChange,
        onClose = onClose,
        onSubmit = onSubmit,
        showReplyContext = false,
    ) {
        AudienceChip()
    }
}

@Composable
private fun XComposeReplyView(
    text: String,
    onTextChange: (String) -> Unit,
    onClose: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    XComposerScaffold(
        modifier = modifier,
        title = null,
        actionText = "回复",
        placeholder = "发布你的回复",
        text = text,
        onTextChange = onTextChange,
        onClose = onClose,
        onSubmit = onSubmit,
        showReplyContext = true,
    ) {
        Row(
            modifier = Modifier.padding(top = 2.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(40.dp)
            ) {
                AvatarBubble(label = "RS", background = Color(0xFF111111), modifier = Modifier.size(40.dp))
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .width(2.dp)
                        .height(108.dp)
                        .background(Color(0xFFCFD9DE), CircleShape)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Robert Scoble", color = XText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    VerifiedBadge()
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("@Scobleizer · 8小时", color = XSubText, fontSize = 14.sp)
                }
                Text(
                    text = "Did someone turn the speed knob up on @Grok?\n\nJust did another transcript reading and, damn, it is way faster than last time I asked it to do the same.",
                    color = XText,
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                )
                Text(
                    text = "回复给 @Scobleizer 和 @grok",
                    color = XBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun XComposerScaffold(
    title: String?,
    actionText: String,
    placeholder: String,
    text: String,
    onTextChange: (String) -> Unit,
    onClose: () -> Unit,
    onSubmit: () -> Unit,
    showReplyContext: Boolean,
    modifier: Modifier = Modifier,
    contentTop: @Composable ColumnScope.() -> Unit,
) {
    val overLimit = text.length > 280
    val canSubmit = text.isNotBlank() && !overLimit
    val progress = (text.length.coerceAtMost(280) / 280f)
    val ringColor = when {
        overLimit -> XDanger
        progress > 0.8f -> XWarn
        text.isNotBlank() -> XBlue
        else -> XDivider
    }

    Surface(
        modifier = modifier,
        color = Color.White,
        shadowElevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.displayCutout)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(53.dp)
                    .padding(start = 8.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CircleIconButton(onClick = onClose) {
                    Icon(HugeIcons.Cancel01, contentDescription = null, tint = XText, modifier = Modifier.size(18.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (title != null) {
                        Text(title, color = XBlue, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    SubmitButton(text = actionText, enabled = canSubmit, onClick = onSubmit)
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp)
            ) {
                contentTop()
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                    AvatarBubble(label = "ME", background = Color(0xFF6C8CD5), modifier = Modifier.size(40.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        if (!showReplyContext) {
                            Text(
                                text = "每个人",
                                color = XBlue,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .border(1.dp, Color(0xFFCFD9DE), CircleShape)
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                        ComposerTextField(
                            value = text,
                            onValueChange = onTextChange,
                            placeholder = placeholder,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        )
                    }
                }
                ComposerMediaRow(modifier = Modifier.padding(top = 18.dp))
                if (!showReplyContext) {
                    ReplyPermissionRow(modifier = Modifier.padding(top = 12.dp))
                }
                Spacer(modifier = Modifier.height(120.dp))
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                ComposerToolbar(
                    progress = progress,
                    ringColor = ringColor,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun XFeedHeader() {
    Surface(
        color = Color.White.copy(alpha = 0.96f),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(53.dp)
                .padding(start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AvatarBubble(
                label = "ME",
                background = Color(0xFFD9E7F4),
                textColor = XText,
                modifier = Modifier.size(32.dp)
            )
            Image(
                painter = painterResource(R.drawable.zphone_x_logo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(24.dp)
            )
            CircleIconButton(onClick = {}) {
                Icon(HugeIcons.MoreVertical, contentDescription = null, tint = XSubText, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun XFeedTabs(selectedTab: Int, onSelect: (Int) -> Unit) {
    val tabs = listOf("为你推荐", "正在关注", "AI — Rumors & Insights")
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            tabs.forEachIndexed { index, title ->
                val tabWidth = if (index == 2) 188.dp else 118.dp
                Box(
                    modifier = Modifier
                        .width(tabWidth)
                        .height(53.dp)
                        .clickable { onSelect(index) }
                ) {
                    Text(
                        text = title,
                        color = if (selectedTab == index) XText else XSubText,
                        fontSize = 15.sp,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .width(56.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(if (selectedTab == index) XBlue else Color.Transparent)
                    )
                }
            }
        }
        XDividerLine()
    }
}

@Composable
private fun XFeedCard(
    card: FeedCard,
    liked: Boolean,
    saved: Boolean,
    onToggleLike: () -> Unit,
    onToggleSave: () -> Unit,
    onOpen: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable(enabled = card.id == "elon", onClick = onOpen)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            AvatarBubble(
                label = if (card.id == "elon") "EM" else "LE",
                background = if (card.id == "elon") Color(0xFF111111) else Color(0xFF8B936E),
                modifier = Modifier.size(40.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(card.author, color = XText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            VerifiedBadge()
                            if (card.id == "elon") {
                                Spacer(modifier = Modifier.width(4.dp))
                                Image(
                                    painter = painterResource(R.drawable.zphone_x_logo),
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${card.handle} · ${card.time}",
                                color = XSubText,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    CircleIconButton(onClick = {}) {
                        Icon(HugeIcons.MoreVertical, contentDescription = null, tint = XSubText, modifier = Modifier.size(18.dp))
                    }
                }
                Text(
                    text = card.content,
                    color = XText,
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (card.quoteTitle != null && card.quoteContent != null) {
                    QuoteCard(
                        title = card.quoteTitle,
                        handle = card.quoteHandle.orEmpty(),
                        content = card.quoteContent,
                        preview = card.quotePreview,
                        modifier = Modifier.padding(top = 14.dp)
                    )
                }
                TweetActionRow(
                    replies = card.replies,
                    reposts = card.reposts,
                    likes = if (card.id == "elon" && liked) "1万+" else card.likes,
                    views = card.views,
                    liked = liked,
                    saved = saved,
                    onToggleLike = onToggleLike,
                    onToggleSave = onToggleSave,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
        XDividerLine()
    }
}

@Composable
private fun QuoteCard(
    title: String,
    handle: String,
    content: String,
    preview: String?,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .border(1.dp, XDivider, RoundedCornerShape(20.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarBubble(label = title.take(1), background = Color(0xFF1C2128), modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, color = XText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                VerifiedBadge(size = 14.dp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(handle, color = XSubText, fontSize = 14.sp)
            }
            Text(
                text = content,
                color = XText,
                fontSize = 15.sp,
                lineHeight = 21.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            if (preview != null) {
                if (preview == "订阅方案预览") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .padding(top = 12.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFFBDE3F3))
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .height(112.dp)
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                .background(Color.White)
                                .border(1.dp, XDivider, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                                Box(
                                    modifier = Modifier
                                        .width(96.dp)
                                        .height(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE1E8EC))
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier
                                        .width(128.dp)
                                        .height(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE7EDF0))
                                )
                            }
                        }
                    }
                } else {
                    Surface(
                        color = Color(0xFFF4F7F9),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(top = 12.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(preview, color = XSubText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TweetActionRow(
    replies: String,
    reposts: String,
    likes: String,
    views: String? = null,
    liked: Boolean,
    saved: Boolean,
    onToggleLike: () -> Unit,
    onToggleSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatAction(label = replies, onClick = {}) { tint ->
            Icon(HugeIcons.BubbleChatQuestion, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        }
        StatAction(label = reposts, activeColor = Color(0xFF00BA7C), onClick = {}) { tint ->
            Icon(HugeIcons.Share04, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        }
        StatAction(label = likes, active = liked, activeColor = Color(0xFFF91880), onClick = onToggleLike) { tint ->
            Icon(HugeIcons.FavouriteCircle, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        }
        if (views != null) {
            StatAction(label = views, onClick = {}) { tint ->
                ViewsGlyph(color = tint, modifier = Modifier.size(18.dp))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            ActionGlyphButton(active = saved, activeColor = XBlue, onClick = onToggleSave) { tint ->
                Icon(HugeIcons.Favourite, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            }
            ActionGlyphButton(active = false, activeColor = XBlue, onClick = {}) { tint ->
                Icon(HugeIcons.Share04, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun StatAction(
    label: String,
    active: Boolean = false,
    activeColor: Color = XBlue,
    onClick: () -> Unit,
    icon: @Composable (Color) -> Unit,
) {
    val tint = if (active) activeColor else XSubText
    Row(
        modifier = Modifier.pressableScale(pressedScale = 0.95f, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        icon(tint)
        Text(label, color = tint, fontSize = 13.sp)
    }
}

@Composable
private fun ActionGlyphButton(
    active: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    content: @Composable (Color) -> Unit,
) {
    val tint = if (active) activeColor else XSubText
    Box(
        modifier = Modifier.pressableScale(pressedScale = 0.94f, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content(tint)
    }
}

@Composable
private fun XComposeFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.pressableScale(pressedScale = 0.94f, onClick = onClick),
        color = XBlue,
        shape = CircleShape,
        shadowElevation = 10.dp
    ) {
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(HugeIcons.Add01, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun XBottomNav(selected: Int, onSelect: (Int) -> Unit) {
    Surface(color = Color.White, shadowElevation = 6.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(onClick = { onSelect(0) }) {
                HomeGlyph(color = if (selected == 0) XText else XSubText, modifier = Modifier.size(22.dp))
            }
            BottomNavItem(onClick = { onSelect(1) }) {
                Icon(HugeIcons.Search01, contentDescription = null, tint = if (selected == 1) XText else XSubText, modifier = Modifier.size(22.dp))
            }
            BottomNavItem(onClick = { onSelect(2) }) {
                SlashGlyph(color = if (selected == 2) XText else XSubText, modifier = Modifier.size(22.dp))
            }
            BottomNavItem(onClick = { onSelect(3) }) {
                BellGlyph(color = if (selected == 3) XText else XSubText, modifier = Modifier.size(22.dp))
            }
            BottomNavItem(onClick = { onSelect(4) }) {
                MailGlyph(color = if (selected == 4) XText else XSubText, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
private fun BottomNavItem(onClick: () -> Unit, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 48.dp, height = 36.dp)
            .pressableScale(pressedScale = 0.94f, onClick = onClick),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
private fun AvatarBubble(
    label: String,
    background: Color,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(background)
            .border(1.dp, Color.White.copy(alpha = 0.24f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = if (label.length <= 2) 13.sp else 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun VerifiedBadge(size: androidx.compose.ui.unit.Dp = 16.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(XBlue),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "✓",
            color = Color.White,
            fontSize = (size.value * 0.6f).sp,
            fontWeight = FontWeight.Bold,
            lineHeight = (size.value * 0.6f).sp
        )
    }
}

@Composable
private fun XDividerLine(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(XDivider)
    )
}

@Composable
private fun CircleIconButton(onClick: () -> Unit, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .pressableScale(pressedScale = 0.94f, onClick = onClick),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
private fun SubmitButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (enabled) XBlue else Color(0xFF8ECDF8),
        shape = CircleShape,
        modifier = Modifier.pressableScale(pressedScale = if (enabled) 0.97f else 1f, onClick = { if (enabled) onClick() })
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun AudienceChip() {
    Text(
        text = "每个人",
        color = XBlue,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .border(1.dp, Color(0xFFCFD9DE), CircleShape)
            .padding(horizontal = 12.dp, vertical = 5.dp)
    )
}

@Composable
private fun ComposerTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = {
            if (it.length <= 400) onValueChange(it)
        },
        modifier = modifier,
        textStyle = TextStyle(
            color = XText,
            fontSize = 22.sp,
            lineHeight = 27.sp,
            fontWeight = FontWeight.Normal
        ),
        minLines = 6,
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = XSubText,
                        fontSize = 22.sp,
                        lineHeight = 27.sp
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun ComposerMediaRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ComposerMediaTile(accent = Color.White, title = "添加图片") {
            Icon(HugeIcons.Image03, contentDescription = null, tint = XBlue, modifier = Modifier.size(28.dp))
        }
        ComposerMediaTile(accent = Color(0xFFE6DBCC), title = "Dog Meme") {
            Text(
                text = "Dog Meme",
                color = XSubText,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
        ComposerMediaTile(accent = Color.White, title = "草稿卡片") {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(Color(0xFFD8E0E5)))
                Box(modifier = Modifier.width(50.dp).height(6.dp).clip(CircleShape).background(Color(0xFFDDE5E9)))
                Box(modifier = Modifier.width(68.dp).height(6.dp).clip(CircleShape).background(Color(0xFFE7EDF0)))
                Box(modifier = Modifier.width(58.dp).height(6.dp).clip(CircleShape).background(Color(0xFFE7EDF0)))
            }
        }
        ComposerMediaTile(accent = Color(0xFF1C2128), title = "代码") {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("This code will let", color = Color(0xFF8B949E), fontSize = 6.sp)
                Text("you recover...", color = Color(0xFF8B949E), fontSize = 6.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("R68645", color = XBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ComposerMediaTile(
    accent: Color,
    title: String,
    content: @Composable BoxScope.() -> Unit,
) {
    Surface(
        color = accent,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.size(width = 88.dp, height = 88.dp),
        shadowElevation = 1.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color(0xFFCFD9DE), RoundedCornerShape(18.dp))
                .padding(10.dp),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

@Composable
private fun ReplyPermissionRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(XBlue),
            contentAlignment = Alignment.Center
        ) {
            Text("i", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Text("所有人可以回复", color = XBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ComposerToolbar(progress: Float, ringColor: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier.background(Color.White)) {
        XDividerLine()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                ToolbarIcon {
                    Icon(HugeIcons.Image03, contentDescription = null, tint = XBlue, modifier = Modifier.size(18.dp))
                }
                ToolbarIcon {
                    PollGlyph(color = XBlue, modifier = Modifier.size(18.dp))
                }
                ToolbarIcon {
                    ComposerLinesGlyph(color = XBlue, modifier = Modifier.size(18.dp))
                }
                ToolbarIcon {
                    PinGlyph(color = XBlue, modifier = Modifier.size(18.dp))
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ProgressRing(progress = progress, color = ringColor)
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFCFD9DE)))
                Surface(
                    color = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.border(1.dp, Color(0xFFCFD9DE), CircleShape)
                ) {
                    Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                        Icon(HugeIcons.Add01, contentDescription = null, tint = XBlue, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolbarIcon(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .pressableScale(pressedScale = 0.94f, onClick = {}),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
private fun HomeGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = 2.dp.toPx()
        drawLine(color, Offset(size.width * 0.18f, size.height * 0.48f), Offset(size.width * 0.5f, size.height * 0.18f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.82f, size.height * 0.48f), Offset(size.width * 0.5f, size.height * 0.18f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.24f, size.height * 0.48f), Offset(size.width * 0.24f, size.height * 0.82f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.76f, size.height * 0.48f), Offset(size.width * 0.76f, size.height * 0.82f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.24f, size.height * 0.82f), Offset(size.width * 0.76f, size.height * 0.82f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
private fun SlashGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawLine(
            color = color,
            start = Offset(size.width * 0.72f, size.height * 0.14f),
            end = Offset(size.width * 0.28f, size.height * 0.86f),
            strokeWidth = 2.6.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun BellGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = 2.dp.toPx()
        drawArc(
            color = color,
            startAngle = 205f,
            sweepAngle = 130f,
            useCenter = false,
            topLeft = Offset(size.width * 0.2f, size.height * 0.2f),
            size = Size(size.width * 0.6f, size.height * 0.55f),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
        drawLine(color, Offset(size.width * 0.27f, size.height * 0.52f), Offset(size.width * 0.2f, size.height * 0.74f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.73f, size.height * 0.52f), Offset(size.width * 0.8f, size.height * 0.74f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.2f, size.height * 0.74f), Offset(size.width * 0.8f, size.height * 0.74f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawCircle(color, radius = 1.6.dp.toPx(), center = Offset(size.width * 0.5f, size.height * 0.82f))
    }
}

@Composable
private fun MailGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = 2.dp.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.14f, size.height * 0.24f),
            size = Size(size.width * 0.72f, size.height * 0.52f),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = Stroke(width = stroke)
        )
        drawLine(color, Offset(size.width * 0.16f, size.height * 0.28f), Offset(size.width * 0.5f, size.height * 0.52f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.84f, size.height * 0.28f), Offset(size.width * 0.5f, size.height * 0.52f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
private fun ViewsGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val barWidth = size.width * 0.14f
        val gap = size.width * 0.07f
        val left = size.width * 0.14f
        val heights = listOf(0.44f, 0.7f, 0.56f, 0.84f)
        heights.forEachIndexed { index, factor ->
            val x = left + index * (barWidth + gap)
            val top = size.height * (1f - factor)
            drawRoundRect(
                color = color,
                topLeft = Offset(x, top),
                size = Size(barWidth, size.height * factor),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}

@Composable
private fun PollGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = 2.dp.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.16f, size.height * 0.16f),
            size = Size(size.width * 0.68f, size.height * 0.68f),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = Stroke(width = stroke)
        )
        drawRoundRect(color, Offset(size.width * 0.26f, size.height * 0.55f), Size(size.width * 0.1f, size.height * 0.14f), CornerRadius(2.dp.toPx(), 2.dp.toPx()))
        drawRoundRect(color, Offset(size.width * 0.45f, size.height * 0.42f), Size(size.width * 0.1f, size.height * 0.27f), CornerRadius(2.dp.toPx(), 2.dp.toPx()))
        drawRoundRect(color, Offset(size.width * 0.64f, size.height * 0.3f), Size(size.width * 0.1f, size.height * 0.39f), CornerRadius(2.dp.toPx(), 2.dp.toPx()))
    }
}

@Composable
private fun ComposerLinesGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = 2.dp.toPx()
        listOf(0.24f, 0.5f, 0.76f).forEach { y ->
            drawLine(
                color = color,
                start = Offset(size.width * 0.18f, size.height * y),
                end = Offset(size.width * 0.82f, size.height * y),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun PinGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = 2.dp.toPx()
        drawCircle(
            color = color,
            radius = size.width * 0.18f,
            center = Offset(size.width * 0.5f, size.height * 0.34f),
            style = Stroke(width = stroke)
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.5f, size.height * 0.52f),
            end = Offset(size.width * 0.5f, size.height * 0.84f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ProgressRing(progress: Float, color: Color) {
    Canvas(modifier = Modifier.size(26.dp)) {
        drawArc(
            color = XDivider,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
        if (progress > 0f) {
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}
