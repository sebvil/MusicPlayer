package com.sebastianvm.musicplayer.ui.components

import android.content.res.Configuration
import android.net.Uri
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
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.ui.components.lists.DoubleLineListItem
import com.sebastianvm.musicplayer.ui.components.lists.SupportingImageType
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview
import com.sebastianvm.musicplayer.util.uri.UriUtils


data class AlbumRowState(
    val albumId: Long,
    val albumName: String,
    val imageUri: Uri,
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
fun AlbumRow(
    state: AlbumRowState,
    modifier: Modifier = Modifier,
    onOverflowMenuIconClicked: () -> Unit
) {

    with(state) {
        DoubleLineListItem(
            modifier = modifier,
            supportingImage = { imageModifier ->
                MediaArtImage(
                    uri = imageUri,
                    contentDescription = stringResource(
                        id = R.string.album_art_for_album,
                        albumName
                    ),
                    backupResource = R.drawable.ic_album,
                    backupContentDescription = R.string.placeholder_album_art,
                    modifier = imageModifier
                )
            },
            supportingImageType = SupportingImageType.LARGE,
            afterListContent = {
                IconButton(
                    onClick = onOverflowMenuIconClicked,
                    modifier = Modifier.padding(end = AppDimensions.spacing.xSmall)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_overflow),
                        contentDescription = stringResource(id = R.string.more)
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


fun Album.toAlbumRowState(): AlbumRowState {
    return AlbumRowState(
        albumId = albumId,
        albumName = albumName,
        imageUri = UriUtils.getAlbumUri(albumId = albumId),
        year = year,
        artists = artists
    )

}

class AlbumRowStateProvider : PreviewParameterProvider<AlbumRowState> {
    override val values =
        sequenceOf(
            AlbumRowState(
                albumId = 1,
                albumName = "Ahora",
                imageUri = Uri.EMPTY,
                year = 2017,
                artists = "Melendi"

            ),
            AlbumRowState(
                albumId = 2,
                albumName = "VIVES",
                imageUri = Uri.EMPTY,
                year = 2017,
                artists = "Carlos Vives"
            ),
        )
}
