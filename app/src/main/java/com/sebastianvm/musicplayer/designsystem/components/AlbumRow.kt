package com.sebastianvm.musicplayer.designsystem.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.model.AlbumWithArtists
import com.sebastianvm.musicplayer.ui.components.MediaArtImage

object AlbumRow {
    data class State(
        val id: Long,
        val albumName: String,
        val artists: String? = null,
        val artworkUri: String,
    ) {
        companion object {
            fun fromAlbum(album: AlbumWithArtists): State {
                return State(
                    id = album.id,
                    albumName = album.title,
                    artists = album.artists.joinToString { it.name },
                    artworkUri = album.imageUri,
                )
            }
        }
    }
}

@Composable
fun AlbumRow(
    state: AlbumRow.State,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    ListItem(
        headlineContent = { Text(text = state.albumName) },
        supportingContent = state.artists?.let { artists -> { Text(text = artists) } },
        modifier = modifier,
        leadingContent = {
            MediaArtImage(artworkUri = state.artworkUri, modifier = Modifier.size(56.dp))
        },
        trailingContent = trailingContent,
    )
}
