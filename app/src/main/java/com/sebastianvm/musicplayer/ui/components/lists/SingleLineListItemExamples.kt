package com.sebastianvm.musicplayer.ui.components.lists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

@Preview
@Composable
fun SingleLineListItemExamples() {
    ThemedPreview {
        Column {
            SingleLineListItem {
                Text(
                    text = "Simple Item",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Divider()
            SingleLineListItem(
                supportingImage = { modifier ->
                    Icon(
                        painter = painterResource(id = R.drawable.ic_album),
                        contentDescription = "",
                        modifier = modifier
                    )
                },
                supportingImageType = SupportingImageType.ICON,
                afterListContent = {
                    Text(
                        text = "3",
                        modifier = Modifier.padding(horizontal = AppDimensions.spacing.medium),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            ) {
                Text(
                    text = "Item with Icon and metadata",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Divider()
            SingleLineListItem(
                supportingImage = { modifier ->
                    Icon(
                        painter = painterResource(id = R.drawable.ic_album),
                        contentDescription = "",
                        modifier = modifier
                    )
                },
                supportingImageType = SupportingImageType.ICON
            ) {
                Text(
                    text = "Item with Icon",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Divider()
            SingleLineListItem(
                supportingImage = { modifier ->
                    Icon(
                        painter = painterResource(id = R.drawable.ic_album),
                        contentDescription = "",
                        modifier = modifier
                    )
                },
                supportingImageType = SupportingImageType.AVATAR
            ) {
                Text(
                    text = "Item with Avatar",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Divider()
            SingleLineListItem(
                supportingImage = { modifier ->
                    Icon(
                        painter = painterResource(id = R.drawable.ic_album),
                        contentDescription = "",
                        modifier = modifier
                    )
                },
                supportingImageType = SupportingImageType.LARGE
            ) {
                Text(
                    text = "Item with Large Image",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Divider()
            SingleLineListItem(
                supportingImage = { modifier ->
                    Icon(
                        painter = painterResource(id = R.drawable.ic_album),
                        contentDescription = "",
                        modifier = modifier
                    )
                },
                supportingImageType = SupportingImageType.LARGE,
                afterListContent = {
                    Text(
                        text = "3",
                        modifier = Modifier.padding(horizontal = AppDimensions.spacing.medium),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }) {
                Text(
                    text = "Item with Large Image and metadata",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}