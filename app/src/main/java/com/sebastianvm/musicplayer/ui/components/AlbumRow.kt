package com.sebastianvm.musicplayer.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.commons.R
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.commons.util.MediaArt
import com.sebastianvm.musicplayer.ui.components.lists.DoubleLineListItem
import com.sebastianvm.musicplayer.ui.components.lists.SupportingImageType
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview


data class AlbumRowState(
    val albumName: String,
    val image: MediaArt,
    val year: Long,
    val artists: String,
)

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AlbumRowPreview(@PreviewParameter(AlbumRowStateProvider::class) state: AlbumRowState) {
    ThemedPreview {
        AlbumRow(state = state) {}
    }
}

@Composable
fun AlbumRow(state: AlbumRowState, modifier: Modifier = Modifier, onOverflowMenuIconClicked: () -> Unit) {
    with(state) {
        DoubleLineListItem(
            modifier = modifier,
            supportingImage = { modifier ->
                MediaArtImage(
                    image = state.image,
                    modifier = modifier
                )
            },
            supportingImageType = SupportingImageType.LARGE,
            afterListContent = {
                IconButton(
                    onClick = onOverflowMenuIconClicked,
                    modifier = Modifier.padding(end = AppDimensions.spacing.xSmall)
                ) {
                    Icon(
                        painter = painterResource(id = com.sebastianvm.musicplayer.R.drawable.ic_overflow),
                        contentDescription = stringResource(com.sebastianvm.musicplayer.R.string.more)
                    )
                }
            },
            secondaryText = {
                Row {
                    if (year != 0L) {
                        Text(
                            text = year.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .paddingFromBaseline(top = AppDimensions.spacing.mediumLarge)
                                .padding(end = AppDimensions.spacing.small)
                        )
                    }
                    Text(
                        text = artists,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.paddingFromBaseline(top = AppDimensions.spacing.mediumLarge)
                    )
                }
            }) {
            Text(
                text = albumName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.paddingFromBaseline(top = AppDimensions.spacing.xxLarge)
            )
        }
    }
}


class AlbumRowStateProvider : PreviewParameterProvider<AlbumRowState> {
    override val values =
        sequenceOf(
            AlbumRowState(
                albumName = "Ahora",
                image = MediaArt(
                    uris = listOf(),
                    contentDescription = DisplayableString.StringValue(""),
                    backupResource = R.drawable.ic_album,
                    backupContentDescription = DisplayableString.StringValue("Album art placeholder")
                ),
                year = 2017,
                artists = "Melendi"

            ),
            AlbumRowState(
                albumName = "VIVES",
                image = MediaArt(
                    uris = listOf(),
                    contentDescription = DisplayableString.StringValue(""),
                    backupResource = R.drawable.ic_album,
                    backupContentDescription = DisplayableString.StringValue("Album art placeholder")
                ),
                year = 2017,
                artists = "Carlos Vives"
            ),
        )
}