package com.sebastianvm.musicplayer.features.playlist.menu

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.ContextMenu
import com.sebastianvm.musicplayer.ui.MenuItem
import com.sebastianvm.musicplayer.ui.icons.Delete
import com.sebastianvm.musicplayer.ui.icons.Icons
import com.sebastianvm.musicplayer.ui.icons.PlayArrow
import com.sebastianvm.musicplayer.ui.icons.Playlist
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState

@Composable
fun PlaylistContextMenu(
    stateHolder: PlaylistContextMenuStateHolder,
    modifier: Modifier = Modifier
) {
    val state by stateHolder.currentState
    PlaylistContextMenu(state = state, handle = stateHolder::handle, modifier = modifier)
}

@Composable
private fun PlaylistContextMenu(
    state: PlaylistContextMenuState,
    handle: Handler<PlaylistContextMenuUserAction>,
    modifier: Modifier = Modifier
) {
    when (state) {
        is PlaylistContextMenuState.Data -> {
            ContextMenu(menuTitle = state.playlistName, modifier = modifier) {
                LazyColumn {
                    item {
                        MenuItem(
                            text = stringResource(id = R.string.play_all_songs),
                            icon = Icons.PlayArrow.icon(),
                            onItemClicked = {
                                handle(PlaylistContextMenuUserAction.PlayPlaylistClicked)
                            }
                        )
                    }

                    item {
                        MenuItem(
                            text = stringResource(id = R.string.view_playlist),
                            icon = Icons.Playlist.icon(),
                            onItemClicked = {
                                handle(PlaylistContextMenuUserAction.ViewPlaylistClicked)
                            }
                        )
                    }

                    item {
                        MenuItem(
                            text = stringResource(id = R.string.delete_playlist),
                            icon = Icons.Delete.icon(),
                            onItemClicked = {
                                handle(PlaylistContextMenuUserAction.DeletePlaylistClicked)
                            }
                        )
                    }
                }
            }

            if (state.showDeleteConfirmationDialog) {
                DeletePlaylistConfirmationDialog(
                    playlistName = state.playlistName,
                    handle = handle
                )
            }
        }

        is PlaylistContextMenuState.Loading -> {
            ContextMenu(menuTitle = stringResource(id = R.string.loading), modifier = modifier) {}
        }
    }
}

@Composable
fun DeletePlaylistConfirmationDialog(
    playlistName: String,
    handle: Handler<PlaylistContextMenuUserAction>
) {
    AlertDialog(
        onDismissRequest = { handle(PlaylistContextMenuUserAction.PlaylistDeletionCancelled) },
        confirmButton = {
            TextButton(onClick = { handle(PlaylistContextMenuUserAction.ConfirmPlaylistDeletionClicked) }) {
                Text(text = stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = { handle(PlaylistContextMenuUserAction.PlaylistDeletionCancelled) }) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        title = {
            Text(text = stringResource(id = R.string.delete_this_playlist, playlistName))
        },
        text = {
            Text(text = stringResource(id = R.string.sure_you_want_to_delete, playlistName))
        }
    )
}
