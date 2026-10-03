package me.rerere.rikkahub.ui.components.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionGrayLight
import me.rerere.rikkahub.ui.theme.ZionSectionItem
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary

/** 表单区块小标题 (设计稿: 灰色小标签) */
@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = SourceSans3,
        color = ZionTextSecondary,
        modifier = modifier.padding(start = 4.dp, top = 14.dp, bottom = 7.dp)
    )
}

/** 胶囊输入框 (设计稿: 圆角药丸形字段) */
@Composable
fun PillInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        textStyle = TextStyle(
            fontSize = 16.sp,
            fontFamily = SourceSans3,
            color = ZionTextPrimary
        ),
        cursorBrush = SolidColor(ZionTextPrimary),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(ZionSectionItem)
    ) { innerTextField ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty() && placeholder.isNotEmpty()) {
                Text(
                    text = placeholder,
                    fontSize = 16.sp,
                    fontFamily = SourceSans3,
                    color = ZionTextSecondary
                )
            }
            innerTextField()
        }
    }
}

/** 胶囊开关 (设计稿: 黑色轨道圆角开关) */
@Composable
fun PillToggle(
    checked: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val knobStart by animateDpAsState(
        targetValue = if (checked) 24.dp else 4.dp,
        animationSpec = tween(durationMillis = 200),
        label = "toggleKnob"
    )
    Box(
        modifier = modifier
            .width(50.dp)
            .height(30.dp)
            .clip(CircleShape)
            .background(if (checked) ZionTextPrimary else ZionGrayLight, CircleShape)
            .pressableScale(pressedScale = 0.94f) { onChange(!checked) },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .padding(start = knobStart)
                .size(22.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}
