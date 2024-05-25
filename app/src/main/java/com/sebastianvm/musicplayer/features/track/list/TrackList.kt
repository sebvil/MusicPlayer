package com.sebastianvm.musicplayer.features.track.list

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.Screen
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.components.lists.ModelList
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState

data class TrackList(override val arguments: TrackListArguments, val navController: NavController) :
    Screen<TrackListArguments> {
    @Composable
    override fun Content(modifier: Modifier) {
        val stateHolder = rememberTrackListStateHolder(arguments, navController)
        TrackList(
            stateHolder = stateHolder,
            modifier = modifier,
            contentWindowInsets = WindowInsets.systemBars
        )
    }
}

@Composable
fun TrackList(
    stateHolder: TrackListStateHolder,
    modifier: Modifier = Modifier,
    contentWindowInsets: WindowInsets = WindowInsets(0)
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
        TrackList(
            state = state,
            handle = stateHolder::handle,
            contentWindowInsets = contentWindowInsets
        )
    }
}

@Composable
fun TrackList(
    state: TrackListState,
    handle: Handler<TrackListUserAction>,
    modifier: Modifier = Modifier,
    contentWindowInsets: WindowInsets = WindowInsets(0),
) {
    Scaffold(
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
//                        val trackListType = state.trackListType
//                        navigateToTrackSearchScreen(TrackSearchArguments(playlistId = trackListType.playlistId))
                    }
                )
            }
        },
        contentWindowInsets = contentWindowInsets
    ) { paddingValues ->
        TrackListLayout(
            state = state,
            handle = handle,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackListLayout(
    state: TrackListState,
    handle: Handler<TrackListUserAction>,
    modifier: Modifier = Modifier,
) {
    ModelList(
        state = state.modelListState,
        modifier = modifier,
        onBackButtonClicked = {
            handle(TrackListUserAction.BackClicked)
        },
        onSortButtonClicked = { handle(TrackListUserAction.SortButtonClicked) },
        onItemClicked = { index, _ ->
//            onTrackClicked(index)
        },
        onItemMoreIconClicked = { _, item ->
            handle(TrackListUserAction.TrackMoreIconClicked(trackId = item.id))
        }
    )
}
