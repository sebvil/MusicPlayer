package com.sebastianvm.musicplayer.features.track.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.di.AppDependencies
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.components.lists.ModelList
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState

data class TrackListUiComponent(
    override val arguments: TrackListArguments,
    val navController: NavController,
) :
    BaseUiComponent<
        TrackListArguments,
        UiState<TrackListState>,
        TrackListUserAction,
        TrackListStateHolder,
    >() {

    override fun createStateHolder(dependencies: AppDependencies): TrackListStateHolder {
        return getTrackListStateHolder(dependencies, arguments, navController)
    }

    @Composable
    override fun Content(
        state: UiState<TrackListState>,
        handle: Handler<TrackListUserAction>,
        modifier: Modifier,
    ) {
        TrackList(uiState = state, handle = handle, modifier = modifier)
    }
}

@Composable
fun TrackList(
    uiState: UiState<TrackListState>,
    handle: Handler<TrackListUserAction>,
    modifier: Modifier = Modifier,
) {
    UiStateScreen(
        uiState = uiState,
        modifier = modifier.fillMaxSize(),
        emptyScreen = {
            StoragePermissionNeededEmptyScreen(
                message = R.string.no_tracks_found,
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            )
        },
    ) { state ->
        TrackList(state = state, handle = handle)
    }
}

@Composable
fun TrackList(
    state: TrackListState,
    handle: Handler<TrackListUserAction>,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            if (state.trackListType is MediaGroup.Playlist) {
                ExtendedFloatingActionButton(
                    text = { Text(text = stringResource(id = R.string.add_tracks)) },
                    icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                    onClick = {
                        //                        val trackListType = state.trackListType
                        //
                        // navigateToTrackSearchScreen(TrackSearchArguments(playlistId =
                        // trackListType.playlistId))
                    },
                )
            }
        },
    ) { paddingValues ->
        TrackListLayout(state = state, handle = handle, modifier = Modifier.padding(paddingValues))
    }
}

@Composable
fun TrackListLayout(
    state: TrackListState,
    handle: Handler<TrackListUserAction>,
    modifier: Modifier = Modifier,
) {
    ModelList(
        state = state.modelListState,
        modifier = modifier,
        onBackButtonClicked = { handle(TrackListUserAction.BackClicked) },
        onSortButtonClicked = { handle(TrackListUserAction.SortButtonClicked) },
        onItemClicked = { index, _ ->
            handle(TrackListUserAction.TrackClicked(trackIndex = index))
        },
        onItemMoreIconClicked = { index, item ->
            handle(
                TrackListUserAction.TrackMoreIconClicked(
                    trackId = item.id,
                    trackPositionInList = index,
                )
            )
        },
    )
}
