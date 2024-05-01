package com.sebastianvm.musicplayer.features.track.list

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.designsystem.components.BottomSheet
import com.sebastianvm.musicplayer.features.sort.SortMenu
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.components.lists.ModelList
import com.sebastianvm.musicplayer.ui.playlist.TrackSearchArguments
import com.sebastianvm.musicplayer.ui.util.compose.ScreenScaffold
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState


@Composable
fun TrackList(
    stateHolder: TrackListStateHolder,
    modifier: Modifier = Modifier
) {
    val uiState by stateHolder.currentState
    UiStateScreen(uiState = uiState, modifier = modifier.fillMaxSize(), emptyScreen = {
        StoragePermissionNeededEmptyScreen(
            message = R.string.no_tracks_found,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        )
    }) { state ->
        TrackList(state = state,
            handle = stateHolder::handle,
            onTrackClicked = {
                TODO()
            },
            navigateToTrackSearchScreen = {
//                navigator.navigate(TrackSearchScreenDestination(it))
            },

            navigateBack = {
//                navigator.navigateUp()
            })
    }
}

@Composable
fun TrackList(
    state: TrackListState,
    handle: Handler<TrackListUserAction>,
    onTrackClicked: (trackIndex: Int) -> Unit,
    navigateToTrackSearchScreen: (args: TrackSearchArguments) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScreenScaffold(modifier = modifier, floatingActionButton = {
        if (state.trackListType is MediaGroup.Playlist) {
            ExtendedFloatingActionButton(text = { Text(text = stringResource(id = R.string.add_tracks)) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add, contentDescription = null
                    )
                },
                onClick = {
                    val trackListType = state.trackListType
                    navigateToTrackSearchScreen(TrackSearchArguments(playlistId = trackListType.playlistId))
                })
        }
    }) { paddingValues ->
        TrackListLayout(
            state = state,
            handle = handle,
            onBackButtonClicked = navigateBack,
            onTrackClicked = onTrackClicked,
            modifier = Modifier
                .padding(paddingValues)
                .windowInsetsPadding(WindowInsets.statusBars)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackListLayout(
    state: TrackListState,
    handle: Handler<TrackListUserAction>,
    onTrackClicked: (trackIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    onBackButtonClicked: () -> Unit = {}
) {
    ModelList(state = state.modelListState,
        modifier = modifier,
        onBackButtonClicked = onBackButtonClicked,
        onSortButtonClicked = { handle(TrackListUserAction.SortButtonClicked) },
        onItemClicked = { index, _ -> onTrackClicked(index) },
        onItemMoreIconClicked = { _, item ->
            handle(TrackListUserAction.TrackMoreIconClicked(trackId = item.id))
        })

    state.trackContextMenuStateHolder?.let { trackContextMenuStateHolder ->
        BottomSheet(
            onDismissRequest = { handle(TrackListUserAction.TrackContextMenuDismissed) },
        ) {
            TrackContextMenu(
                stateHolder = trackContextMenuStateHolder,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }

    state.sortMenuStateHolder?.let { sortMenuStateHolder ->
        BottomSheet(
            onDismissRequest = { handle(TrackListUserAction.SortMenuDismissed) },
        ) {
            SortMenu(
                stateHolder = sortMenuStateHolder, modifier = Modifier.navigationBarsPadding()
            )
        }
    }
}
