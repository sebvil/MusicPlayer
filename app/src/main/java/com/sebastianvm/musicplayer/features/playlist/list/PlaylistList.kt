package com.sebastianvm.musicplayer.features.playlist.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.designsystem.components.BottomSheet
import com.sebastianvm.musicplayer.destinations.TrackListRouteDestination
import com.sebastianvm.musicplayer.features.playlist.menu.PlaylistContextMenu
import com.sebastianvm.musicplayer.features.track.list.TrackListArgumentsForNav
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.components.EmptyScreen
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.components.lists.ModelList
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState

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
fun PlaylistList(
    stateHolder: PlaylistListStateHolder,
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier
) {
    val uiState by stateHolder.currentState
    UiStateScreen(uiState = uiState, modifier = modifier.fillMaxSize(), emptyScreen = {
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
    }) { state ->
        PlaylistListLayout(
            state = state,
            handle = stateHolder::handle,
            isCreatePlaylistDialogOpen = false,
            navigateToPlaylist = { args ->
                navigator.navigate(TrackListRouteDestination(args))
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistListLayout(
    state: PlaylistListState,
    handle: Handler<PlaylistListUserAction>,
    isCreatePlaylistDialogOpen: Boolean,
    navigateToPlaylist: (TrackListArgumentsForNav) -> Unit,
    modifier: Modifier = Modifier
) {
    if (isCreatePlaylistDialogOpen) {
        CreatePlaylistDialog(
            onDismiss = {
                handle(PlaylistListUserAction.DismissPlaylistCreationErrorDialog)
            },
            onConfirm = {
                handle(PlaylistListUserAction.CreatePlaylistButtonClicked(it))
            }
        )
    }

    if (state.isPlaylistCreationErrorDialogOpen) {
        PlaylistCreationErrorDialog(
            onDismiss = {
                handle(PlaylistListUserAction.DismissPlaylistCreationErrorDialog)
            }
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
            handle(PlaylistListUserAction.PlaylistMoreIconClicked(item.id))
        }
    )

    state.playlistContextMenuStateHolder?.let { playlistContextMenuStateHolder ->
        BottomSheet(
            onDismissRequest = {
                handle(PlaylistListUserAction.PlaylistContextMenuDismissed)
            },
        ) {
            PlaylistContextMenu(
                stateHolder = playlistContextMenuStateHolder,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }
}
