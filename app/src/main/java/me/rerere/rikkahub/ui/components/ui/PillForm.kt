package me.rerere.rikkahub.ui.components.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionSurface
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary

/**
 * 药丸表单规格 (设计稿: 698px 画布 ÷ 2)
 *
 * - 字段高度: 109px / 2 = 54.5dp
 * - 字段圆角: 44px / 2 = 22dp
 * - 字段底色: #fff, 无描边
 * - Enable 卡片: 高 62dp, 圆角 22.5dp
 * - 标签: 14sp / #5d5d5d
 */
object PillSpec {
    /** 字段高度 (设计稿 .field) */
    val FieldHeight = 54.5.dp

    /** 字段圆角 (设计稿 .field border-radius: 44px) */
    val FieldRadius = 22.dp

    /** 字段左右内边距 (设计稿 padding: 0 35px) */
    val FieldPadding = 17.5.dp

    /** 字段字号 (设计稿 font-size: 34px) */
    val FieldFontSize = 17.sp

    /** 标签字号 (设计稿 .label font-size: 28px) */
    val LabelFontSize = 14.sp

    /** Enable 卡片高度 (设计稿 .enable height: 124px) */
    val CardHeight = 62.dp

    /** Enable 卡片圆角 (设计稿 .enable border-radius: 45px) */
    val CardRadius = 22.5.dp
}

/** 表单区块小标题 (设计稿: .label 灰色 14sp 标签) */
@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        fontSize = PillSpec.LabelFontSize,
        fontFamily = SourceSans3,
        color = ZionTextSecondary,
        modifier = modifier.padding(start = 4.5.dp, top = 15.5.dp, bottom = 7.5.dp)
    )
}

/**
 * 药丸输入框 (设计稿: .field)
 *
 * 高 54.5dp / 圆角 22dp / 纯白底 / 无描边, 与药丸选择器完全同规格。
 */
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
            fontSize = PillSpec.FieldFontSize,
            fontFamily = SourceSans3,
            color = ZionTextPrimary
        ),
        cursorBrush = SolidColor(ZionTextPrimary),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier
            .height(PillSpec.FieldHeight)
            .clip(RoundedCornerShape(PillSpec.FieldRadius))
            .background(ZionSurface, RoundedCornerShape(PillSpec.FieldRadius))
    ) { innerTextField ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = PillSpec.FieldPadding),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty() && placeholder.isNotEmpty()) {
                Text(
                    text = placeholder,
                    fontSize = PillSpec.FieldFontSize,
                    fontFamily = SourceSans3,
                    color = ZionTextSecondary
                )
            }
            innerTextField()
        }
    }
}

/**
 * 药丸选择器 (设计稿: .field select)
 *
 * 与 [PillInput] 完全同规格: 高 54.5dp / 圆角 22dp / 纯白底 / **无描边**,
 * 右侧为设计稿的 chevron 下拉箭头。
 */
@Composable
fun <T> PillSelect(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    optionToString: @Composable (T) -> String = { it.toString() },
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(PillSpec.FieldHeight)
                .clip(RoundedCornerShape(PillSpec.FieldRadius))
                .background(ZionSurface, RoundedCornerShape(PillSpec.FieldRadius))
                .pressableScale(enabled = enabled, pressedScale = 0.985f) { expanded = true }
                .padding(start = PillSpec.FieldPadding, end = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = optionToString(selectedOption),
                fontSize = PillSpec.FieldFontSize,
                fontFamily = SourceSans3,
                color = ZionTextPrimary,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = chevronDownIcon(),
                contentDescription = "expand",
                tint = ZionTextSecondary,
                modifier = Modifier
                    .size(16.dp)
                    .pressableScale(enabled = enabled, pressedScale = 0.985f) { expanded = true }
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = ZionSurface,
            shadowElevation = 10.dp
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    text = {
                        Text(
                            text = optionToString(option),
                            maxLines = 1,
                            fontFamily = SourceSans3,
                            fontSize = PillSpec.FieldFontSize,
                            color = ZionTextPrimary
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ZionSurface)
                )
            }
        }
    }
}

/**
 * 向下箭头图标 (设计稿: .chev, viewBox 0 0 24 14, stroke-width 3.5)
 */
@Composable
private fun chevronDownIcon(): ImageVector = remember {
    ImageVector.Builder(
        name = "ChevronDown",
        defaultWidth = 11.dp,
        defaultHeight = 7.dp,
        viewportWidth = 24f,
        viewportHeight = 14f
    ).apply {
        path(fill = SolidColor(Color.Transparent)) {
            moveTo(3f, 3f)
            lineTo(12f, 11f)
            lineTo(21f, 3f)
        }
        path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 3.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(3f, 3f)
            lineTo(12f, 11f)
            lineTo(21f, 3f)
        }
    }.build()
}

/** 药丸开关 (设计稿: .toggle 黑色轨道 + 白色圆形按钮) */
@Composable
fun PillToggle(
    checked: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val knobStart by animateDpAsState(
        targetValue = if (checked) 25.5.dp else 4.5.dp,
        animationSpec = tween(durationMillis = 200),
        label = "toggleKnob"
    )
    Box(
        modifier = modifier
            .width(52.dp)
            .height(31.dp)
            .clip(CircleShape)
            .background(if (checked) ZionTextPrimary else Color(0xFFCFCFCF), CircleShape)
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