package com.sebastianvm.musicplayer.features.track.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.designsystem.components.Text
import com.sebastianvm.musicplayer.designsystem.components.TrackRow
import com.sebastianvm.musicplayer.di.AppDependencies
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder

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
    val listState = rememberLazyListState()
    val content: @Composable (Modifier, PaddingValues) -> Unit =
        { contentModifier, contentPadding ->
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
                            Text(text = "${stringResource(id = R.string.sort_by)}:")
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
                itemsIndexed(state.tracks, key = { index, item -> index to item.id }) { index, item
                    ->
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
                                    contentDescription = stringResource(id = R.string.more),
                                )
                            }
                        },
                    )
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
