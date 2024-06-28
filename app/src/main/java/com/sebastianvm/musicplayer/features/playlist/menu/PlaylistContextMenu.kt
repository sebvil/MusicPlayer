package com.sebastianvm.musicplayer.features.playlist.menu

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.designsystem.components.Text
import com.sebastianvm.musicplayer.designsystem.icons.AppIcons
import com.sebastianvm.musicplayer.di.Dependencies
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.ui.ContextMenu
import com.sebastianvm.musicplayer.ui.MenuItem
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.util.resources.RString

data class PlaylistContextMenu(
    override val arguments: PlaylistContextMenuArguments,
    val delegate: PlaylistContextMenuDelegate,
) :
    BaseUiComponent<
        PlaylistContextMenuArguments,
        PlaylistContextMenuState,
        PlaylistContextMenuUserAction,
        PlaylistContextMenuStateHolder,
    >() {

    override fun createStateHolder(dependencies: Dependencies): PlaylistContextMenuStateHolder {
        return PlaylistContextMenuStateHolder(
            arguments = arguments,
            playlistRepository = dependencies.repositoryProvider.playlistRepository,
            playbackManager = dependencies.repositoryProvider.playbackManager,
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
                onClick = {
                    handle(PlaylistContextMenuUserAction.ConfirmPlaylistDeletionClicked)
                }) {
                    Text(text = stringResource(RString.delete))
                }
        },
        dismissButton = {
            TextButton(
                onClick = { handle(PlaylistContextMenuUserAction.PlaylistDeletionCancelled) }) {
                    Text(text = stringResource(RString.cancel))
                }
        },
        title = { Text(text = stringResource(id = RString.delete_this_playlist, playlistName)) },
        text = { Text(text = stringResource(id = RString.sure_you_want_to_delete, playlistName)) },
    )
}
