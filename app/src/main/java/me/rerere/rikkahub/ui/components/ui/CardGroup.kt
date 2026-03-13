package me.rerere.rikkahub.ui.components.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionBackground
import me.rerere.rikkahub.ui.theme.ZionSectionItem
import me.rerere.rikkahub.ui.theme.ZionSurface
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary

private val CardGroupCorner = 26.dp
private val CardGroupItemSpacing = 2.dp
private val CardGroupInnerCorner = 10.dp

data class CardGroupItem(
    val onClick: (() -> Unit)?,
    val modifier: Modifier,
    val overlineContent: (@Composable () -> Unit)?,
    val headlineContent: @Composable () -> Unit,
    val supportingContent: (@Composable () -> Unit)?,
    val leadingContent: (@Composable () -> Unit)?,
    val trailingContent: (@Composable () -> Unit)?,
    val colors: ListItemColors?,
)

class CardGroupScope {
    internal val items = mutableListOf<CardGroupItem>()

    fun item(
        onClick: (() -> Unit)? = null,
        modifier: Modifier = Modifier,
        overlineContent: (@Composable () -> Unit)? = null,
        supportingContent: (@Composable () -> Unit)? = null,
        leadingContent: (@Composable () -> Unit)? = null,
        trailingContent: (@Composable () -> Unit)? = null,
        colors: ListItemColors? = null,
        headlineContent: @Composable () -> Unit,
    ) {
        items.add(
            CardGroupItem(
                onClick = onClick,
                modifier = modifier,
                overlineContent = overlineContent,
                headlineContent = headlineContent,
                supportingContent = supportingContent,
                leadingContent = leadingContent,
                trailingContent = trailingContent,
                colors = colors,
            )
        )
    }
}

@Composable
private fun CardGroupListItem(
    item: CardGroupItem,
    count: Int,
    index: Int,
) {
    val isFirst = index == 0
    val isLast = index == count - 1

    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(
        topStart = if (count == 1 || isFirst) CardGroupCorner else CardGroupInnerCorner,
        topEnd = if (count == 1 || isFirst) CardGroupCorner else CardGroupInnerCorner,
        bottomStart = if (count == 1 || isLast) CardGroupCorner else CardGroupInnerCorner,
        bottomEnd = if (count == 1 || isLast) CardGroupCorner else CardGroupInnerCorner,
    )

    Box(
        modifier = item.modifier
            .fillMaxWidth()
            .clip(shape)
            .background(ZionSectionItem, shape)
            .then(
                if (item.onClick != null) {
                    Modifier.pressableScale(
                        interactionSource = interactionSource,
                        onClick = item.onClick
                    )
                } else {
                    Modifier
                }
            )
    ) {
        ListItem(
            headlineContent = {
                androidx.compose.material3.ProvideTextStyle(
                    TextStyle(
                        fontFamily = SourceSans3,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = ZionTextPrimary
                    )
                ) {
                    item.headlineContent()
                }
            },
            overlineContent = item.overlineContent?.let { content ->
                {
                    androidx.compose.material3.ProvideTextStyle(
                        TextStyle(
                            fontFamily = SourceSans3,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = ZionTextSecondary
                        )
                    ) {
                        content()
                    }
                }
            },
            supportingContent = item.supportingContent?.let { content ->
                {
                    androidx.compose.material3.ProvideTextStyle(
                        TextStyle(
                            fontFamily = SourceSans3,
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp,
                            color = ZionTextSecondary
                        )
                    ) {
                        content()
                    }
                }
            },
            leadingContent = item.leadingContent,
            trailingContent = item.trailingContent,
            colors = item.colors ?: ListItemDefaults.colors(
                containerColor = Color.Transparent,
                headlineColor = ZionTextPrimary,
                supportingColor = ZionTextSecondary,
                overlineColor = ZionTextSecondary,
                leadingIconColor = ZionTextSecondary,
                trailingIconColor = ZionTextSecondary
            ),
        )
    }
}

@Composable
fun CardGroup(
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)? = null,
    content: @Composable CardGroupScope.() -> Unit,
) {
    val scope = CardGroupScope()
    scope.content()

    Column(modifier = modifier) {
        if (title != null) {
            androidx.compose.material3.ProvideTextStyle(
                TextStyle(
                    fontFamily = SourceSans3,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = ZionTextSecondary
                )
            ) {
                Box(modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 8.dp)) {
                    title()
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CardGroupCorner))
                .background(ZionSurface)
        ) {
            Column {
                val count = scope.items.size
                scope.items.fastForEachIndexed { index, item ->
                    CardGroupListItem(item = item, count = count, index = index)
                    if (index != count - 1) {
                        Spacer(modifier = Modifier.height(CardGroupItemSpacing))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CardGroupPreview() {
    Scaffold(
        containerColor = ZionBackground,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            CardGroup(
                modifier = Modifier.padding(horizontal = 16.dp),
                title = { Text("ABOUT") },
            ) {
                item(
                    headlineContent = { Text("第一项") },
                )
                item(
                    headlineContent = { Text("第二项") },
                    supportingContent = { Text("支持文本") },
                )
                item(
                    onClick = {},
                    headlineContent = { Text("第三项") },
                    trailingContent = { Text("→") },
                )
            }
        }
    }
}
