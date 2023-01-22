package com.sebastianvm.musicplayer.ui.library.playlistlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.bottomsheets.context.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.components.topbar.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.topbar.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArguments
import com.sebastianvm.musicplayer.ui.navigation.NavFunction
import com.sebastianvm.musicplayer.ui.navigation.NoArgNavFunction
import com.sebastianvm.musicplayer.ui.util.compose.ScreenScaffold

@Composable
fun PlaylistListRoute(
    viewModel: PlaylistListViewModel,
    navigateToPlaylist: NavFunction<TrackListArguments>,
    openPlaylistContextMenu: NavFunction<PlaylistContextMenuArguments>,
    navigateBack: NoArgNavFunction
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    PlaylistListScreen(
        state = state,
        onSortByClicked = { viewModel.handle(PlaylistListUserAction.SortByClicked) },
        onDismissPlaylistCreationErrorDialog = { viewModel.handle(PlaylistListUserAction.DismissPlaylistCreationErrorDialog) },
        onCreatePlaylistCLicked = { playlistName ->
            viewModel.handle(
                PlaylistListUserAction.CreatePlaylistButtonClicked(
                    playlistName = playlistName
                )
            )
        },
        navigateToPlaylist = navigateToPlaylist,
        openPlaylistContextMenu = openPlaylistContextMenu,
        navigateBack = navigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistListScreen(
    state: PlaylistListState,
    onSortByClicked: () -> Unit,
    onDismissPlaylistCreationErrorDialog: () -> Unit,
    onCreatePlaylistCLicked: (playlistName: String) -> Unit,
    navigateToPlaylist: NavFunction<TrackListArguments>,
    openPlaylistContextMenu: NavFunction<PlaylistContextMenuArguments>,
    navigateBack: NoArgNavFunction,
    modifier: Modifier = Modifier
) {
    var isCreatePlaylistDialogOpen by remember {
        mutableStateOf(false)
    }
    ScreenScaffold(
        modifier = modifier,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(id = R.string.new_playlist)) },
                onClick = { isCreatePlaylistDialogOpen = true },
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
                        onSortByClicked()
                    }

                    override fun upButtonClicked() {
                        navigateBack()
                    }
                }
            )
        }
    ) { paddingValues ->
        PlaylistListLayout(
            state = state,
            isCreatePlaylistDialogOpen = isCreatePlaylistDialogOpen,
            onDismissPlaylistCreationDialog = { isCreatePlaylistDialogOpen = false },
            onDismissPlaylistCreationErrorDialog = onDismissPlaylistCreationErrorDialog,
            onCreatePlaylistCLicked = onCreatePlaylistCLicked,
            navigateToPlaylist = navigateToPlaylist,
            openPlaylistContextMenu = openPlaylistContextMenu,
            modifier = Modifier.padding(paddingValues)
        )
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
    isCreatePlaylistDialogOpen: Boolean,
    onDismissPlaylistCreationDialog: () -> Unit,
    onDismissPlaylistCreationErrorDialog: () -> Unit,
    onCreatePlaylistCLicked: (playlistName: String) -> Unit,
    navigateToPlaylist: NavFunction<TrackListArguments>,
    openPlaylistContextMenu: NavFunction<PlaylistContextMenuArguments>,
    modifier: Modifier = Modifier
) {
    if (isCreatePlaylistDialogOpen) {
        CreatePlaylistDialog(
            onDismiss = onDismissPlaylistCreationDialog,
            onConfirm = onCreatePlaylistCLicked
        )
    }

    if (state.isPlaylistCreationErrorDialogOpen) {
        PlaylistCreationErrorDialog(
            onDismiss = onDismissPlaylistCreationErrorDialog,
        )
    }
    LazyColumn(modifier = modifier) {
        items(state.playlistList) { item ->
            ModelListItem(
                state = item,
                modifier = Modifier.clickable {
                    navigateToPlaylist(TrackListArguments(trackList = MediaGroup.Playlist(playlistId = item.id)))
                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            openPlaylistContextMenu(PlaylistContextMenuArguments(playlistId = item.id))
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
