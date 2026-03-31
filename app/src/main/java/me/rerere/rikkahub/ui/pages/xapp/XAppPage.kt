package me.rerere.rikkahub.ui.pages.xapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import me.rerere.rikkahub.R
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.context.LocalToaster
import com.dokar.sonner.ToastType

private val XBlue = Color(0xFF1D9BF0)
private val XBlueLight = Color(0xFF8ECDF8)
private val XTextPrimary = Color(0xFF0F1419)
private val XTextSecondary = Color(0xFF536471)
private val XBorder = Color(0xFFEFF3F4)
private val XGreen = Color(0xFF00BA7C)
private val XPink = Color(0xFFF91880)
private val XRed = Color(0xFFF4212E)
private val XYellow = Color(0xFFFFD400)
private val XBackground = Color(0xFFF7F9F9)

private data class TweetData(
    val avatarUrl: String,
    val name: String,
    val handle: String,
    val time: String,
    val content: String,
    val isVerified: Boolean = true,
    val replies: String = "0",
    val retweets: String = "0",
    val likes: String = "0",
    val views: String = "0",
    val quoteTweet: QuoteTweetData? = null,
)

private data class QuoteTweetData(
    val avatarUrl: String,
    val name: String,
    val handle: String,
    val time: String,
    val content: String,
    val isVerified: Boolean = true,
    val hasImage: Boolean = false,
)

private sealed class XView {
    object Feed : XView()
    object Detail : XView()
    object ComposePost : XView()
    object ComposeReply : XView()
}

@Composable
fun XAppPage() {
    val navController = LocalNavController.current
    val toaster = LocalToaster.current
    var currentView by remember { mutableStateOf<XView>(XView.Feed) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        when (currentView) {
            XView.Feed -> FeedView(
                onNavigate = { currentView = it },
                onBack = { navController.popBackStack() }
            )
            XView.Detail -> DetailView(
                onNavigate = { currentView = it },
                onBack = { currentView = XView.Feed }
            )
            XView.ComposePost -> ComposePostView(
                onBack = { currentView = XView.Feed },
                toaster = toaster
            )
            XView.ComposeReply -> ComposeReplyView(
                onBack = { currentView = XView.Detail },
                toaster = toaster
            )
        }
    }
}

@Composable
private fun FeedView(
    onNavigate: (XView) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("为你推荐", "正在关注", "AI — Rumors & Insights")

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        FeedHeader(onBack = onBack)

        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                TabItem(
                    label = tab,
                    isSelected = selectedTab == index,
                    onClick = { selectedTab = index },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(XBorder)
        )

        // Feed content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            items(getTweets()) { tweet ->
                TweetCard(
                    tweet = tweet,
                    onClick = { onNavigate(XView.Detail) }
                )
            }
        }

        // FAB
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, bottom = 72.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            IconButton(
                onClick = { onNavigate(XView.ComposePost) },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(XBlue)
            ) {
                Icon(
                    imageVector = XIcons.Plus,
                    contentDescription = "Compose",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Bottom nav
        XBottomNav()
    }
}

@Composable
private fun FeedHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile avatar
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFE1E8ED))
        ) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1506748686214-e9df14d4d9d0?auto=format&fit=crop&w=100&q=80",
                contentDescription = "Profile",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // X Logo
        Icon(
            imageVector = XIcons.XLogo,
            contentDescription = "X",
            tint = XTextPrimary,
            modifier = Modifier.size(22.dp)
        )

        // Settings icon
        Icon(
            imageVector = XIcons.Settings,
            contentDescription = "Settings",
            tint = XTextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun TabItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) XTextPrimary else XTextSecondary
            )
            if (isSelected) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(56.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(XBlue)
                )
            }
        }
    }
}

@Composable
private fun TweetCard(
    tweet: TweetData,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row {
            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE1E8ED))
            ) {
                AsyncImage(
                    model = tweet.avatarUrl,
                    contentDescription = tweet.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = tweet.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = XTextPrimary,
                            maxLines = 1
                        )
                        if (tweet.isVerified) {
                            Icon(
                                imageVector = XIcons.Verified,
                                contentDescription = "Verified",
                                tint = XBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = " ${tweet.handle}",
                            fontSize = 15.sp,
                            color = XTextSecondary
                        )
                        Text(
                            text = " · ${tweet.time}",
                            fontSize = 15.sp,
                            color = XTextSecondary
                        )
                    }

                    Icon(
                        imageVector = XIcons.MoreHoriz,
                        contentDescription = "More",
                        tint = XTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = tweet.content,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    color = XTextPrimary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                tweet.quoteTweet?.let { quote ->
                    QuoteTweetCard(quote = quote)
                }

                TweetActions(
                    replies = tweet.replies,
                    retweets = tweet.retweets,
                    likes = tweet.likes,
                    views = tweet.views
                )
            }
        }
    }

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(XBorder)
    )
}

@Composable
private fun QuoteTweetCard(quote: QuoteTweetData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { }
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE1E8ED))
            ) {
                AsyncImage(
                    model = quote.avatarUrl,
                    contentDescription = quote.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = quote.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = XTextPrimary
            )
            if (quote.isVerified) {
                Icon(
                    imageVector = XIcons.Verified,
                    contentDescription = "Verified",
                    tint = XBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = " ${quote.handle}",
                fontSize = 15.sp,
                color = XTextSecondary
            )
            Text(
                text = " · ${quote.time}",
                fontSize = 15.sp,
                color = XTextSecondary
            )
        }

        Text(
            text = quote.content,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            color = XTextPrimary,
            modifier = Modifier.padding(top = 4.dp)
        )

        if (quote.hasImage) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFBDE3F3))
            )
        }
    }
}

@Composable
private fun TweetActions(
    replies: String,
    retweets: String,
    likes: String,
    views: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ActionButton(
            icon = XIcons.Reply,
            count = replies,
            activeColor = XBlue
        )
        ActionButton(
            icon = XIcons.Retweet,
            count = retweets,
            activeColor = XGreen
        )
        ActionButton(
            icon = XIcons.Like,
            count = likes,
            activeColor = XPink
        )
        ActionButton(
            icon = XIcons.Share,
            count = views,
            activeColor = XBlue
        )
        Row {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = XIcons.Bookmark,
                    contentDescription = "Bookmark",
                    tint = XTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    count: String,
    activeColor: Color
) {
    var isActive by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { isActive = !isActive }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isActive) activeColor else XTextSecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = count,
            fontSize = 13.sp,
            color = if (isActive) activeColor else XTextSecondary
        )
    }
}

@Composable
private fun XBottomNav() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        IconButton(onClick = { }) {
            Icon(
                imageVector = XIcons.Home,
                contentDescription = "Home",
                tint = XTextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(onClick = { }) {
            Icon(
                imageVector = XIcons.Search,
                contentDescription = "Search",
                tint = XTextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(onClick = { }) {
            Icon(
                imageVector = XIcons.XLogo,
                contentDescription = "Post",
                tint = XTextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(onClick = { }) {
            Icon(
                imageVector = XIcons.Notifications,
                contentDescription = "Notifications",
                tint = XTextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(onClick = { }) {
            Icon(
                imageVector = XIcons.Messages,
                contentDescription = "Messages",
                tint = XTextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun DetailView(
    onNavigate: (XView) -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = XIcons.ArrowBack,
                    contentDescription = "Back",
                    tint = XTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = "发帖",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = XTextPrimary
            )
        }

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Author info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE1E8ED))
                    ) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1535295972055-1c762f4483e5?auto=format&fit=crop&w=100&q=80",
                            contentDescription = "Scobleizer",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Robert Scoble",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = XTextPrimary
                            )
                            Icon(
                                imageVector = XIcons.Verified,
                                contentDescription = "Verified",
                                tint = XBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "@Scobleizer",
                            fontSize = 15.sp,
                            color = XTextSecondary
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(XTextPrimary)
                        .clickable { }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "订阅",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Tweet content
            Text(
                text = "Did someone turn the speed knob up on @Grok?\n\nJust did another transcript reading and, damn, it is way faster than last time I asked it to do the same.",
                fontSize = 17.sp,
                lineHeight = 24.sp,
                color = XTextPrimary,
                modifier = Modifier.padding(top = 12.dp)
            )

            // Date and views
            Row(
                modifier = Modifier.padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "26年3月28日, 13:35",
                    fontSize = 15.sp,
                    color = XTextSecondary
                )
                Text(
                    text = " · ",
                    fontSize = 15.sp,
                    color = XTextSecondary
                )
                Text(
                    text = "571万",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = XTextPrimary
                )
                Text(
                    text = " 查看",
                    fontSize = 15.sp,
                    color = XTextSecondary
                )
            }

            // Divider
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(XBorder)
                    .padding(vertical = 8.dp)
            )

            // Stats
            Row(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "15 转帖",
                    fontSize = 15.sp,
                    color = XTextSecondary
                )
                Text(
                    text = "302 喜欢",
                    fontSize = 15.sp,
                    color = XTextSecondary
                )
                Text(
                    text = "19 书签",
                    fontSize = 15.sp,
                    color = XTextSecondary
                )
            }

            // Reply header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "最相关的回复",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = XTextPrimary
                )
            }

            // Reply
            Row {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black)
                ) {
                    Icon(
                        imageVector = XIcons.GrokLogo,
                        contentDescription = "Grok",
                        tint = Color.White,
                        modifier = Modifier
                            .size(26.dp)
                            .align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Grok",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = XTextPrimary
                        )
                        Icon(
                            imageVector = XIcons.VerifiedGold,
                            contentDescription = "Verified",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Box(
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .background(XBorder, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "XI",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = XTextPrimary
                            )
                        }
                        Text(
                            text = " @grok · 7小时",
                            fontSize = 15.sp,
                            color = XTextSecondary
                        )
                    }
                    Text(
                        text = "回复给 @Scobleizer 和 @grok",
                        fontSize = 15.sp,
                        color = XTextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "Thanks! We've been optimizing hard at xAI—speed improvements like this are rolling out fast. Glad the transcript readings feel snappier. What else are you testing?",
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        color = XTextPrimary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Bottom input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE1E8ED))
            ) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1506748686214-e9df14d4d9d0?auto=format&fit=crop&w=100&q=80",
                    contentDescription = "Me",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(XBackground)
                    .clickable { onNavigate(XView.ComposeReply) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "发布你的回复",
                    fontSize = 15.sp,
                    color = XTextSecondary
                )
            }
        }
    }
}

@Composable
private fun ComposePostView(
    onBack: () -> Unit,
    toaster: com.dokar.sonner.ToasterState
) {
    var text by remember { mutableStateOf("") }
    val maxChars = 280
    val progress = (text.length.toFloat() / maxChars).coerceIn(0f, 1f)
    val canPost = text.isNotEmpty() && text.length <= maxChars

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = XIcons.Close,
                    contentDescription = "Close",
                    tint = XTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "草稿",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = XBlue
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (canPost) XBlue else XBlueLight)
                        .clickable(enabled = canPost) {
                            if (canPost) {
                                toaster.show("发帖成功", type = ToastType.Success)
                                onBack()
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "发帖",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Compose area
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE1E8ED))
            ) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1506748686214-e9df14d4d9d0?auto=format&fit=crop&w=100&q=80",
                    contentDescription = "Me",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .clickable { }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "每个人",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = XBlue
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                BasicTextField(
                    value = text,
                    onValueChange = { if (it.length <= maxChars) text = it },
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                        color = XTextPrimary
                    ),
                    cursorBrush = SolidColor(XBlue),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box {
                            if (text.isEmpty()) {
                                Text(
                                    text = "有什么新鲜事？",
                                    fontSize = 20.sp,
                                    color = XTextSecondary
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }

        // Image preview
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = XIcons.Image,
                    contentDescription = "Add image",
                    tint = XBlue,
                    modifier = Modifier.size(28.dp)
                )
            }
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            when (index) {
                                0 -> Color(0xFFE6DBCC)
                                1 -> Color.White
                                else -> Color.White
                            }
                        )
                )
            }
        }

        // Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = XIcons.Image,
                        contentDescription = "Image",
                        tint = XBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = XIcons.Gif,
                        contentDescription = "GIF",
                        tint = XBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = XIcons.Poll,
                        contentDescription = "Poll",
                        tint = XBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = XIcons.Emoji,
                        contentDescription = "Emoji",
                        tint = XBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Progress ring
                Box(
                    modifier = Modifier.size(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${text.length}",
                        fontSize = 10.sp,
                        color = if (text.length > maxChars) XRed else XTextSecondary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = XIcons.Plus,
                        contentDescription = "Add",
                        tint = XBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ComposeReplyView(
    onBack: () -> Unit,
    toaster: com.dokar.sonner.ToasterState
) {
    var text by remember { mutableStateOf("") }
    val maxChars = 280
    val canReply = text.isNotEmpty() && text.length <= maxChars

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = XIcons.Close,
                    contentDescription = "Close",
                    tint = XTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (canReply) XBlue else XBlueLight)
                    .clickable(enabled = canReply) {
                        if (canReply) {
                            toaster.show("回复成功", type = ToastType.Success)
                            onBack()
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "回复",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Original tweet context
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE1E8ED))
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1535295972055-1c762f4483e5?auto=format&fit=crop&w=100&q=80",
                        contentDescription = "Scobleizer",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Robert Scoble",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = XTextPrimary
                        )
                        Icon(
                            imageVector = XIcons.Verified,
                            contentDescription = "Verified",
                            tint = XBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = " @Scobleizer · 8小时",
                            fontSize = 15.sp,
                            color = XTextSecondary
                        )
                    }
                    Text(
                        text = "Did someone turn the speed knob up on @Grok?\n\nJust did another transcript reading and, damn, it is way faster than last time I asked it to do the same.",
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        color = XTextPrimary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "回复给 @Scobleizer 和 @grok",
                        fontSize = 15.sp,
                        color = XTextSecondary,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }

            // Reply input
            Row(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE1E8ED))
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1506748686214-e9df14d4d9d0?auto=format&fit=crop&w=100&q=80",
                        contentDescription = "Me",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                BasicTextField(
                    value = text,
                    onValueChange = { if (it.length <= maxChars) text = it },
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                        color = XTextPrimary
                    ),
                    cursorBrush = SolidColor(XBlue),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        Box {
                            if (text.isEmpty()) {
                                Text(
                                    text = "发布你的回复",
                                    fontSize = 20.sp,
                                    color = XTextSecondary
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }

        // Bottom toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = XIcons.Image,
                        contentDescription = "Image",
                        tint = XBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = XIcons.Gif,
                        contentDescription = "GIF",
                        tint = XBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = XIcons.Poll,
                        contentDescription = "Poll",
                        tint = XBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = XIcons.Emoji,
                        contentDescription = "Emoji",
                        tint = XBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            IconButton(onClick = { }) {
                Icon(
                    imageVector = XIcons.Plus,
                    contentDescription = "Add",
                    tint = XBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun getTweets(): List<TweetData> {
    return listOf(
        TweetData(
            avatarUrl = "https://images.unsplash.com/photo-1560250097-0b93528c311a?auto=format&fit=crop&w=100&q=80",
            name = "Elon Musk",
            handle = "@elonmusk",
            time = "2小时",
            content = "Grok gets faster & smarter every week",
            replies = "1,711",
            retweets = "1,141",
            likes = "1万",
            views = "568万",
            quoteTweet = QuoteTweetData(
                avatarUrl = "https://images.unsplash.com/photo-1535295972055-1c762f4483e5?auto=format&fit=crop&w=50&q=80",
                name = "Robert Scoble",
                handle = "@Scobl...",
                time = "7小时",
                content = "Did someone turn the speed knob up on @Grok?\n\nJust did another transcript reading and, damn, it is way faster than last ...",
                isVerified = true
            )
        ),
        TweetData(
            avatarUrl = "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=100&q=80",
            name = "leo 🐾",
            handle = "@synthwavedd",
            time = "1小时",
            content = "The \$200/mo ChatGPT Pro plan will soon no longer be unlimited\n\nI think we all knew this was coming, but still 👎",
            replies = "856",
            retweets = "432",
            likes = "5,231",
            views = "120万",
            quoteTweet = QuoteTweetData(
                avatarUrl = "",
                name = "Chetaslua",
                handle = "@chetaslua",
                time = "1小时",
                content = "🚨 Chatgpt 100\$ plan is coming soon\n\nOne bad news - now the 200\$ plan is not truly unlimited ( it's 20 times the plus uses)...",
                isVerified = true,
                hasImage = true
            )
        )
    )
}

private object XIcons {
    val XLogo: ImageVector
        get() = ImageVector.Builder(
            name = "XLogo",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color.Black)) {
                moveTo(18.244f, 2.25f)
                horizontalLineTo(21.552f)
                lineTo(14.325f, 10.51f)
                lineTo(22.827f, 21.75f)
                horizontalLineTo(16.17f)
                lineTo(10.956f, 14.933f)
                lineTo(4.99f, 21.75f)
                horizontalLineTo(1.68f)
                lineTo(9.41f, 12.915f)
                lineTo(1.254f, 2.25f)
                horizontalLineTo(8.08f)
                lineTo(12.793f, 8.481f)
                close()
            }
        }.build()

    val Verified: ImageVector
        get() = ImageVector.Builder(
            name = "Verified",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(XBlue)) {
                moveTo(22.5f, 12.5f)
                curveTo(22.5f, 10.92f, 21.625f, 9.55f, 20.352f, 8.9f)
                curveTo(20.506f, 8.465f, 20.59f, 7.995f, 20.59f, 7.495f)
                curveTo(20.59f, 5.285f, 18.88f, 3.497f, 16.672f, 3.497f)
                curveTo(16.202f, 3.497f, 15.752f, 3.581f, 15.336f, 3.746f)
                curveTo(14.818f, 2.413f, 13.51f, 1.5f, 12f, 1.5f)
                curveTo(10.49f, 1.5f, 9.184f, 2.417f, 8.663f, 3.75f)
                curveTo(8.247f, 3.585f, 7.797f, 3.5f, 7.327f, 3.5f)
                curveTo(5.117f, 3.5f, 3.409f, 5.29f, 3.409f, 7.5f)
                curveTo(3.409f, 7.995f, 3.493f, 8.465f, 3.647f, 8.9f)
                curveTo(2.374f, 9.55f, 1.499f, 10.92f, 1.499f, 12.5f)
                curveTo(1.499f, 13.96f, 2.24f, 15.247f, 3.366f, 15.947f)
                curveTo(3.382f, 16.097f, 3.391f, 16.247f, 3.391f, 16.4f)
                curveTo(3.391f, 18.61f, 5.101f, 20.4f, 7.309f, 20.4f)
                curveTo(7.779f, 20.4f, 8.229f, 20.314f, 8.645f, 20.15f)
                curveTo(9.165f, 21.484f, 10.471f, 22.4f, 11.982f, 22.4f)
                curveTo(13.493f, 22.4f, 14.799f, 21.484f, 15.32f, 20.15f)
                curveTo(15.736f, 20.314f, 16.186f, 20.4f, 16.656f, 20.4f)
                curveTo(18.866f, 20.4f, 20.576f, 18.61f, 20.576f, 16.4f)
                curveTo(20.576f, 16.247f, 20.566f, 16.097f, 20.55f, 15.947f)
                curveTo(21.677f, 15.247f, 22.417f, 13.96f, 22.417f, 12.5f)
                close()
                moveTo(11.269f, 16.75f)
                lineTo(7.941f, 13.424f)
                lineTo(8.837f, 12.527f)
                lineTo(11.27f, 14.957f)
                lineTo(16.702f, 9.527f)
                lineTo(17.598f, 10.423f)
                lineTo(11.269f, 16.75f)
                close()
            }
        }.build()

    val VerifiedGold: ImageVector
        get() = ImageVector.Builder(
            name = "VerifiedGold",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(Color(0xFFFFD700))) {
                moveTo(2f, 2f)
                horizontalLineTo(22f)
                verticalLineTo(22f)
                horizontalLineTo(2f)
                close()
            }
            path(
                fill = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color.Black),
                stroke = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color.Black),
                strokeLineWidth = 2f
            ) {
                moveTo(16f, 8f)
                lineTo(10f, 16f)
                lineTo(7f, 13f)
            }
        }.build()

    val MoreHoriz: ImageVector
        get() = ImageVector.Builder(
            name = "MoreHoriz",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(XTextSecondary)) {
                moveTo(3f, 12f)
                curveTo(3f, 10.9f, 3.9f, 10f, 5f, 10f)
                reflectiveCurveTo(7f, 10.9f, 7f, 12f)
                reflectiveCurveTo(6.1f, 14f, 5f, 14f)
                reflectiveCurveTo(3f, 13.1f, 3f, 12f)
                close()
                moveTo(12f, 14f)
                curveTo(13.1f, 14f, 14f, 13.1f, 14f, 12f)
                reflectiveCurveTo(13.1f, 10f, 12f, 10f)
                reflectiveCurveTo(10f, 10.9f, 10f, 12f)
                reflectiveCurveTo(10.9f, 14f, 12f, 14f)
                close()
                moveTo(19f, 14f)
                curveTo(20.1f, 14f, 21f, 13.1f, 21f, 12f)
                reflectiveCurveTo(20.1f, 10f, 19f, 10f)
                reflectiveCurveTo(17f, 10.9f, 17f, 12f)
                reflectiveCurveTo(17.9f, 14f, 19f, 14f)
                close()
            }
        }.build()

    val Reply: ImageVector
        get() = ImageVector.Builder(
            name = "Reply",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(XTextSecondary)) {
                moveTo(1.751f, 10f)
                curveTo(1.751f, 5.58f, 5.335f, 2f, 9.756f, 2f)
                horizontalLineTo(14.122f)
                curveTo(18.612f, 2f, 22.251f, 5.64f, 22.251f, 10.13f)
                curveTo(22.251f, 13.09f, 20.644f, 15.81f, 18.055f, 17.24f)
                lineTo(10.001f, 21.7f)
                verticalLineTo(18.01f)
                horizontalLineTo(9.934f)
                curveTo(5.444f, 18.11f, 1.751f, 14.5f, 1.751f, 10f)
                close()
            }
        }.build()

    val Retweet: ImageVector
        get() = ImageVector.Builder(
            name = "Retweet",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(XTextSecondary)) {
                moveTo(4.5f, 3.88f)
                lineTo(8.932f, 8.02f)
                lineTo(7.568f, 9.48f)
                lineTo(5.5f, 7.55f)
                verticalLineTo(16f)
                curveTo(5.5f, 17.1f, 6.396f, 18f, 7.5f, 18f)
                horizontalLineTo(13f)
                verticalLineTo(20f)
                horizontalLineTo(7.5f)
                curveTo(5.291f, 20f, 3.5f, 18.21f, 3.5f, 16f)
                verticalLineTo(7.55f)
                lineTo(1.432f, 9.48f)
                lineTo(0.068f, 8.02f)
                lineTo(4.5f, 3.88f)
                close()
                moveTo(16.5f, 6f)
                horizontalLineTo(11f)
                verticalLineTo(4f)
                horizontalLineTo(16.5f)
                curveTo(18.709f, 4f, 20.5f, 5.79f, 20.5f, 8f)
                verticalLineTo(16.45f)
                lineTo(22.568f, 14.52f)
                lineTo(23.932f, 15.98f)
                lineTo(19.5f, 20.12f)
                lineTo(15.068f, 15.98f)
                lineTo(16.432f, 14.52f)
                lineTo(18.5f, 16.45f)
                verticalLineTo(8f)
                curveTo(18.5f, 6.9f, 17.604f, 6f, 16.5f, 6f)
                close()
            }
        }.build()

    val Like: ImageVector
        get() = ImageVector.Builder(
            name = "Like",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(XTextSecondary)) {
                moveTo(16.697f, 5.5f)
                curveTo(15.475f, 5.44f, 14.018f, 6.01f, 12.807f, 7.66f)
                lineTo(12.002f, 8.75f)
                lineTo(11.196f, 7.66f)
                curveTo(9.984f, 6.01f, 8.526f, 5.44f, 7.304f, 5.5f)
                curveTo(6.061f, 5.57f, 4.955f, 6.28f, 4.394f, 7.41f)
                curveTo(3.842f, 8.53f, 3.761f, 10.19f, 4.873f, 12.23f)
                curveTo(5.947f, 14.2f, 8.13f, 16.5f, 12.001f, 18.84f)
                curveTo(15.871f, 16.5f, 18.053f, 14.2f, 19.127f, 12.23f)
                curveTo(20.238f, 10.19f, 20.157f, 8.53f, 19.605f, 7.41f)
                curveTo(19.044f, 6.28f, 17.938f, 5.57f, 16.697f, 5.5f)
                close()
            }
        }.build()

    val Share: ImageVector
        get() = ImageVector.Builder(
            name = "Share",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(XTextSecondary)) {
                moveTo(8.75f, 21f)
                verticalLineTo(3f)
                horizontalLineTo(10.75f)
                verticalLineTo(21f)
                horizontalLineTo(8.75f)
                close()
                moveTo(18f, 21f)
                verticalLineTo(8.5f)
                horizontalLineTo(20f)
                verticalLineTo(21f)
                horizontalLineTo(18f)
                close()
                moveTo(4f, 21f)
                lineTo(4.004f, 11f)
                horizontalLineTo(6.004f)
                lineTo(6f, 21f)
                horizontalLineTo(4f)
                close()
                moveTo(13.248f, 21f)
                verticalLineTo(14f)
                horizontalLineTo(15.248f)
                verticalLineTo(21f)
                horizontalLineTo(13.248f)
                close()
            }
        }.build()

    val Bookmark: ImageVector
        get() = ImageVector.Builder(
            name = "Bookmark",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(XTextSecondary)) {
                moveTo(4f, 4.5f)
                curveTo(4f, 3.12f, 5.119f, 2f, 6.5f, 2f)
                horizontalLineTo(17.5f)
                curveTo(18.881f, 2f, 20f, 3.12f, 20f, 4.5f)
                verticalLineTo(22.94f)
                lineTo(12f, 17.23f)
                lineTo(4f, 22.94f)
                verticalLineTo(4.5f)
                close()
            }
        }.build()

    val Home: ImageVector
        get() = ImageVector.Builder(
            name = "Home",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(XTextPrimary)) {
                moveTo(12f, 1.696f)
                lineTo(0.622f, 8.807f)
                lineTo(1.682f, 10.503f)
                lineTo(3f, 9.679f)
                verticalLineTo(19.5f)
                curveTo(3f, 20.881f, 4.119f, 22f, 5.5f, 22f)
                horizontalLineTo(18.5f)
                curveTo(19.881f, 22f, 21f, 20.881f, 21f, 19.5f)
                verticalLineTo(9.679f)
                lineTo(22.318f, 10.503f)
                lineTo(23.378f, 8.807f)
                lineTo(12f, 1.696f)
                close()
            }
        }.build()

    val Search: ImageVector
        get() = ImageVector.Builder(
            name = "Search",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(XTextPrimary)) {
                moveTo(10.25f, 3.75f)
                curveTo(6.66f, 3.75f, 3.75f, 6.66f, 3.75f, 10.25f)
                reflectiveCurveTo(6.66f, 16.75f, 10.25f, 16.75f)
                curveTo(12.045f, 16.75f, 13.669f, 16.024f, 14.846f, 14.846f)
                curveTo(16.024f, 13.669f, 16.75f, 12.045f, 16.75f, 10.25f)
                curveTo(16.75f, 6.66f, 13.84f, 3.75f, 10.25f, 3.75f)
                close()
                moveTo(1.75f, 10.25f)
                curveTo(1.75f, 5.556f, 5.556f, 1.75f, 10.25f, 1.75f)
                reflectiveCurveTo(18.75f, 5.556f, 18.75f, 10.25f)
                curveTo(18.75f, 12.236f, 18.068f, 14.065f, 16.926f, 15.512f)
                lineTo(21.707f, 20.293f)
                lineTo(20.293f, 21.707f)
                lineTo(15.512f, 16.926f)
                curveTo(14.065f, 18.068f, 12.236f, 18.75f, 10.25f, 18.75f)
                curveTo(5.556f, 18.75f, 1.75f, 14.944f, 1.75f, 10.25f)
                close()
            }
        }.build()

    val Notifications: ImageVector
        get() = ImageVector.Builder(
            name = "Notifications",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(XTextPrimary)) {
                moveTo(12f, 22f)
                curveTo(13.868f, 22f, 15.395f, 20.568f, 15.49f, 18.736f)
                lineTo(15.5f, 18.5f)
                horizontalLineTo(8.5f)
                lineTo(8.51f, 18.736f)
                curveTo(8.605f, 20.568f, 10.132f, 22f, 12f, 22f)
                close()
                moveTo(21f, 16.5f)
                verticalLineTo(15.368f)
                lineTo(19f, 12.701f)
                verticalLineTo(8.5f)
                curveTo(19f, 4.91f, 16.09f, 2f, 12.5f, 2f)
                horizontalLineTo(11.5f)
                curveTo(7.91f, 2f, 5f, 4.91f, 5f, 8.5f)
                verticalLineTo(12.701f)
                lineTo(3f, 15.368f)
                verticalLineTo(16.5f)
                horizontalLineTo(21f)
                close()
            }
        }.build()

    val Messages: ImageVector
        get() = ImageVector.Builder(
            name = "Messages",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(XTextPrimary)) {
                moveTo(1.998f, 5.5f)
                curveTo(1.998f, 4.119f, 3.117f, 3f, 4.498f, 3f)
                horizontalLineTo(19.498f)
                curveTo(20.879f, 3f, 21.998f, 4.119f, 21.998f, 5.5f)
                verticalLineTo(18.5f)
                curveTo(21.998f, 19.881f, 20.879f, 21f, 19.498f, 21f)
                horizontalLineTo(4.498f)
                curveTo(3.117f, 21f, 1.998f, 19.881f, 1.998f, 18.5f)
                verticalLineTo(5.5f)
                close()
                moveTo(4.498f, 5f)
                curveTo(4.222f, 5f, 3.998f, 5.224f, 3.998f, 5.5f)
                verticalLineTo(6.941f)
                lineTo(11.998f, 12.655f)
                lineTo(19.998f, 6.941f)
                verticalLineTo(5.5f)
                curveTo(19.998f, 5.224f, 19.774f, 5f, 19.498f, 5f)
                horizontalLineTo(4.498f)
                close()
            }
        }.build()

    val ArrowBack: ImageVector
        get() = ImageVector.Builder(
            name = "ArrowBack",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(XTextPrimary)) {
                moveTo(7.414f, 13f)
                lineTo(12.457f, 18.04f)
                lineTo(11.043f, 19.46f)
                lineTo(3.586f, 12f)
                lineTo(11.043f, 4.54f)
                lineTo(12.457f, 5.96f)
                lineTo(7.414f, 11f)
                horizontalLineTo(21f)
                verticalLineTo(13f)
                horizontalLineTo(7.414f)
                close()
            }
        }.build()

    val Close: ImageVector
        get() = ImageVector.Builder(
            name = "Close",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(XTextPrimary)) {
                moveTo(10.59f, 12f)
                lineTo(4.54f, 5.96f)
                lineTo(5.96f, 4.54f)
                lineTo(12f, 10.59f)
                lineTo(18.04f, 4.54f)
                lineTo(19.46f, 5.96f)
                lineTo(13.41f, 12f)
                lineTo(19.46f, 18.04f)
                lineTo(18.04f, 19.46f)
                lineTo(12f, 13.41f)
                lineTo(5.96f, 19.46f)
                lineTo(4.54f, 18.04f)
                lineTo(10.59f, 12f)
                close()
            }
        }.build()

    val Plus: ImageVector
        get() = ImageVector.Builder(
            name = "Plus",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = androidx.compose.ui.graphics.SolidColor(Color.Unspecified),
                stroke = androidx.compose.ui.graphics.SolidColor(Color.White),
                strokeLineWidth = 1.5f
            ) {
                moveTo(12f, 4.5f)
                verticalLineTo(19.5f)
                moveTo(19.5f, 12f)
                horizontalLineTo(4.5f)
            }
        }.build()

    val Image: ImageVector
        get() = ImageVector.Builder(
            name = "Image",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(XBlue)) {
                moveTo(3f, 5.5f)
                curveTo(3f, 4.119f, 4.119f, 3f, 5.5f, 3f)
                horizontalLineTo(18.5f)
                curveTo(19.881f, 3f, 21f, 4.119f, 21f, 5.5f)
                verticalLineTo(18.5f)
                curveTo(21f, 19.881f, 19.881f, 21f, 18.5f, 21f)
                horizontalLineTo(5.5f)
                curveTo(4.119f, 21f, 3f, 19.881f, 3f, 18.5f)
                verticalLineTo(5.5f)
                close()
                moveTo(5.5f, 5f)
                curveTo(5.224f, 5f, 5f, 5.224f, 5f, 5.5f)
                verticalLineTo(14.586f)
                lineTo(8f, 11.586f)
                lineTo(11f, 14.586f)
                lineTo(16f, 9.586f)
                lineTo(19f, 12.586f)
                verticalLineTo(5.5f)
                curveTo(19f, 5.224f, 18.776f, 5f, 18.5f, 5f)
                horizontalLineTo(5.5f)
                close()
                moveTo(19f, 15.414f)
                lineTo(16f, 12.414f)
                lineTo(11f, 17.414f)
                lineTo(8f, 14.414f)
                lineTo(5f, 17.414f)
                verticalLineTo(18.5f)
                curveTo(5f, 18.776f, 5.224f, 19f, 5.5f, 19f)
                horizontalLineTo(18.5f)
                curveTo(18.776f, 19f, 19f, 18.776f, 19f, 18.5f)
                verticalLineTo(15.414f)
                close()
                moveTo(9.75f, 7f)
                curveTo(8.784f, 7f, 8f, 7.784f, 8f, 8.75f)
                reflectiveCurveTo(8.784f, 10.5f, 9.75f, 10.5f)
                reflectiveCurveTo(11.5f, 9.716f, 11.5f, 8.75f)
                reflectiveCurveTo(10.716f, 7f, 9.75f, 7f)
                close()
            }
        }.build()

    val Gif: ImageVector
        get() = ImageVector.Builder(
            name = "Gif",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(XBlue)) {
                moveTo(3f, 5.5f)
                curveTo(3f, 4.119f, 4.119f, 3f, 5.5f, 3f)
                horizontalLineTo(18.5f)
                curveTo(19.881f, 3f, 21f, 4.119f, 21f, 5.5f)
                verticalLineTo(18.5f)
                curveTo(21f, 19.881f, 19.881f, 21f, 18.5f, 21f)
                horizontalLineTo(5.5f)
                curveTo(4.119f, 21f, 3f, 19.881f, 3f, 18.5f)
                verticalLineTo(5.5f)
                close()
                moveTo(5.5f, 5f)
                curveTo(5.224f, 5f, 5f, 5.224f, 5f, 5.5f)
                verticalLineTo(18.5f)
                curveTo(5f, 18.776f, 5.224f, 19f, 5.5f, 19f)
                horizontalLineTo(18.5f)
                curveTo(18.776f, 19f, 19f, 18.776f, 19f, 18.5f)
                verticalLineTo(5.5f)
                curveTo(19f, 5.224f, 18.776f, 5f, 18.5f, 5f)
                horizontalLineTo(5.5f)
                close()
                moveTo(7.5f, 14f)
                horizontalLineTo(9.75f)
                verticalLineTo(11.75f)
                horizontalLineTo(7.5f)
                verticalLineTo(14f)
                close()
                moveTo(16f, 14f)
                horizontalLineTo(14.5f)
                verticalLineTo(9.5f)
                horizontalLineTo(16f)
                verticalLineTo(14f)
                close()
                moveTo(11.25f, 14f)
                horizontalLineTo(9f)
                verticalLineTo(9.5f)
                horizontalLineTo(11.25f)
                verticalLineTo(10.75f)
                horizontalLineTo(10.5f)
                verticalLineTo(12.75f)
                horizontalLineTo(11.25f)
                verticalLineTo(14f)
                close()
            }
        }.build()

    val Poll: ImageVector
        get() = ImageVector.Builder(
            name = "Poll",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(XBlue)) {
                moveTo(4f, 4f)
                horizontalLineTo(20f)
                verticalLineTo(6f)
                horizontalLineTo(4f)
                verticalLineTo(4f)
                close()
                moveTo(4f, 10f)
                horizontalLineTo(20f)
                verticalLineTo(12f)
                horizontalLineTo(4f)
                verticalLineTo(10f)
                close()
                moveTo(4f, 16f)
                horizontalLineTo(20f)
                verticalLineTo(18f)
                horizontalLineTo(4f)
                verticalLineTo(16f)
                close()
            }
        }.build()

    val Emoji: ImageVector
        get() = ImageVector.Builder(
            name = "Emoji",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(XBlue)) {
                moveTo(12f, 2f)
                curveTo(8.13f, 2f, 5f, 5.13f, 5f, 9f)
                curveTo(5f, 14.25f, 12f, 22f, 12f, 22f)
                reflectiveCurveTo(19f, 14.25f, 19f, 9f)
                curveTo(19f, 5.13f, 15.87f, 2f, 12f, 2f)
                close()
                moveTo(12f, 11.5f)
                curveTo(10.62f, 11.5f, 9.5f, 10.38f, 9.5f, 9f)
                reflectiveCurveTo(10.62f, 6.5f, 12f, 6.5f)
                reflectiveCurveTo(14.5f, 7.62f, 14.5f, 9f)
                reflectiveCurveTo(13.38f, 11.5f, 12f, 11.5f)
                close()
            }
        }.build()

    val Settings: ImageVector
        get() = ImageVector.Builder(
            name = "Settings",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(XTextSecondary)) {
                moveTo(3f, 12f)
                curveTo(3f, 10.9f, 3.9f, 10f, 5f, 10f)
                reflectiveCurveTo(7f, 10.9f, 7f, 12f)
                reflectiveCurveTo(6.1f, 14f, 5f, 14f)
                reflectiveCurveTo(3f, 13.1f, 3f, 12f)
                close()
                moveTo(12f, 14f)
                curveTo(13.1f, 14f, 14f, 13.1f, 14f, 12f)
                reflectiveCurveTo(13.1f, 10f, 12f, 10f)
                reflectiveCurveTo(10f, 10.9f, 10f, 12f)
                reflectiveCurveTo(10.9f, 14f, 12f, 14f)
                close()
                moveTo(21f, 14f)
                curveTo(22.1f, 14f, 23f, 13.1f, 23f, 12f)
                reflectiveCurveTo(22.1f, 10f, 21f, 10f)
                reflectiveCurveTo(19f, 10.9f, 19f, 12f)
                reflectiveCurveTo(19.9f, 14f, 21f, 14f)
                close()
            }
        }.build()

    val GrokLogo: ImageVector
        get() = ImageVector.Builder(
            name = "GrokLogo",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = androidx.compose.ui.graphics.SolidColor(Color.White)) {
                moveTo(21f, 4f)
                horizontalLineTo(3f)
                verticalLineTo(22f)
                horizontalLineTo(21f)
                verticalLineTo(4f)
                close()
                moveTo(19f, 18f)
                horizontalLineTo(5f)
                verticalLineTo(6f)
                horizontalLineTo(19f)
                verticalLineTo(18f)
                close()
                moveTo(8f, 14f)
                lineTo(12f, 10f)
                lineTo(16f, 14f)
                verticalLineTo(12f)
                lineTo(12f, 8f)
                lineTo(8f, 12f)
                verticalLineTo(14f)
                close()
            }
        }.build()
}
