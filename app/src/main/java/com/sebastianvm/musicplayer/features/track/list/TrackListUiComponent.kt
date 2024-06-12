package com.sebastianvm.musicplayer.features.track.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.designsystem.components.Text
import com.sebastianvm.musicplayer.designsystem.components.TrackRow
import com.sebastianvm.musicplayer.di.Dependencies
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.util.resources.RString
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder

data class TrackListUiComponent(
    override val arguments: TrackListArguments,
    val navController: NavController,
) :
    BaseUiComponent<
        TrackListArguments,
        TrackListState,
        TrackListUserAction,
        TrackListStateHolder,
    >() {

    override fun createStateHolder(dependencies: Dependencies): TrackListStateHolder {
        return TrackListStateHolder(
            args = arguments,
            navController = navController,
            trackRepository = dependencies.repositoryProvider.trackRepository,
            sortPreferencesRepository = dependencies.repositoryProvider.sortPreferencesRepository,
            playbackManager = dependencies.repositoryProvider.playbackManager,
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
    Scaffold(
        modifier = modifier,
    ) { paddingValues ->
        TrackListLayout(state = state, handle = handle, modifier = Modifier.padding(paddingValues))
    }
}

@Composable
fun TrackListLayout(
    state: TrackListState.Data,
    handle: Handler<TrackListUserAction>,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val content: @Composable (Modifier, PaddingValues) -> Unit =
        { contentModifier, contentPadding ->
            if (state.tracks.isEmpty()) {
                StoragePermissionNeededEmptyScreen(
                    message = RString.no_tracks_found,
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                )
            } else {
                LazyColumn(
                    modifier = contentModifier,
                    contentPadding = contentPadding,
                    state = listState,
                ) {
                    state.sortButtonState?.let {
                        item {
                            TextButton(
                                onClick = { handle(TrackListUserAction.SortButtonClicked) },
                                modifier = Modifier.padding(start = 16.dp),
                            ) {
                                Text(text = "${stringResource(id = RString.sort_by)}:")
                                Icon(
                                    imageVector =
                                        if (it.sortOrder == MediaSortOrder.ASCENDING)
                                            Icons.Default.ArrowUpward
                                        else Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                )
                                Text(text = stringResource(id = it.text))
                            }
                        }
                    }
                    itemsIndexed(state.tracks, key = { index, item -> index to item.id }) {
                        index,
                        item ->
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
                                            )
                                        )
                                    }
                                ) {
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
    when (val headerState = state.headerState) {
        is Header.State.None -> {
            content(modifier, LocalPaddingValues.current)
        }
        is Header.State.Simple -> {
            Column(modifier) {
                TopBar(
                    title = headerState.title,
                    onBackButtonClicked = { handle(TrackListUserAction.BackClicked) },
                )
                content(Modifier, LocalPaddingValues.current)
            }
        }
        is Header.State.WithImage -> {
            HeaderWithImageModelList(
                state = headerState,
                modifier = modifier,
                listState = listState,
                onBackButtonClicked = { handle(TrackListUserAction.BackClicked) },
            ) {
                content(Modifier, it)
            }
        }
    }
}
