package com.sebastianvm.musicplayer.ui.components.lists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions

enum class SupportingImageType(val imageSize: Dp, val paddingEnd: Dp) {
    ICON(24.dp, 32.dp),
    AVATAR(40.dp, 16.dp),
    LARGE(56.dp, 16.dp)
}

@Composable
fun SingleLineListItem(
    text: DisplayableString,
    modifier: Modifier = Modifier,
    supportingImage: (@Composable (Modifier) -> Unit)? = null,
    supportingImageType: SupportingImageType? = null,
    metadata: DisplayableString? = null,
    onClick: () -> Unit = {}
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
            .clickable { onClick() }
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

        Text(
            text = text.getString(),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        if (metadata != null) {
            Text(
                text = metadata.getString(),
                modifier = Modifier.padding(horizontal = AppDimensions.spacing.medium),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }

}