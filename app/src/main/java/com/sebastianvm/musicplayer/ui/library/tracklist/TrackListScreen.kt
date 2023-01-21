package com.sebastianvm.musicplayer.ui.library.tracklist

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.toSortableListType
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicator
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicatorDelegate
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.topbar.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.topbar.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.components.topbar.LibraryTopBarState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

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
    Scaffold(
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

@Composable
fun CollapsingImageHeader(
    mediaArtImageState: MediaArtImageState,
    listState: LazyListState,
    title: String,
    updateAlpha: (Float) -> Unit
) {
    val minSizeDp = 100.dp
    val minSizePx = with(LocalDensity.current) { minSizeDp.toPx() }
    val textHeight = remember {
        mutableStateOf(-1)
    }
    val density = LocalDensity.current
    val maxSizeDp = 500.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val originalImageHeaderSizeDp = min(screenWidth - 200.dp, maxSizeDp)
    val sizeDp = remember { mutableStateOf(originalImageHeaderSizeDp) }

    LaunchedEffect(key1 = listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.getOrNull(1) }
            .filterNotNull()
            .distinctUntilChanged { old, new ->
                (old.index == new.index && old.offset == new.offset)
            }
            .collect { listSecondItem ->
                // header is off-screen
                if (listSecondItem.index != 1) {
                    sizeDp.value = minSizeDp
                    updateAlpha(1f)
                    return@collect
                }
                // header is off-screen
                if (listSecondItem.offset < 0) {
                    sizeDp.value = minSizeDp
                    updateAlpha(1f)
                    return@collect
                }

                val offsetDiff = listSecondItem.offset
                if (offsetDiff < (minSizePx + textHeight.value)) {
                    sizeDp.value = minSizeDp
                    updateAlpha(0f)
                } else if (textHeight.value != -1) {
                    sizeDp.value = with(density) { offsetDiff.toDp() - textHeight.value.toDp() }
                    updateAlpha(0f)
                }
                if (offsetDiff < textHeight.value) {
                    updateAlpha(1f - (offsetDiff / textHeight.value.toFloat()))
                }
            }
    }

    val padding = (originalImageHeaderSizeDp - sizeDp.value).coerceIn(
        minimumValue = 0.dp,
        maximumValue = null
    )


    Column(
        modifier = Modifier
            .animateContentSize()
            .fillMaxWidth()
            .padding(top = padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MediaArtImage(
            mediaArtImageState = mediaArtImageState,
            modifier = Modifier
                .size(sizeDp.value)
        )
        Text(
            text = title,
            modifier = Modifier
                .onSizeChanged {
                    textHeight.value = it.height
                },
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
            textAlign = TextAlign.Center
        )
    }

}

