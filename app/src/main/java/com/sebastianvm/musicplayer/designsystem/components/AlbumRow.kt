package com.sebastianvm.musicplayer.designsystem.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.designsystem.icons.Album
import com.sebastianvm.musicplayer.designsystem.icons.Icons
import com.sebastianvm.musicplayer.model.Album
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState

object AlbumRow {
    data class State(
        val id: Long,
        val albumName: String,
        val artists: String? = null,
        val mediaArtImageState: MediaArtImageState,
    ) {
        companion object {
            fun fromAlbum(album: Album): State {
                return State(
                    id = album.id,
                    albumName = album.title,
                    artists = album.artists.joinToString { it.name },
                    mediaArtImageState =
                        MediaArtImageState(imageUri = album.imageUri, backupImage = Icons.Album),
                )
            }
        }
    }
}

@Composable
fun AlbumRow(state: AlbumRow.State, onMoreIconClicked: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = { Text(text = state.albumName) },
        supportingContent = state.artists?.let { artists -> { Text(text = artists) } },
        modifier = modifier,
        leadingContent = {
            MediaArtImage(
                mediaArtImageState = state.mediaArtImageState,
                modifier = Modifier.size(56.dp),
            )
        },
        trailingContent = { OverflowIconButton(onClick = onMoreIconClicked) },
    )
}
