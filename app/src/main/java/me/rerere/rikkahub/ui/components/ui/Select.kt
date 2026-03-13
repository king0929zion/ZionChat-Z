package me.rerere.rikkahub.ui.components.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.ArrowDown01
import me.rerere.hugeicons.stroke.ArrowUp01
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionGrayLight
import me.rerere.rikkahub.ui.theme.ZionSectionItem
import me.rerere.rikkahub.ui.theme.ZionSurface
import me.rerere.rikkahub.ui.theme.ZionTextPrimary

@Composable
fun <T> Select(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    optionToString: @Composable (T) -> String = { it.toString() },
    optionLeading: @Composable ((T) -> Unit)? = null,
    leading: @Composable () -> Unit = {},
    trailing: @Composable () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Surface(
            color = ZionSurface,
            tonalElevation = 0.dp,
            shadowElevation = 6.dp,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, ZionGrayLight)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ZionSurface)
                    .pressableScale { expanded = true }
                    .padding(vertical = 10.dp, horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                leading()
                Text(
                    text = optionToString(selectedOption),
                    fontFamily = SourceSans3,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = ZionTextPrimary,
                    modifier = Modifier.weight(1f)
                )
                trailing()
                Icon(
                    imageVector = if (expanded) HugeIcons.ArrowUp01 else HugeIcons.ArrowDown01,
                    contentDescription = "expand",
                    tint = ZionTextPrimary
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = ZionSurface,
            shadowElevation = 10.dp
        ) {
            options.fastForEach { option ->
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
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                            color = ZionTextPrimary
                        )
                    },
                    leadingIcon = optionLeading?.let {
                        { it(option) }
                    },
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ZionSectionItem)
                )
            }
        }
    }
}
