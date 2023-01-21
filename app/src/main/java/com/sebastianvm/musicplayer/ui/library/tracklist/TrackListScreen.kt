package com.sebastianvm.musicplayer.ui.library.tracklist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.toSortableListType
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicator
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicatorDelegate
import com.sebastianvm.musicplayer.ui.components.header.CollapsingImageHeader
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.topbar.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.topbar.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.components.topbar.LibraryTopBarState
import com.sebastianvm.musicplayer.ui.util.compose.ScreenScaffold

@Composable
fun TrackListRoute(
    viewModel: TrackListViewModel,
    openSortMenu: (listType: SortableListType) -> Unit,
    navigateToTrackSearchScreen: (playlistId: Long) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    TrackListScreen(
        state = state,
        onTrackClicked = { trackIndex ->
            viewModel.handle(TrackListUserAction.TrackClicked(trackIndex = trackIndex))
        },
        onTrackOverflowMenuIconClicked = { trackIndex, trackId, position ->
            viewModel.handle(
                TrackListUserAction.TrackOverflowMenuIconClicked(
                    trackIndex = trackIndex,
                    trackId = trackId,
                    position = position
                )
            )
        },
        onDismissPlaybackErrorDialog = {
            viewModel.handle(TrackListUserAction.DismissPlaybackErrorDialog)
        },
        navigateToTrackSearchScreen = navigateToTrackSearchScreen,
        openSortMenu = openSortMenu,
        navigateBack = navigateBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackListScreen(
    state: TrackListState,
    onTrackClicked: (trackIndex: Int) -> Unit,
    onTrackOverflowMenuIconClicked: (trackIndex: Int, trackId: Long, position: Long?) -> Unit,
    onDismissPlaybackErrorDialog: () -> Unit,
    navigateToTrackSearchScreen: (playlistId: Long) -> Unit,
    openSortMenu: (listType: SortableListType) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    val maybeTitleAlpha = remember {
        mutableStateOf(0f)
    }
    val titleAlpha: State<Float> = remember(state.trackListName, maybeTitleAlpha) {
        derivedStateOf {
            if (state.headerImage == null) {
                1f
            } else {
                maybeTitleAlpha.value
            }
        }
    }
    ScreenScaffold(
        modifier = modifier,
        floatingActionButton = {
            if (state.trackListType is MediaGroup.Playlist) {
                ExtendedFloatingActionButton(
                    text = { Text(text = stringResource(id = R.string.add_tracks)) },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_plus),
                            contentDescription = stringResource(id = R.string.add_tracks)
                        )
                    },
                    onClick = {
                        val trackListType = state.trackListType
                        navigateToTrackSearchScreen(trackListType.playlistId)
                    }
                )
            }
        },
        topBar = {
            LibraryTopBar(
                state = LibraryTopBarState(
                    title = state.trackListName ?: stringResource(id = R.string.all_songs),
                    hasSortButton = state.trackListType !is MediaGroup.Album,
                ),
                delegate = object : LibraryTopBarDelegate {
                    override fun upButtonClicked() {
                        navigateBack()
                    }

                    override fun sortByClicked() {
                        openSortMenu(state.trackListType.toSortableListType())
                    }
                },
                titleAlpha = titleAlpha.value
            )
        }
    ) { paddingValues ->
        TrackListLayout(
            state = state,
            onTrackClicked = onTrackClicked,
            onTrackOverflowMenuIconClicked = onTrackOverflowMenuIconClicked,
            onDismissPlaybackErrorDialog = onDismissPlaybackErrorDialog,
            updateAlpha = { newAlpha -> maybeTitleAlpha.value = newAlpha },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun TrackListLayout(
    state: TrackListState,
    onTrackClicked: (trackIndex: Int) -> Unit,
    onTrackOverflowMenuIconClicked: (trackIndex: Int, trackId: Long, position: Long?) -> Unit,
    onDismissPlaybackErrorDialog: () -> Unit,
    updateAlpha: (Float) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    PlaybackStatusIndicator(
        playbackResult = state.playbackResult,
        delegate = object : PlaybackStatusIndicatorDelegate {
            override fun onDismissRequest() {
                onDismissPlaybackErrorDialog()
            }
        })

    LazyColumn(state = listState, modifier = modifier) {
        item {
            state.headerImage?.also {
                CollapsingImageHeader(
                    mediaArtImageState = it,
                    listState = listState,
                    title = state.trackListName ?: "",
                    updateAlpha = updateAlpha
                )
            }
        }
        itemsIndexed(state.trackList, key = { _, item -> item.id }) { index, item ->
            ModelListItem(
                state = item,
                modifier = Modifier
                    .clickable {
                        onTrackClicked(index)
                    },
                trailingContent = {
                    IconButton(
                        onClick = {
                            onTrackOverflowMenuIconClicked(
                                index,
                                item.id,
                                (item as? ModelListItemState.WithPosition)?.position
                            )
                        })
                    {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_overflow),
                            contentDescription = stringResource(R.string.more),
                        )
                    }
                }
            )
        }
    }
}