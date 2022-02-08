package com.sebastianvm.musicplayer.ui.library.playlists

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.TextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.components.lists.SingleLineListItem
import com.sebastianvm.musicplayer.ui.theme.textFieldColors
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

interface PlaylistsListScreenNavigationDelegate {
    fun navigateUp()
    fun navigateToPlaylist(playlistName: String)
    fun openContextMenu(playlistName: String)
}

@Composable
fun PlaylistsListScreen(
    screenViewModel: PlaylistsListViewModel = viewModel(),
    delegate: PlaylistsListScreenNavigationDelegate,
) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is PlaylistsListUiEvent.NavigateToPlaylist -> {
                    delegate.navigateToPlaylist(event.playlistName)
                }
                is PlaylistsListUiEvent.NavigateUp -> delegate.navigateUp()
                is PlaylistsListUiEvent.OpenContextMenu -> {
                    delegate.openContextMenu(event.playlistName)
                }
            }
        },
        fab = {
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(id = R.string.new_playlist)) },
                onClick = { screenViewModel.handle(PlaylistsListUserAction.FabClicked) },
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
                        screenViewModel.handle(PlaylistsListUserAction.SortByClicked)
                    }

                    override fun upButtonClicked() {
                        screenViewModel.handle(PlaylistsListUserAction.UpButtonClicked)
                    }
                })
        }) { state ->
        PlaylistsListLayout(state = state, object : PlaylistsListScreenDelegate {
            override fun onPlaylistClicked(playlistName: String) {
                screenViewModel.handle(action = PlaylistsListUserAction.PlaylistClicked(playlistName = playlistName))
            }

            override fun onContextMenuIconClicked(playlistName: String) {
                screenViewModel.handle(
                    action = PlaylistsListUserAction.OverflowMenuIconClicked(
                        playlistName = playlistName
                    )
                )
            }

            override fun onDismissDialog() {
                screenViewModel.handle(PlaylistsListUserAction.DialogDismissed)
            }

            override fun onSubmit(playlistName: String) {
                screenViewModel.handle(PlaylistsListUserAction.PlaylistCreated(playlistName))
            }
        })
    }
}

interface PlaylistsListScreenDelegate : PlaylistDialogDelegate {
    fun onPlaylistClicked(playlistName: String) = Unit
    fun onContextMenuIconClicked(playlistName: String) = Unit
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PlaylistsListScreenPreview(
    @PreviewParameter(PlaylistsListStatePreviewParameterProvider::class) state: PlaylistsListState
) {
    ScreenPreview {
        PlaylistsListLayout(state = state, object : PlaylistsListScreenDelegate {
            override fun onPlaylistClicked(playlistName: String) = Unit
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
                colors = textFieldColors()
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
                modifier = Modifier.clickable { delegate.onPlaylistClicked(item.playlistName) },
                afterListContent = {
                    IconButton(
                        onClick = { delegate.onContextMenuIconClicked(playlistName = item.playlistName) },
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
