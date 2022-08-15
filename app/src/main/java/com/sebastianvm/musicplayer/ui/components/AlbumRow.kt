package com.sebastianvm.musicplayer.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.sebastianvm.commons.R
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.ui.components.lists.ListItem
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview
import com.sebastianvm.musicplayer.util.uri.UriUtils


data class AlbumRowState(
    val albumId: Long,
    val albumName: String,
    val imageUri: String,
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
        ListItem(
            headlineText = albumName,
            supportingText = if (year != 0L) {
                "$year $artists"
            } else {
                artists
            },
            modifier = modifier,
            leadingContent = {
                MediaArtImage(
                    uri = imageUri,
                    contentDescription = stringResource(
                        id = R.string.album_art_for_album,
                        albumName
                    ),
                    backupResource = R.drawable.ic_album,
                    backupContentDescription = R.string.placeholder_album_art,
                    modifier = Modifier.size(56.dp)
                )
            },
            trailingContent = {
                IconButton(
                    onClick = onOverflowMenuIconClicked,
                    modifier = Modifier.padding(end = AppDimensions.spacing.xSmall)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_overflow),
                        contentDescription = stringResource(id = R.string.more)
                    )
                }
            }
        )
    }
}


fun Album.toAlbumRowState(): AlbumRowState {
    return AlbumRowState(
        albumId = id,
        albumName = albumName,
        imageUri = UriUtils.getAlbumUriString(albumId = id),
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
                imageUri = "",
                year = 2017,
                artists = "Melendi"

            ),
            AlbumRowState(
                albumId = 2,
                albumName = "VIVES",
                imageUri = "",
                year = 2017,
                artists = "Carlos Vives"
            ),
        )
}
