package com.sebastianvm.musicplayer.ui.library.playlistlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.components.topbar.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.topbar.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenLayout
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate

@Composable
fun PlaylistListScreen(viewModel: PlaylistListViewModel, navigationDelegate: NavigationDelegate) {
    val listState = rememberLazyListState()
    Screen(
        screenViewModel = viewModel,
        eventHandler = { event ->
            when (event) {
                is PlaylistListUiEvent.ScrollToTop -> {
                    listState.scrollToItem(0)
                }
            }
        },
        navigationDelegate = navigationDelegate
    ) { state, delegate ->
        PlaylistListScreen(
            state = state,
            screenDelegate = delegate,
            listState = listState
        )
    }
}

@Composable
fun PlaylistListScreen(
    state: PlaylistListState,
    screenDelegate: ScreenDelegate<PlaylistListUserAction>,
    listState: LazyListState
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
        PlaylistListLayout(state = state, screenDelegate = screenDelegate, listState = listState)
    }
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
    screenDelegate: ScreenDelegate<PlaylistListUserAction>,
    listState: LazyListState
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
    LazyColumn(state = listState) {
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
                            painter = painterResource(id = R.drawable.ic_overflow),
                            contentDescription = stringResource(id = R.string.more)
                        )
                    }
                }
            )
        }
    }
}
