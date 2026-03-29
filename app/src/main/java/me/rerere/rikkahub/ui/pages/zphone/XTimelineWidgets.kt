package me.rerere.rikkahub.ui.pages.zphone

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.datastore.Settings
import me.rerere.rikkahub.data.db.entity.XPostEntity
import me.rerere.rikkahub.ui.components.ui.pressableScale

@Composable
internal fun FeedHeader(settings: Settings) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(XSurface)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(53.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CurrentUserAvatar(settings = settings, size = 32.dp)

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(R.drawable.x_logo_black),
                    contentDescription = "X",
                    modifier = Modifier.size(22.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .pressableScale(pressedScale = 0.95f, onClick = {}),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = XCloneIcons.More,
                    contentDescription = null,
                    tint = XSubText,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}

@Composable
internal fun FeedTopTabs(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    val titles = listOf("为你推荐", "正在关注", "AI — Rumors & Insights")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .background(XSurface)
            .border(1.dp, XDivider)
    ) {
        titles.forEachIndexed { index, title ->
            Column(
                modifier = Modifier
                    .defaultMinSize(minWidth = 132.dp)
                    .height(53.dp)
                    .clickable { onSelect(index) }
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = if (selectedIndex == index) XText else XSubText,
                    fontSize = 15.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .width(56.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (selectedIndex == index) XBlue else Color.Transparent)
                )
            }
        }
    }
}

@Composable
internal fun FloatingComposeButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(end = 16.dp, bottom = 72.dp)
            .size(56.dp)
            .clip(CircleShape)
            .background(XBlue)
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
internal fun XBottomNavBar(
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    onSelect: (Int) -> Unit,
) {
    val icons = listOf(XCloneIcons.Home, XCloneIcons.Search, XCloneIcons.Spark, XCloneIcons.Bell, XCloneIcons.Mail)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(XSurface)
            .border(1.dp, XDivider)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(53.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
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
                    tint = if (index == selectedIndex) XText else XSubText,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
internal fun ReplyBar(
    settings: Settings,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(XSurface)
            .border(1.dp, XDivider)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CurrentUserAvatar(settings = settings, size = 32.dp)
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFFF0F3F4))
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 11.dp)
        ) {
            Text(text = "发布你的回复", color = XSubText, fontSize = 15.sp)
        }
    }
}

@Composable
internal fun DetailPostContent(
    settings: Settings,
    post: XPostEntity,
    onLike: () -> Unit,
    onRepost: () -> Unit,
    onBookmark: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PostAvatar(settings = settings, post = post, size = 40.dp)
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = post.authorName, color = XText, fontSize = 15.sp)
                        if (isVerified(post)) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = XCloneIcons.Verified, contentDescription = null, tint = Color.Unspecified, modifier = Modifier.size(18.dp))
                        }
                    }
                    Text(text = post.authorHandle, color = XSubText, fontSize = 15.sp)
                }
            }
            XFollowChip()
        }

        Text(text = post.content, color = XText, fontSize = 17.sp, lineHeight = 24.sp, modifier = Modifier.padding(horizontal = 16.dp))
        Text(text = "${absoluteTime(post.createAt)} · ${compactCount(post.viewCount)} 查看", color = XSubText, fontSize = 15.sp, modifier = Modifier.padding(start = 16.dp, top = 18.dp))
        HorizontalDivider(color = XDivider, modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp))

        Row(modifier = Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(22.dp)) {
            DetailMetric(label = "转帖", value = compactCount(post.repostCount))
            DetailMetric(label = "喜欢", value = compactCount(post.likeCount))
            DetailMetric(label = "书签", value = if (post.bookmarkedByUser) "已收藏" else "未收藏")
        }

        HorizontalDivider(color = XDivider, modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp))
        PostActionBar(post = post, modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), onLike = onLike, onRepost = onRepost, onBookmark = onBookmark)
        HorizontalDivider(color = XDivider)
    }
}

@Composable
internal fun DetailMetric(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = value, color = XText, fontSize = 15.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, color = XSubText, fontSize = 15.sp)
    }
}

@Composable
internal fun XFollowChip() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(XText)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = "订阅", color = Color.White, fontSize = 15.sp)
    }
}
