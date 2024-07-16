package com.sebastianvm.musicplayer.core.designsystems.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.core.model.BasicPlaylist

object PlaylistRow {
    data class State(val id: Long, val playlistName: String) {
        companion object {
            fun fromPlaylist(playlist: BasicPlaylist): State {
                return State(id = playlist.id, playlistName = playlist.name)
            }
        }
    }
}

@Composable
fun PlaylistRow(
    state: PlaylistRow.State,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    ListItem(
        headlineContent = { Text(text = state.playlistName) },
        modifier = modifier,
        trailingContent = trailingContent,
    )
}
