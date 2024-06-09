package com.sebastianvm.musicplayer.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.model.BasicPlaylist

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
    onMoreIconClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = { Text(text = state.playlistName) },
        modifier = modifier,
        trailingContent = { OverflowIconButton(onClick = onMoreIconClicked) },
    )
}
