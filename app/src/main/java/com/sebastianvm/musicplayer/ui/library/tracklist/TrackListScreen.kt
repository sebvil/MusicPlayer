package com.sebastianvm.musicplayer.ui.library.tracklist

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.toSortableListType
import com.sebastianvm.musicplayer.ui.PlaybackHandler
import com.sebastianvm.musicplayer.ui.bottomsheets.context.TrackContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.components.lists.ModelList
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.destinations.SortBottomSheetDestination
import com.sebastianvm.musicplayer.ui.destinations.TrackContextMenuDestination
import com.sebastianvm.musicplayer.ui.destinations.TrackSearchScreenDestination
import com.sebastianvm.musicplayer.ui.playlist.TrackSearchArguments
import com.sebastianvm.musicplayer.ui.util.compose.ScreenScaffold

@RootNavGraph
@Destination(navArgsDelegate = TrackListArgumentsForNav::class)
@Composable
fun TrackListRoute(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: TrackListViewModel = hiltViewModel(),
    handlePlayback: PlaybackHandler
) {
    val uiState by viewModel.stateFlow.collectAsStateWithLifecycle()
    UiStateScreen(
        uiState = uiState.toUiState(),
        modifier = modifier
            .fillMaxSize(),
        emptyScreen = {
            StoragePermissionNeededEmptyScreen(
                message = R.string.no_tracks_found,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            )
        }
    ) { state ->
        TrackListScreen(
            state = state,
            onTrackClicked = { trackIndex ->
                handlePlayback(
                    mediaGroup = state.trackListType,
                    initialTrackIndex = trackIndex
                )
            },
            openTrackContextMenu = { navigator.navigate(TrackContextMenuDestination(it)) },
            navigateToTrackSearchScreen = { navigator.navigate(TrackSearchScreenDestination(it)) },
            openSortMenu = { navigator.navigate(SortBottomSheetDestination(it)) },
            navigateBack = { navigator.navigateUp() }
        )
    }
}

@Composable
fun TrackListScreen(
    state: TrackListState,
    onTrackClicked: (trackIndex: Int) -> Unit,
    openTrackContextMenu: (args: TrackContextMenuArguments) -> Unit,
    openSortMenu: (args: SortMenuArguments) -> Unit,
    navigateToTrackSearchScreen: (args: TrackSearchArguments) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScreenScaffold(
        modifier = modifier,
        floatingActionButton = {
            if (state.trackListType is MediaGroup.Playlist) {
                ExtendedFloatingActionButton(
                    text = { Text(text = stringResource(id = R.string.add_tracks)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        val trackListType = state.trackListType
                        navigateToTrackSearchScreen(TrackSearchArguments(playlistId = trackListType.playlistId))
                    }
                )
            }
        }
    ) { paddingValues ->
        TrackListLayout(
            state = state,
            onBackButtonClicked = navigateBack,
            onTrackClicked = onTrackClicked,
            openSortMenu = openSortMenu,
            openTrackContextMenu = openTrackContextMenu,
            modifier = Modifier
                .padding(paddingValues)
                .windowInsetsPadding(WindowInsets.statusBars)
        )
    }
}

@Composable
fun TrackListLayout(
    state: TrackListState,
    onTrackClicked: (trackIndex: Int) -> Unit,
    openSortMenu: (args: SortMenuArguments) -> Unit,
    openTrackContextMenu: (args: TrackContextMenuArguments) -> Unit,
    modifier: Modifier = Modifier,
    onBackButtonClicked: () -> Unit = {}
) {
    ModelList(
        state = state.modelListState,
        modifier = modifier,
        onBackButtonClicked = onBackButtonClicked,
        onSortButtonClicked = {
            openSortMenu(
                SortMenuArguments(listType = state.trackListType.toSortableListType())
            )
        },
        onItemClicked = { index, _ -> onTrackClicked(index) },
        onItemMoreIconClicked = { index, item ->
            openTrackContextMenu(
                TrackContextMenuArguments(
                    trackId = item.id,
                    mediaGroup = state.trackListType,
                    trackIndex = index,
                    positionInPlaylist = (item as? ModelListItemState.WithPosition)?.position
                )
            )
        }
    )
}
