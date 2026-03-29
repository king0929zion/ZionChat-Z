package me.rerere.rikkahub.ui.pages.zphone

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
            .background(XSurface.copy(alpha = 0.92f))
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
                    painter = painterResource(R.drawable.x_logo_black),
                    contentDescription = "X",
                    modifier = Modifier.size(22.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .pressableScale(pressedScale = 0.96f, onClick = {}),
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
    val titles = listOf(
        stringResource(R.string.x_timeline_tab_for_you),
        stringResource(R.string.x_timeline_tab_following),
        stringResource(R.string.x_timeline_tab_ai)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(XSurface)
            .horizontalScroll(rememberScrollState())
    ) {
        titles.forEachIndexed { index, title ->
            Column(
                modifier = Modifier
                    .defaultMinSize(minWidth = 128.dp)
                    .height(53.dp)
                    .clickable { onSelect(index) }
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = if (selectedIndex == index) XText else XSubText,
                    fontSize = 15.sp,
                    fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Medium,
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

    HorizontalDivider(color = XDivider)
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
    val icons = listOf(
        XCloneIcons.Home,
        XCloneIcons.Search,
        XCloneIcons.Spark,
        XCloneIcons.Bell,
        XCloneIcons.Mail
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(XSurface)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        HorizontalDivider(color = XDivider)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(53.dp)
                .padding(horizontal = 6.dp),
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
                        tint = XText,
                        modifier = Modifier.size(28.dp)
                    )
                }
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(XSurface)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        HorizontalDivider(color = XDivider)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
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
                Text(
                    text = stringResource(R.string.x_timeline_reply_placeholder),
                    color = XSubText,
                    fontSize = 15.sp
                )
            }
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PostAvatar(settings = settings, post = post, size = 40.dp)
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.authorName,
                            color = XText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
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
                    }
                    Text(text = post.authorHandle, color = XSubText, fontSize = 15.sp)
                }
            }
            XFollowChip()
        }

        Text(
            text = post.content,
            color = XText,
            fontSize = 17.sp,
            lineHeight = 24.sp,
            letterSpacing = (-0.1).sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(
            text = "${absoluteTime(post.createAt)} · ${timelineMetricCount(post.viewCount)} ${stringResource(R.string.x_timeline_views_suffix)}",
            color = XSubText,
            fontSize = 15.sp,
            modifier = Modifier.padding(start = 16.dp, top = 18.dp)
        )

        HorizontalDivider(color = XDivider, modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp))

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            DetailMetric(label = stringResource(R.string.x_timeline_metric_reposts), value = detailMetricCount(post.repostCount))
            DetailMetric(label = stringResource(R.string.x_timeline_metric_likes), value = detailMetricCount(post.likeCount))
            DetailMetric(
                label = stringResource(R.string.x_timeline_metric_bookmarks),
                value = detailMetricCount(if (post.bookmarkedByUser) 20 else 19)
            )
        }

        HorizontalDivider(color = XDivider, modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp))
    }
}

@Composable
internal fun RelevantReplyCard(
    settings: Settings,
    post: XPostEntity,
    parentHandle: String,
    modifier: Modifier = Modifier,
) {
    val isGrok = post.authorHandle.lowercase() == "@grok"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        if (isGrok) {
            GrokReplyAvatar()
        } else {
            PostAvatar(settings = settings, post = post, size = 40.dp)
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = post.authorName,
                        color = XText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (isGrok) {
                        Spacer(modifier = Modifier.width(4.dp))
                        GrokGoldBadge()
                        Spacer(modifier = Modifier.width(6.dp))
                        IntelligenceChip()
                    } else if (isVerified(post)) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = XCloneIcons.Verified,
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = post.authorHandle, color = XSubText, fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "·", color = XSubText, fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = relativeTime(post.createAt), color = XSubText, fontSize = 15.sp)
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
                text = buildAnnotatedString {
                    append(stringResource(R.string.x_timeline_reply_to_prefix))
                    withStyle(style = SpanStyle(color = XBlue)) {
                        append(parentHandle)
                    }
                },
                color = XSubText,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 2.dp)
            )

            Text(
                text = detailReplyContent(post),
                color = XText,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }

    HorizontalDivider(color = XDivider)
}

@Composable
internal fun DetailMetric(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = value, color = XText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
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
        Text(
            text = stringResource(R.string.x_timeline_follow),
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GrokReplyAvatar() {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = XCloneIcons.GrokMark,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
private fun GrokGoldBadge() {
    Canvas(modifier = Modifier.size(18.dp)) {
        val stroke = 1.8.dp.toPx()
        drawRoundRect(
            color = Color(0xFFFFD700),
            cornerRadius = CornerRadius(4.dp.toPx())
        )
        drawLine(
            color = Color.Black,
            start = Offset(size.width * 0.28f, size.height * 0.56f),
            end = Offset(size.width * 0.45f, size.height * 0.72f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.Black,
            start = Offset(size.width * 0.45f, size.height * 0.72f),
            end = Offset(size.width * 0.74f, size.height * 0.34f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun IntelligenceChip() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFEFF3F4))
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(
            text = "I",
            color = XText,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
