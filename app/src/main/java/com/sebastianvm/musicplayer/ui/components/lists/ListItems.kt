package com.sebastianvm.musicplayer.ui.components.lists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun DoubleLineListItem(
    modifier: Modifier = Modifier,
    supportingImage: (@Composable RowScope.(Modifier) -> Unit)? = null,
    supportingImageType: SupportingImageType? = null,
    afterListContent: (@Composable RowScope.() -> Unit)? = null,
    secondaryText: @Composable ColumnScope.() -> Unit,
    primaryText: @Composable ColumnScope.() -> Unit,
) {
    val rowHeight = supportingImageType?.let { 72.dp } ?: 64.dp
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
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(end = AppDimensions.spacing.medium)
        ) {
            primaryText()
            secondaryText()
        }

        if (afterListContent != null) {
            afterListContent()
        }
    }
}
