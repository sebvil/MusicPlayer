package com.sebastianvm.musicplayer.features.track.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.services.Services
import com.sebastianvm.musicplayer.core.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.core.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.core.ui.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.Features

class TrackListUiComponent(val navController: NavController, val features: Features) :
    BaseUiComponent<TrackListState, TrackListUserAction, TrackListStateHolder>() {

    override fun createStateHolder(services: Services): TrackListStateHolder {
        return TrackListStateHolder(
            navController = navController,
            trackRepository = services.repositoryProvider.trackRepository,
            sortPreferencesRepository = services.repositoryProvider.sortPreferencesRepository,
            playbackManager = services.playbackManager,
            features = features,
        )
    }

    @Composable
    override fun Content(
        state: TrackListState,
        handle: Handler<TrackListUserAction>,
        modifier: Modifier,
    ) {
        TrackList(state = state, handle = handle, modifier = modifier)
    }
}

@Composable
fun TrackList(
    state: TrackListState,
    handle: Handler<TrackListUserAction>,
    modifier: Modifier = Modifier,
) {
    when (state) {
        TrackListState.Loading -> {
            Box(modifier = modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        is TrackListState.Data -> {
            TrackList(state = state, handle = handle, modifier = modifier)
        }
    }
}

@Composable
fun TrackList(
    state: TrackListState.Data,
    handle: Handler<TrackListUserAction>,
    modifier: Modifier = Modifier,
) {

    if (state.tracks.isEmpty()) {
        StoragePermissionNeededEmptyScreen(
            message = RString.no_tracks_found,
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        )
    } else {
        LazyColumn(modifier = modifier, contentPadding = LocalPaddingValues.current) {
            item {
                SortButton(
                    state = state.sortButtonState,
                    onClick = { handle(TrackListUserAction.SortButtonClicked) },
                    modifier = Modifier.padding(start = 16.dp),
                )
            }

            itemsIndexed(state.tracks, key = { index, item -> index to item.id }) { index, item ->
                TrackRow(
                    state = item,
                    modifier =
                        Modifier.animateItem().clickable {
                            handle(TrackListUserAction.TrackClicked(trackIndex = index))
                        },
                    trailingContent = {
                        IconButton(
                            onClick = {
                                handle(
                                    TrackListUserAction.TrackMoreIconClicked(
                                        trackId = item.id,
                                        trackPositionInList = index,
                                    ))
                            }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = stringResource(id = RString.more),
                                )
                            }
                    },
                )
            }
        }
    }
}
