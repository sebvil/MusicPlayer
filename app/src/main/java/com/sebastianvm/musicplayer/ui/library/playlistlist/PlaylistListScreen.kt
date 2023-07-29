package com.sebastianvm.musicplayer.ui.library.playlistlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.ui.bottomsheets.context.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.EmptyScreen
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArguments


////@Composable
//fun PlaylistListScreen(
//    state: PlaylistListState,
//    onSortByClicked: () -> Unit,
//    onDismissPlaylistCreationErrorDialog: () -> Unit,
//    onCreatePlaylistCLicked: (playlistName: String) -> Unit,
//    navigateToPlaylist: (TrackListArguments) -> Unit,
//    openPlaylistContextMenu: (PlaylistContextMenuArguments) -> Unit,
//    navigateBack: NoArgNavFunction,
//    modifier: Modifier = Modifier
//) {
//    var isCreatePlaylistDialogOpen by remember {
//        mutableStateOf(false)
//    }
//    ScreenScaffold(
//        modifier = modifier,
//        floatingActionButton = {
//            ExtendedFloatingActionButton(
//                text = { Text(text = stringResource(id = R.string.new_playlist)) },
//                onClick = { isCreatePlaylistDialogOpen = true },
//                icon = {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_plus),
//                        contentDescription = stringResource(id = R.string.new_playlist)
//                    )
//                })
//        },
//        topBar = {
//            LibraryTopBar(
//                title = stringResource(id = R.string.playlists),
//                delegate = object : LibraryTopBarDelegate {
//                    override fun sortByClicked() {
//                        onSortByClicked()
//                    }
//
//                    override fun upButtonClicked() {
//                        navigateBack()
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        PlaylistListLayout(
//            state = state,
//            isCreatePlaylistDialogOpen = isCreatePlaylistDialogOpen,
//            onDismissPlaylistCreationDialog = { isCreatePlaylistDialogOpen = false },
//            onDismissPlaylistCreationErrorDialog = onDismissPlaylistCreationErrorDialog,
//            onCreatePlaylistCLicked = onCreatePlaylistCLicked,
//            navigateToPlaylist = navigateToPlaylist,
//            openPlaylistContextMenu = openPlaylistContextMenu,
//            modifier = Modifier.padding(paddingValues)
//        )
//    }
//}


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
    navigateToPlaylist: (TrackListArguments) -> Unit,
    openPlaylistContextMenu: (PlaylistContextMenuArguments) -> Unit,
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
    if (state.playlistList.isEmpty()) {
        EmptyScreen(
            message = {
                Text(
                    text = stringResource(R.string.no_playlists_try_creating_one),
                    textAlign = TextAlign.Center
                )
            },
            button = {
                Button(onClick = {}) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Text(text = stringResource(id = R.string.create_playlist))
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        )
    } else {
        LazyColumn(modifier = modifier, contentPadding = LocalPaddingValues.current) {
            items(state.playlistList) { item ->
                ModelListItem(
                    state = item,
                    modifier = Modifier.clickable {
                        navigateToPlaylist(
                            TrackListArguments(
                                trackList = MediaGroup.Playlist(
                                    playlistId = item.id
                                )
                            )
                        )
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
}
