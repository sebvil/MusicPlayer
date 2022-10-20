package com.sebastianvm.musicplayer.ui.library.playlistlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.util.compose.ScreenLayout
import com.sebastianvm.musicplayer.ui.util.mvvm.DefaultScreenDelegateProvider
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate

@Composable
fun PlaylistListScreen(
    state: PlaylistListState,
    screenDelegate: ScreenDelegate<PlaylistListUserAction> = DefaultScreenDelegateProvider.getDefaultInstance()
) {
    ScreenLayout(
        fab = {
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(id = R.string.new_playlist)) },
                onClick = { screenDelegate.handle(PlaylistListUserAction.AddPlaylistButtonClicked) },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_plus),
                        contentDescription = stringResource(id = R.string.new_playlist)
                    )
                })
        },
        topBar = {
            LibraryTopBar(
                title = stringResource(id = R.string.playlists),
                delegate = object : LibraryTopBarDelegate {
                    override fun sortByClicked() {
                        screenDelegate.handle(PlaylistListUserAction.SortByButtonClicked)
                    }

                    override fun upButtonClicked() {
                        screenDelegate.handle(PlaylistListUserAction.UpButtonClicked)
                    }
                })
        }) {
        PlaylistListLayout(state = state, screenDelegate = screenDelegate)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistDialog(onDismiss: () -> Unit, onConfirm: (playlistName: String) -> Unit) {
    var playListName by rememberSaveable {
        mutableStateOf("")
    }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onConfirm(playListName) }) {
                Text(text = "Create")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "Cancel")
            }
        },
        title = {
            Text(text = "Playlist name")
        },
        text = {
            TextField(
                value = playListName,
                onValueChange = { newValue -> playListName = newValue },
            )
        }
    )
}

@Composable
fun PlaylistCreationErrorDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "Ok")
            }
        },
        title = {
            Text(text = "Error creating playlist")
        },
        text = {
            Text(text = "A playlist with that name already exists.")

        }
    )
}


@Composable
fun PlaylistListLayout(
    state: PlaylistListState,
    screenDelegate: ScreenDelegate<PlaylistListUserAction>
) {
    if (state.isCreatePlaylistDialogOpen) {
        CreatePlaylistDialog(
            onDismiss = { screenDelegate.handle(PlaylistListUserAction.DismissPlaylistCreationButtonClicked) },
            onConfirm = { playlistName ->
                screenDelegate.handle(
                    PlaylistListUserAction.CreatePlaylistButtonClicked(
                        playlistName
                    )
                )
            }
        )
    }

    if (state.isPlaylistCreationErrorDialogOpen) {
        PlaylistCreationErrorDialog(
            onDismiss = { screenDelegate.handle(PlaylistListUserAction.DismissPlaylistCreationErrorDialog) },
        )
    }
    LazyColumn {
        items(state.playlistList) { item ->
            ModelListItem(
                state = item,
                modifier = Modifier.clickable {
                    screenDelegate.handle(PlaylistListUserAction.PlaylistClicked(item.id))
                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            screenDelegate.handle(
                                PlaylistListUserAction.PlaylistOverflowMenuIconClicked(
                                    playlistId = item.id
                                )
                            )
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = com.sebastianvm.commons.R.drawable.ic_overflow),
                            contentDescription = stringResource(id = com.sebastianvm.commons.R.string.more)
                        )
                    }
                }
            )
        }
    }
}
