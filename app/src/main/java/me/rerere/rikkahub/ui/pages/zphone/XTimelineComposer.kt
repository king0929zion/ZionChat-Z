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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            Icon(imageVector = XCloneIcons.Close, contentDescription = null, tint = XText, modifier = Modifier.size(18.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            trailingHint?.let {
                Text(text = it, color = XBlue, fontSize = 15.sp)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (enabled) XBlue else XBlue.copy(alpha = 0.45f))
                    .clickable(enabled = enabled, onClick = onSubmit)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = label, color = Color.White, fontSize = 15.sp)
            }
        }
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
        Text(text = "每个人", color = XBlue, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Icon(imageVector = XCloneIcons.ChevronDown, contentDescription = null, tint = XBlue, modifier = Modifier.size(16.dp))
    }
}

@Composable
internal fun EveryoneCanReplyRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, XDivider)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = XCloneIcons.Info, contentDescription = null, tint = XBlue, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "所有人可以回复", color = XBlue, fontSize = 14.sp)
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
                if (it.length <= 560) onValueChange(it)
            },
            textStyle = androidx.compose.ui.text.TextStyle(color = XText, fontSize = 20.sp, lineHeight = 24.sp),
            cursorBrush = SolidColor(XBlue),
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 120.dp)
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
            .border(1.dp, XDivider)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        ComposerAttachmentStrip(showReplyInfo = showReplyInfo)

        Row(
            modifier = Modifier.fillMaxWidth().height(53.dp).padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                ComposerToolbarIcon(icon = XCloneIcons.Image)
                ComposerToolbarIcon(icon = XCloneIcons.Poll)
                ComposerToolbarIcon(icon = XCloneIcons.Lines)
                ComposerToolbarIcon(icon = XCloneIcons.Location)
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ComposerProgressRing(length = textLength)
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFCFD9DE)))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color(0xFFCFD9DE), CircleShape)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = XCloneIcons.Plus, contentDescription = null, tint = XBlue, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
internal fun ComposerAttachmentStrip(showReplyInfo: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AttachmentTile(background = XSurface, borderColor = Color(0xFFCFD9DE))
        AttachmentWireframeCard(background = if (showReplyInfo) Color(0xFFF7F9F9) else Color(0xFFE6DBCC), title = if (showReplyInfo) "Robert Scoble" else "Dog Meme", lines = if (showReplyInfo) listOf("Did someone turn the speed knob up on @Grok?") else listOf("图片预览"))
        AttachmentWireframeCard(background = XSurface, title = "leo", lines = listOf("The \$200/mo ChatGPT Pro plan will soon..."))
        AttachmentWireframeCard(background = Color(0xFF1C2128), title = "CODE", titleColor = Color(0xFF8B949E), lines = listOf("This code will let you recover your account..."))
    }
}

@Composable
internal fun AttachmentTile(background: Color, borderColor: Color) {
    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = XCloneIcons.Image, contentDescription = null, tint = XBlue, modifier = Modifier.size(28.dp))
    }
}

@Composable
internal fun AttachmentWireframeCard(
    background: Color,
    title: String,
    lines: List<String>,
    titleColor: Color = XText,
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
        Text(text = title, color = titleColor, fontSize = 8.sp)
        lines.forEach { line ->
            Text(text = line, color = if (background == Color(0xFF1C2128)) Color(0xFF8B949E) else XSubText, fontSize = 6.sp, lineHeight = 8.sp, maxLines = 3)
        }
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
        Icon(imageVector = icon, contentDescription = null, tint = XBlue, modifier = Modifier.size(20.dp))
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
            drawArc(color = strokeColor, startAngle = -90f, sweepAngle = 360f * progress, useCenter = false, style = Stroke(width = 3.dp.toPx()))
        }
    }
}

@Composable
internal fun ReplyContextCard(settings: Settings, post: XPostEntity) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            PostAvatar(settings = settings, post = post, size = 40.dp)
            Box(modifier = Modifier.width(2.dp).height(92.dp).background(Color(0xFFCFD9DE)))
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = post.authorName, color = XText, fontSize = 15.sp)
                if (isVerified(post)) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = XCloneIcons.Verified, contentDescription = null, tint = Color.Unspecified, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = post.authorHandle, color = XSubText, fontSize = 15.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = relativeTime(post.createAt), color = XSubText, fontSize = 15.sp)
            }

            Text(text = post.content, color = XText, fontSize = 15.sp, lineHeight = 20.sp, modifier = Modifier.padding(top = 6.dp))
            Text(text = "回复给 ${post.authorHandle} 和 @grok", color = XSubText, fontSize = 15.sp, modifier = Modifier.padding(top = 10.dp))
        }
    }
}
