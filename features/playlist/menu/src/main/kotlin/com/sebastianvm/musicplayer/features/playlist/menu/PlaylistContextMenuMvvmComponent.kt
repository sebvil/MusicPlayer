package com.sebastianvm.musicplayer.features.playlist.menu

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.core.data.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.core.designsystems.components.MenuItem
import com.sebastianvm.musicplayer.core.designsystems.components.Text
import com.sebastianvm.musicplayer.core.designsystems.icons.AppIcons
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.components.ContextMenu
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseMvvmComponent
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuDelegate

data class PlaylistContextMenuMvvmComponent(
    val arguments: PlaylistContextMenuArguments,
    val delegate: PlaylistContextMenuDelegate,
    val playlistRepository: PlaylistRepository,
    val playbackManager: PlaybackManager,
) :
    BaseMvvmComponent<
        PlaylistContextMenuState,
        PlaylistContextMenuUserAction,
        PlaylistContextMenuViewModel,
    >() {

    override val viewModel: PlaylistContextMenuViewModel by lazy {
        PlaylistContextMenuViewModel(
            arguments = arguments,
            playlistRepository = playlistRepository,
            playbackManager = playbackManager,
            delegate = delegate,
        )
    }

    @Composable
    override fun Content(
        state: PlaylistContextMenuState,
        handle: Handler<PlaylistContextMenuUserAction>,
        modifier: Modifier,
    ) {
        PlaylistContextMenu(state = state, handle = handle, modifier = modifier)
    }
}

@Composable
private fun PlaylistContextMenu(
    state: PlaylistContextMenuState,
    handle: Handler<PlaylistContextMenuUserAction>,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is PlaylistContextMenuState.Data -> {
            ContextMenu(menuTitle = state.playlistName, modifier = modifier) {
                LazyColumn {
                    item {
                        MenuItem(
                            text = stringResource(id = RString.play_all_songs),
                            icon = AppIcons.PlayArrow.icon(),
                            onItemClick = {
                                handle(PlaylistContextMenuUserAction.PlayPlaylistClicked)
                            },
                        )
                    }

                    item {
                        MenuItem(
                            text = stringResource(id = RString.delete_playlist),
                            icon = AppIcons.Delete.icon(),
                            onItemClick = {
                                handle(PlaylistContextMenuUserAction.DeletePlaylistClicked)
                            },
                        )
                    }
                }
            }

            if (state.showDeleteConfirmationDialog) {
                DeletePlaylistConfirmationDialog(playlistName = state.playlistName, handle = handle)
            }
        }
        is PlaylistContextMenuState.Loading -> {
            ContextMenu(menuTitle = stringResource(id = RString.loading), modifier = modifier) {}
        }
    }
}

@Composable
fun DeletePlaylistConfirmationDialog(
    playlistName: String,
    handle: Handler<PlaylistContextMenuUserAction>,
) {
    AlertDialog(
        onDismissRequest = { handle(PlaylistContextMenuUserAction.PlaylistDeletionCancelled) },
        confirmButton = {
            TextButton(
                onClick = { handle(PlaylistContextMenuUserAction.ConfirmPlaylistDeletionClicked) }
            ) {
                Text(text = stringResource(RString.delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { handle(PlaylistContextMenuUserAction.PlaylistDeletionCancelled) }
            ) {
                Text(text = stringResource(RString.cancel))
            }
        },
        title = { Text(text = stringResource(id = RString.delete_this_playlist, playlistName)) },
        text = { Text(text = stringResource(id = RString.sure_you_want_to_delete, playlistName)) },
    )
}
