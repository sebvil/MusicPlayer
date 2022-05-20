package com.sebastianvm.musicplayer.ui.library.playlists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.components.lists.SingleLineListItem
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.ComposePreviews
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

@Composable
fun PlaylistsListScreen(
    screenViewModel: PlaylistsListViewModel = viewModel(),
    navigationDelegate: NavigationDelegate,
) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = {},
        navigationDelegate = navigationDelegate,
        fab = {
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(id = R.string.new_playlist)) },
                onClick = { screenViewModel.onFabClicked() },
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
                        screenViewModel.onSortByClicked()
                    }

                    override fun upButtonClicked() {
                        screenViewModel.onUpButtonClicked()
                    }
                })
        }) { state ->
        PlaylistsListLayout(state = state, object : PlaylistsListScreenDelegate {
            override fun onPlaylistClicked(playlistId: Long) {
                screenViewModel.onPlaylistClicked(playlistId = playlistId)
            }

            override fun onContextMenuIconClicked(playlistId: Long) {
                screenViewModel.onOverflowMenuIconClicked(playlistId = playlistId)
            }

            override fun onDismissDialog() {
                screenViewModel.onDialogDismissed()
            }

            override fun onSubmit(playlistName: String) {
                screenViewModel.onPlaylistCreated(playlistName)
            }
        })
    }
}

interface PlaylistsListScreenDelegate : PlaylistDialogDelegate {
    fun onPlaylistClicked(playlistId: Long) = Unit
    fun onContextMenuIconClicked(playlistId: Long) = Unit
}

@ComposePreviews
@Composable
fun PlaylistsListScreenPreview(
    @PreviewParameter(PlaylistsListStatePreviewParameterProvider::class) state: PlaylistsListState
) {
    ScreenPreview {
        PlaylistsListLayout(state = state, object : PlaylistsListScreenDelegate {
            override fun onPlaylistClicked(playlistId: Long) = Unit
        })
    }
}

interface PlaylistDialogDelegate {
    fun onDismissDialog() = Unit
    fun onSubmit(playlistName: String) = Unit
}

@Composable
fun CreatePlaylistDialog(delegate: PlaylistDialogDelegate) {
    var playListName by rememberSaveable {
        mutableStateOf("")
    }
    AlertDialog(
        onDismissRequest = { delegate.onDismissDialog() },
        confirmButton = {
            TextButton(onClick = { delegate.onSubmit(playListName) }) {
                Text(text = "Create")
            }
        },
        dismissButton = {
            TextButton(onClick = { delegate.onDismissDialog() }) {
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
fun PlaylistsListLayout(
    state: PlaylistsListState,
    delegate: PlaylistsListScreenDelegate
) {
    if (state.isDialogOpen) {
        CreatePlaylistDialog(delegate = delegate)
    }
    LazyColumn {
        items(state.playlistsList) { item ->
            SingleLineListItem(
                modifier = Modifier.clickable { delegate.onPlaylistClicked(item.id) },
                afterListContent = {
                    IconButton(
                        onClick = { delegate.onContextMenuIconClicked(playlistId = item.id) },
                        modifier = Modifier.padding(end = AppDimensions.spacing.xSmall)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_overflow),
                            contentDescription = stringResource(R.string.more)
                        )
                    }
                }
            ) {
                Text(
                    text = item.playlistName,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

        }
    }
}
