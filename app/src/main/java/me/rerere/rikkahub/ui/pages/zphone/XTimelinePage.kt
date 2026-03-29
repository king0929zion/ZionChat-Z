package me.rerere.rikkahub.ui.pages.zphone

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dokar.sonner.ToastType
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.datastore.Settings
import me.rerere.rikkahub.data.db.entity.XPostEntity
import me.rerere.rikkahub.ui.context.LocalToaster
import org.koin.androidx.compose.koinViewModel

private enum class XLayer {
    Feed,
    Detail,
    ComposePost,
    ComposeReply,
}

@Composable
fun XTimelinePage(vm: XTimelineVM = koinViewModel()) {
    val toaster = LocalToaster.current
    val settings by vm.settings.collectAsStateWithLifecycle()
    val feed by vm.feed.collectAsStateWithLifecycle()
    val selectedPostId by vm.selectedPostId.collectAsStateWithLifecycle()
    val selectedPost by vm.selectedPost.collectAsStateWithLifecycle()
    val replies by vm.replies.collectAsStateWithLifecycle()
    val submitting by vm.submitting.collectAsStateWithLifecycle()

    var currentLayer by rememberSaveable { mutableStateOf(XLayer.Feed) }
    var selectedTopTab by rememberSaveable { mutableIntStateOf(0) }
    var selectedBottomTab by rememberSaveable { mutableIntStateOf(0) }
    var composePostText by rememberSaveable { mutableStateOf("") }
    var composeReplyText by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        vm.bootstrap()
        vm.events.collect { event ->
            when (event) {
                is XTimelineEvent.Error -> toaster.show(event.message, type = ToastType.Info)
                is XTimelineEvent.PostPublished -> {
                    composePostText = ""
                    currentLayer = XLayer.Detail
                }
                XTimelineEvent.ReplyPublished -> {
                    composeReplyText = ""
                    currentLayer = XLayer.Detail
                }
            }
        }
    }

    LaunchedEffect(selectedPostId, currentLayer) {
        if (selectedPostId == null && currentLayer == XLayer.Detail) {
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
            1 -> feed.filterNot { it.authorHandle == "@you" }
            2 -> feed.filter {
                it.authorHandle.lowercase() in setOf("@elonmusk", "@grok", "@scobleizer", "@synthwavedd")
            }
            else -> feed
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(XBackground)) {
        val pageWidth = maxWidth
        val pageHeight = maxHeight

        val feedOffsetX by animateDpAsState(
            targetValue = if (currentLayer == XLayer.Detail || currentLayer == XLayer.ComposeReply) -pageWidth else 0.dp,
            animationSpec = tween(250, easing = FastOutSlowInEasing),
            label = "feed_offset"
        )
        val detailOffsetX by animateDpAsState(
            targetValue = if (currentLayer == XLayer.Detail || currentLayer == XLayer.ComposeReply) 0.dp else pageWidth,
            animationSpec = tween(250, easing = FastOutSlowInEasing),
            label = "detail_offset"
        )
        val composePostOffsetY by animateDpAsState(
            targetValue = if (currentLayer == XLayer.ComposePost) 0.dp else pageHeight,
            animationSpec = tween(250, easing = FastOutSlowInEasing),
            label = "compose_post_offset"
        )
        val composeReplyOffsetY by animateDpAsState(
            targetValue = if (currentLayer == XLayer.ComposeReply) 0.dp else pageHeight,
            animationSpec = tween(250, easing = FastOutSlowInEasing),
            label = "compose_reply_offset"
        )

        XFeedLayer(
            settings = settings,
            posts = filteredFeed,
            selectedTopTab = selectedTopTab,
            selectedBottomTab = selectedBottomTab,
            modifier = Modifier.offset(x = feedOffsetX),
            onTopTabChange = { selectedTopTab = it },
            onBottomTabChange = { selectedBottomTab = it },
            onOpenPost = {
                vm.openPost(it)
                currentLayer = XLayer.Detail
            },
            onCompose = { currentLayer = XLayer.ComposePost },
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
            onComposeReply = { currentLayer = XLayer.ComposeReply },
            onLike = vm::toggleLike,
            onRepost = vm::toggleRepost,
            onBookmark = vm::toggleBookmark,
        )

        ComposePostLayer(
            settings = settings,
            text = composePostText,
            posting = submitting,
            modifier = Modifier.offset(y = composePostOffsetY),
            onClose = { currentLayer = XLayer.Feed },
            onTextChange = { composePostText = it },
            onPost = { vm.submitPost(composePostText) },
        )

        ComposeReplyLayer(
            settings = settings,
            post = selectedPost,
            text = composeReplyText,
            posting = submitting,
            modifier = Modifier.offset(y = composeReplyOffsetY),
            onClose = { currentLayer = XLayer.Detail },
            onTextChange = { composeReplyText = it },
            onReply = { vm.submitReply(composeReplyText) },
        )
    }
}

@Composable
private fun XFeedLayer(
    settings: Settings,
    posts: List<XPostEntity>,
    selectedTopTab: Int,
    selectedBottomTab: Int,
    modifier: Modifier = Modifier,
    onTopTabChange: (Int) -> Unit,
    onBottomTabChange: (Int) -> Unit,
    onOpenPost: (String) -> Unit,
    onCompose: () -> Unit,
    onLike: (String) -> Unit,
    onRepost: (String) -> Unit,
    onBookmark: (String) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize().background(XSurface)) {
        Column(modifier = Modifier.fillMaxSize()) {
            FeedHeader(settings = settings)
            FeedTopTabs(selectedIndex = selectedTopTab, onSelect = onTopTabChange)
            LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(bottom = 124.dp)) {
                items(items = posts, key = { it.id }) { post ->
                    XPostCard(
                        settings = settings,
                        post = post,
                        onClick = { onOpenPost(post.id) },
                        onLike = { onLike(post.id) },
                        onRepost = { onRepost(post.id) },
                        onBookmark = { onBookmark(post.id) },
                    )
                }
            }
        }

        FloatingComposeButton(modifier = Modifier.align(Alignment.BottomEnd), onClick = onCompose)
        XBottomNavBar(selectedIndex = selectedBottomTab, modifier = Modifier.align(Alignment.BottomCenter), onSelect = onBottomTabChange)
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
    onLike: (String) -> Unit,
    onRepost: (String) -> Unit,
    onBookmark: (String) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize().background(XSurface)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).height(53.dp).padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(36.dp).clickable(onClick = onBack), contentAlignment = Alignment.Center) {
                    Icon(imageVector = XCloneIcons.Back, contentDescription = null, tint = XText, modifier = Modifier.size(20.dp))
                }
                Text(text = stringResource(R.string.x_timeline_detail_title), color = XText, fontSize = 20.sp, modifier = Modifier.padding(start = 20.dp))
            }

            if (post == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(R.string.x_timeline_post_missing), color = XSubText, fontSize = 16.sp)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(bottom = 92.dp)) {
                    item("detail_post") {
                        DetailPostContent(
                            settings = settings,
                            post = post,
                            onLike = { onLike(post.id) },
                            onRepost = { onRepost(post.id) },
                            onBookmark = { onBookmark(post.id) },
                        )
                    }
                    item("reply_header") {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = stringResource(R.string.x_timeline_relevant_replies), color = XText, fontSize = 15.sp)
                        }
                        HorizontalDivider(color = XDivider)
                    }
                    items(items = replies, key = { it.id }) { reply ->
                        XPostCard(
                            settings = settings,
                            post = reply,
                            showReplyingHint = true,
                            replyTargetHandle = post.authorHandle,
                            onClick = {},
                            onLike = { onLike(reply.id) },
                            onRepost = { onRepost(reply.id) },
                            onBookmark = { onBookmark(reply.id) },
                        )
                    }
                }
                ReplyBar(settings = settings, onClick = onComposeReply)
            }
        }
    }
}

@Composable
private fun ComposePostLayer(
    settings: Settings,
    text: String,
    posting: Boolean,
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    onTextChange: (String) -> Unit,
    onPost: () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize().background(XSurface).imePadding()) {
        ComposerHeader(
            label = stringResource(R.string.x_timeline_compose_post),
            enabled = text.isNotBlank() && text.length <= ComposerLimit && !posting,
            onClose = onClose,
            onSubmit = onPost,
            trailingHint = stringResource(R.string.x_timeline_draft)
        )
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 14.dp)) {
            ComposerAudienceChip()
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top, modifier = Modifier.padding(top = 12.dp)) {
                CurrentUserAvatar(settings = settings, size = 40.dp)
                ComposerTextField(
                    value = text,
                    placeholder = stringResource(R.string.x_timeline_compose_post_placeholder),
                    modifier = Modifier.weight(1f),
                    onValueChange = onTextChange
                )
            }
        }
        EveryoneCanReplyRow()
        ComposerFooter(textLength = text.length, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun ComposeReplyLayer(
    settings: Settings,
    post: XPostEntity?,
    text: String,
    posting: Boolean,
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    onTextChange: (String) -> Unit,
    onReply: () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize().background(XSurface).imePadding()) {
        ComposerHeader(
            label = stringResource(R.string.x_timeline_compose_reply),
            enabled = text.isNotBlank() && text.length <= ComposerLimit && !posting,
            onClose = onClose,
            onSubmit = onReply
        )
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 16.dp)) {
            post?.let { ReplyContextCard(settings = settings, post = it) }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top, modifier = Modifier.padding(top = 8.dp)) {
                CurrentUserAvatar(settings = settings, size = 40.dp)
                ComposerTextField(
                    value = text,
                    placeholder = stringResource(R.string.x_timeline_reply_placeholder),
                    modifier = Modifier.weight(1f),
                    onValueChange = onTextChange
                )
            }
        }
        ComposerFooter(textLength = text.length, modifier = Modifier.fillMaxWidth(), showReplyInfo = false)
    }
}
