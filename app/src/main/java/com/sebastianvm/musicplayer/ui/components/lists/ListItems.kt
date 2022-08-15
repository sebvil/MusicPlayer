package com.sebastianvm.musicplayer.ui.components.lists

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions

enum class SupportingImageType(val imageSize: Dp, val paddingEnd: Dp) {
    ICON(24.dp, 32.dp),
    AVATAR(40.dp, 16.dp),
    LARGE(56.dp, 16.dp)
}

@Composable
fun SingleLineListItem(
    modifier: Modifier = Modifier,
    supportingImage: (@Composable (Modifier) -> Unit)? = null,
    supportingImageType: SupportingImageType? = null,
    afterListContent: (@Composable () -> Unit)? = null,
    text: @Composable RowScope.() -> Unit,
) {
    val rowHeight = when (supportingImageType) {
        null -> 48.dp
        SupportingImageType.ICON, SupportingImageType.AVATAR -> 56.dp
        SupportingImageType.LARGE -> 72.dp
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(rowHeight)
            .padding(start = AppDimensions.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (supportingImage != null) {
            if (supportingImageType == null) {
                throw IllegalStateException("supportingImage without supportingImageType")
            }
            supportingImage(
                Modifier
                    .padding(end = supportingImageType.paddingEnd)
                    .size(supportingImageType.imageSize)
            )
        }

        text()

        if (afterListContent != null) {
            afterListContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListItem(
    headlineText: String,
    supportingText: String? = null,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    val textColor = contentColorFor(backgroundColor = backgroundColor)
    ListItem(
        headlineText = {
            Text(
                text = headlineText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        modifier = modifier,
        supportingText = supportingText?.let {
            {
                Text(
                    text = supportingText,
                    modifier = Modifier.alpha(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        colors = ListItemDefaults.colors(
            containerColor = backgroundColor,
            headlineColor = textColor,
            supportingColor = textColor,
            trailingIconColor = textColor,
            leadingIconColor = textColor
        )
    )
}
