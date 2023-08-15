package com.sebastianvm.musicplayer.ui.library.playlistlist

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.bottomsheets.context.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.ModelList
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArgumentsForNav

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
    navigateToPlaylist: (TrackListArgumentsForNav) -> Unit,
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
            onDismiss = onDismissPlaylistCreationErrorDialog
        )
    }
    ModelList(
        state = state.modelListState,
        modifier = modifier,
        onBackButtonClicked = {},
        onSortButtonClicked = null,
        onItemClicked = { _, item ->
            navigateToPlaylist(
                TrackListArgumentsForNav(
                    trackListType = MediaGroup.Genre(
                        item.id
                    )
                )
            )
        },
        onItemMoreIconClicked = { _, item ->
            openPlaylistContextMenu(
                PlaylistContextMenuArguments(item.id)
            )
        }
    )
}
