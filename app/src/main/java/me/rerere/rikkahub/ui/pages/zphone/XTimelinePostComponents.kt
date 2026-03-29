package me.rerere.rikkahub.ui.pages.zphone

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.datastore.Settings
import me.rerere.rikkahub.data.db.entity.XPostEntity

@Composable
internal fun XPostCard(
    settings: Settings,
    post: XPostEntity,
    modifier: Modifier = Modifier,
    showReplyingHint: Boolean = false,
    replyTargetHandle: String? = null,
    onClick: () -> Unit,
    onLike: () -> Unit,
    onRepost: () -> Unit,
    onBookmark: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        PostAvatar(settings = settings, post = post, size = 40.dp)

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.authorName,
                            color = XText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        if (isVerified(post)) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = XCloneIcons.Verified,
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        if (post.authorHandle == "@elonmusk") {
                            Spacer(modifier = Modifier.width(4.dp))
                            Image(
                                painter = painterResource(R.drawable.x_logo_black),
                                contentDescription = "X",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = post.authorHandle, color = XSubText, fontSize = 15.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "·", color = XSubText, fontSize = 15.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = relativeTime(post.createAt), color = XSubText, fontSize = 15.sp)
                    }

                    if (showReplyingHint && !replyTargetHandle.isNullOrBlank()) {
                        Text(
                            text = stringResource(R.string.x_timeline_reply_to_format, replyTargetHandle),
                            color = XSubText,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable(onClick = {})
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = XCloneIcons.More,
                        contentDescription = null,
                        tint = XSubText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = post.content,
                color = XText,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            if (!post.quoteContent.isNullOrBlank()) {
                QuoteCard(post = post, modifier = Modifier.padding(top = 12.dp))
            }

            PostActionBar(
                post = post,
                modifier = Modifier.padding(top = 12.dp),
                onLike = onLike,
                onRepost = onRepost,
                onBookmark = onBookmark,
            )
        }
    }

    HorizontalDivider(color = XDivider)
}

@Composable
internal fun QuoteCard(
    post: XPostEntity,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, XDivider, RoundedCornerShape(20.dp))
            .background(XSurface)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            QuoteAvatar(post = post)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = post.quoteAuthorName.orEmpty(),
                color = XText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = XCloneIcons.Verified,
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = post.quoteHandle.orEmpty(), color = XSubText, fontSize = 15.sp)
            quoteTimeLabel(post)?.let { time ->
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "·", color = XSubText, fontSize = 15.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = time, color = XSubText, fontSize = 15.sp)
            }
        }

        Text(
            text = post.quoteContent.orEmpty(),
            color = XText,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        if (post.quotePreview == "wireframe") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFBDE3F3))
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(110.dp)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .border(1.dp, XDivider, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(XSurface)
                ) {
                    Box(
                        modifier = Modifier
                            .width(96.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color(0xFFE5E7EB))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
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
}

@Composable
internal fun PostActionBar(
    post: XPostEntity,
    modifier: Modifier = Modifier,
    onLike: () -> Unit,
    onRepost: () -> Unit,
    onBookmark: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionMetric(icon = XCloneIcons.Reply, count = timelineMetricCount(post.replyCount), tint = XSubText, onClick = {})
        ActionMetric(icon = XCloneIcons.Repost, count = timelineMetricCount(post.repostCount), tint = if (post.repostedByUser) XGreen else XSubText, onClick = onRepost)
        ActionMetric(icon = XCloneIcons.Like, count = timelineMetricCount(post.likeCount), tint = if (post.likedByUser) XPink else XSubText, onClick = onLike)
        ActionMetric(icon = XCloneIcons.Stats, count = timelineMetricCount(post.viewCount), tint = XSubText, onClick = {})
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            MiniAction(icon = XCloneIcons.Bookmark, tint = if (post.bookmarkedByUser) XBlue else XSubText, onClick = onBookmark)
            MiniAction(icon = XCloneIcons.Share, tint = XSubText, onClick = {})
        }
    }
}

@Composable
internal fun ActionMetric(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .clickable(onClick = onClick)
                .padding(7.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(text = count, color = tint, fontSize = 13.sp)
    }
}

@Composable
internal fun MiniAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(7.dp),
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
private fun QuoteAvatar(post: XPostEntity) {
    val isScoble = post.quoteAuthorName == "Robert Scoble"
    if (isScoble) {
        AsyncImage(
            model = "https://images.unsplash.com/photo-1535295972055-1c762f4483e5?auto=format&fit=crop&w=50&q=80",
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        return
    }

    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = post.quoteAuthorName.orEmpty().take(1).ifBlank { "C" },
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
