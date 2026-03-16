package me.rerere.rikkahub.ui.components.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary

private val CardGroupCorner = 26.dp
private val CardGroupItemSpacing = 2.dp
private val SettingsPageBackgroundColor = Color(0xFFFFFFFF)
private val SettingsItemContainerColor = Color(0xFFF1F1F1)
private val SettingsItemPressedColor = Color(0xFFE5E5E5)
private val SettingsGroupTitleColor = Color(0xFF6B6B6B)

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
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = item.onClick != null && interactionSource.collectIsPressedAsState().value
    val shape = RoundedCornerShape(6.dp)

    Box(
        modifier = item.modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                if (isPressed) SettingsItemPressedColor else SettingsItemContainerColor,
                shape
            )
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 54.dp)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.leadingContent != null) {
                Box(
                    modifier = Modifier.size(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    item.leadingContent.invoke()
                }
                Spacer(modifier = Modifier.size(14.dp))
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    if (item.overlineContent != null || item.supportingContent != null) 2.dp else 0.dp
                )
            ) {
                item.overlineContent?.let { overline ->
                    CompositionLocalProvider(
                        LocalTextStyle provides TextStyle(
                            fontFamily = SourceSans3,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = SettingsGroupTitleColor
                        )
                    ) {
                        overline()
                    }
                }
                CompositionLocalProvider(
                    LocalTextStyle provides TextStyle(
                        fontFamily = SourceSans3,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = ZionTextPrimary
                    )
                ) {
                    item.headlineContent()
                }
                item.supportingContent?.let { supporting ->
                    CompositionLocalProvider(
                        LocalTextStyle provides TextStyle(
                            fontFamily = SourceSans3,
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp,
                            color = SettingsGroupTitleColor
                        )
                    ) {
                        supporting()
                    }
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            when {
                item.trailingContent != null -> item.trailingContent.invoke()
                item.onClick != null -> DefaultCardGroupChevron()
            }
        }
    }
}

@Composable
private fun DefaultCardGroupChevron(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = ZionAppIcons.ChevronRight,
            contentDescription = null,
            tint = ZionTextSecondary,
            modifier = Modifier.size(size)
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
                    color = SettingsGroupTitleColor,
                    letterSpacing = 0.4.sp
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
                .background(SettingsPageBackgroundColor)
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
        containerColor = SettingsPageBackgroundColor,
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
