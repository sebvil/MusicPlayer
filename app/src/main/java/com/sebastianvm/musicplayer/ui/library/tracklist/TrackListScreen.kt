package com.sebastianvm.musicplayer.ui.library.tracklist

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarState
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicator
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicatorDelegate
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenLayout
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.math.roundToInt


@Composable
fun TrackListScreen(viewModel: TrackListViewModel, navigationDelegate: NavigationDelegate) {
    val listState = rememberLazyListState()
    Screen(
        screenViewModel = viewModel,
        eventHandler = { event ->
            when (event) {
                is TrackListUiEvent.ScrollToTop -> listState.scrollToItem(0)
            }
        },
        navigationDelegate = navigationDelegate
    ) { state, delegate ->
        TrackListScreen(
            state = state,
            screenDelegate = delegate,
            listState = listState
        )
    }
}

@Composable
fun TrackListScreen(
    state: TrackListState,
    screenDelegate: ScreenDelegate<TrackListUserAction>,
    listState: LazyListState
) {
    ScreenLayout(
        fab = {
            if (state.trackListType == TrackListType.PLAYLIST) {
                ExtendedFloatingActionButton(
                    text = { Text(text = stringResource(id = R.string.add_tracks)) },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_plus),
                            contentDescription = "Plus"
                        )
                    },
                    onClick = { screenDelegate.handle(TrackListUserAction.AddTracksClicked) }
                )
            }

        },
        topBar = {
            LibraryTopBar(
                state = LibraryTopBarState(
                    state.trackListName ?: stringResource(id = R.string.all_songs),
                    hasSortButton = state.trackListType != TrackListType.ALBUM
                ),
                delegate = object : LibraryTopBarDelegate {
                    override fun upButtonClicked() {
                        screenDelegate.handle(TrackListUserAction.UpButtonClicked)
                    }

                    override fun sortByClicked() {
                        screenDelegate.handle(TrackListUserAction.SortByButtonClicked)
                    }
                })
        },
    ) {
        TrackListLayout(state = state, screenDelegate = screenDelegate, listState = listState)
    }
}

@Composable
fun TrackListLayout(
    state: TrackListState,
    screenDelegate: ScreenDelegate<TrackListUserAction>,
    listState: LazyListState = rememberLazyListState(),
) {
    val density = LocalDensity.current
    PlaybackStatusIndicator(
        playbackResult = state.playbackResult,
        delegate = object : PlaybackStatusIndicatorDelegate {
            override fun onDismissRequest() {
                screenDelegate.handle(TrackListUserAction.DismissPlaybackErrorDialog)
            }
        })

    val maxSizeDp = 500.dp
    val minSizeDp = 100.dp
    val minSizePx = with(LocalDensity.current) { minSizeDp.toPx() }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val originalImageHeaderSizeDp =
        state.headerImage?.let { min(screenWidth - 200.dp, maxSizeDp) } ?: 0.dp
    val sizeDp = remember { mutableStateOf(originalImageHeaderSizeDp) }
    val imageOffsetHeightPx = remember { mutableStateOf(0f) }


    Box(Modifier.fillMaxSize()) {
        state.headerImage?.also {
            LaunchedEffect(key1 = listState) {
                snapshotFlow { listState.layoutInfo }.onEach {
                    val listFirstItem =
                        it.visibleItemsInfo.firstOrNull() ?: return@onEach

                    if (listFirstItem.index != 0) {
                        sizeDp.value = minSizeDp
                        imageOffsetHeightPx.value = -minSizePx
                    }
                    if (listFirstItem.offset < it.viewportStartOffset) {
                        sizeDp.value = minSizeDp
                        imageOffsetHeightPx.value = -minSizePx
                    }

                    val offsetDiff = listFirstItem.offset - it.viewportStartOffset
                    if (offsetDiff < minSizePx) {
                        imageOffsetHeightPx.value =
                            (offsetDiff - minSizePx).coerceIn(-minSizePx, 0f)
                        sizeDp.value = minSizeDp
                    } else {
                        imageOffsetHeightPx.value = 0f
                        sizeDp.value = with(density) { offsetDiff.toDp() }
                    }
                }.launchIn(this)
            }

            Box(
                modifier = Modifier
                    .animateContentSize()
                    .fillMaxWidth()
                    .offset { IntOffset(x = 0, y = imageOffsetHeightPx.value.roundToInt()) }
            ) {
                MediaArtImage(
                    mediaArtImageState = it,
                    modifier = Modifier
                        .size(sizeDp.value)
                        .align(Alignment.Center)
                )
            }

        }
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(top = originalImageHeaderSizeDp)
        ) {
            itemsIndexed(state.trackList, key = { _, item -> item.id }) { index, item ->
                ModelListItem(
                    state = item,
                    modifier = Modifier
                        .clickable {
                            screenDelegate.handle(TrackListUserAction.TrackClicked(index))
                        },
                    trailingContent = {
                        IconButton(
                            onClick = {
                                screenDelegate.handle(
                                    TrackListUserAction.TrackOverflowMenuIconClicked(
                                        index,
                                        item.id,
                                        (item as? ModelListItemState.WithPosition)?.position
                                    )
                                )
                            },
                        ) {
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
}