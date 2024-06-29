package com.sebastianvm.musicplayer.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.core.model.Artist
import com.sebastianvm.musicplayer.core.model.BasicArtist

object ArtistRow {
    data class State(val id: Long, val artistName: String) {

        companion object {
            fun fromArtist(artist: Artist): State {
                return State(id = artist.id, artistName = artist.name)
            }

            fun fromArtist(artist: BasicArtist): State {
                return State(id = artist.id, artistName = artist.name)
            }
        }
    }
}

@Composable
fun ArtistRow(
    state: ArtistRow.State,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    ListItem(
        headlineContent = { Text(text = state.artistName) },
        modifier = modifier,
        trailingContent = trailingContent,
    )
}
