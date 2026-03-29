package me.rerere.rikkahub.ui.pages.zphone

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.datastore.Settings
import me.rerere.rikkahub.data.db.entity.XPostEntity
import me.rerere.rikkahub.ui.components.ui.pressableScale

@Composable
internal fun ComposerHeader(
    label: String,
    enabled: Boolean,
    onClose: () -> Unit,
    onSubmit: () -> Unit,
    trailingHint: String? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(XSurface)
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
                    .pressableScale(pressedScale = 0.95f, onClick = onClose),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = XCloneIcons.Close,
                    contentDescription = null,
                    tint = XText,
                    modifier = Modifier.size(18.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                trailingHint?.let {
                    Text(
                        text = it,
                        color = XBlue,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (enabled) XBlue else Color(0xFF8ECDF8))
                        .clickable(enabled = enabled, onClick = onSubmit)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(XDivider)
        )
    }
}

@Composable
internal fun ComposerAudienceChip() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .border(1.dp, Color(0xFFCFD9DE), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.x_timeline_audience_everyone),
            color = XBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = XCloneIcons.ChevronDown,
            contentDescription = null,
            tint = XBlue,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
internal fun EveryoneCanReplyRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(XSurface)
            .border(1.dp, XDivider)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = XCloneIcons.Info,
            contentDescription = null,
            tint = XBlue,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.x_timeline_everyone_can_reply),
            color = XBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
internal fun ComposerTextField(
    value: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
) {
    Box(modifier = modifier) {
        BasicTextField(
            value = value,
            onValueChange = {
                if (it.length <= ComposerLimit) onValueChange(it)
            },
            textStyle = TextStyle(
                color = XText,
                fontSize = 20.sp,
                lineHeight = 24.sp
            ),
            cursorBrush = SolidColor(XBlue),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 120.dp)
        )

        if (value.isBlank()) {
            Text(text = placeholder, color = XSubText, fontSize = 20.sp)
        }
    }
}

@Composable
internal fun ComposerFooter(
    textLength: Int,
    modifier: Modifier = Modifier,
    showReplyInfo: Boolean = true,
) {
    Column(
        modifier = modifier
            .background(XSurface)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        if (showReplyInfo) {
            PostAttachmentStrip()
            EveryoneCanReplyRow()
        } else {
            ReplyAttachmentStrip()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(53.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                ComposerToolbarIcon(icon = XCloneIcons.Image)
                ComposerToolbarIcon(icon = XCloneIcons.Poll)
                ComposerToolbarIcon(icon = XCloneIcons.Lines)
                ComposerToolbarIcon(icon = XCloneIcons.Location)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ComposerProgressRing(length = textLength)
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(Color(0xFFCFD9DE))
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color(0xFFCFD9DE), CircleShape)
                        .background(XSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = XCloneIcons.Plus,
                        contentDescription = null,
                        tint = XBlue,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PostAttachmentStrip() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CameraAttachmentTile()
        DogMemeTile()
        SkeletonCardTile(primaryWidth = 48.dp, secondaryWidth = 64.dp)
        SkeletonCardTile(primaryWidth = 56.dp, secondaryWidth = 40.dp, tertiaryWidth = 64.dp)
    }
}

@Composable
private fun ReplyAttachmentStrip() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CameraAttachmentTile()
        CompactPreviewTile(
            background = Color(0xFFF7F9F9),
            title = "Robert Scoble",
            lines = listOf("Did someone turn the speed knob up on @Grok?")
        )
        CompactPreviewTile(
            background = XSurface,
            title = "leo",
            lines = listOf("The \$200/mo ChatGPT Pro plan will soon...")
        )
        CodePreviewTile()
    }
}

@Composable
private fun CameraAttachmentTile() {
    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(XSurface)
            .border(1.dp, Color(0xFFCFD9DE), RoundedCornerShape(20.dp)),
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
private fun DogMemeTile() {
    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFE6DBCC))
            .border(1.dp, Color(0xFFCFD9DE), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Dog Meme",
            color = XText.copy(alpha = 0.5f),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SkeletonCardTile(
    primaryWidth: androidx.compose.ui.unit.Dp,
    secondaryWidth: androidx.compose.ui.unit.Dp,
    tertiaryWidth: androidx.compose.ui.unit.Dp = 56.dp,
) {
    Column(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(XSurface)
            .border(1.dp, Color(0xFFCFD9DE), RoundedCornerShape(20.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(Color(0xFFD1D5DB))
        )
        Box(
            modifier = Modifier
                .width(primaryWidth)
                .height(6.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFFE5E7EB))
        )
        Box(
            modifier = Modifier
                .width(secondaryWidth)
                .height(5.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFFE5E7EB))
        )
        Box(
            modifier = Modifier
                .width(tertiaryWidth)
                .height(5.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFFE5E7EB))
        )
    }
}

@Composable
private fun CompactPreviewTile(
    background: Color,
    title: String,
    lines: List<String>,
) {
    Column(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .border(1.dp, Color(0xFFCFD9DE), RoundedCornerShape(20.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            color = XText,
            fontSize = 7.sp,
            fontWeight = FontWeight.Bold
        )
        lines.forEach { line ->
            Text(
                text = line,
                color = XSubText,
                fontSize = 6.sp,
                lineHeight = 8.sp,
                maxLines = 3
            )
        }
    }
}

@Composable
private fun CodePreviewTile() {
    Column(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1C2128))
            .border(1.dp, Color(0xFFCFD9DE), RoundedCornerShape(20.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "This code will let you recover your account...",
            color = Color(0xFF8B949E),
            fontSize = 6.sp,
            lineHeight = 8.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "R68645",
            color = Color(0xFF58A6FF),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
internal fun ComposerToolbarIcon(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .pressableScale(pressedScale = 0.94f, onClick = {}),
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
internal fun ComposerProgressRing(length: Int) {
    val progress = (length.coerceIn(0, ComposerLimit).toFloat() / ComposerLimit).coerceIn(0f, 1f)
    val strokeColor = when {
        length > ComposerLimit -> Color(0xFFF4212E)
        progress > 0.8f -> Color(0xFFFFD400)
        progress > 0f -> XBlue
        else -> Color(0xFFEFF3F4)
    }

    Canvas(modifier = Modifier.size(28.dp)) {
        drawCircle(color = Color(0xFFEFF3F4), style = Stroke(width = 3.dp.toPx()))
        if (progress > 0f) {
            drawArc(
                color = strokeColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 3.dp.toPx())
            )
        }
    }
}

@Composable
internal fun ReplyContextCard(settings: Settings, post: XPostEntity) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            PostAvatar(settings = settings, post = post, size = 40.dp)
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(92.dp)
                    .background(Color(0xFFCFD9DE))
            )
        }

        Column(modifier = Modifier.weight(1f)) {
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
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = post.authorHandle, color = XSubText, fontSize = 15.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "·", color = XSubText, fontSize = 15.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = relativeTime(post.createAt), color = XSubText, fontSize = 15.sp)
            }

            Text(
                text = post.content,
                color = XText,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 6.dp)
            )

            Text(
                text = stringResource(
                    if (post.authorHandle.lowercase() == "@scobleizer") {
                        R.string.x_timeline_replying_to_format
                    } else {
                        R.string.x_timeline_reply_to_format
                    },
                    post.authorHandle
                ),
                color = XSubText,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}
