package me.rerere.rikkahub.ui.components.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionAccentNeutral
import me.rerere.rikkahub.ui.theme.ZionSectionItem
import me.rerere.rikkahub.ui.theme.ZionTextPrimary

@Composable
fun ToggleSurface(
    checked: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(50),
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (checked) ZionAccentNeutral else ZionSectionItem,
        contentColor = if (checked) Color.White else ZionTextPrimary,
        modifier = modifier,
        shape = shape,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        ProvideTextStyle(
            TextStyle(
                fontFamily = SourceSans3,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = if (checked) Color.White else ZionTextPrimary
            )
        ) {
            content()
        }
    }
}
