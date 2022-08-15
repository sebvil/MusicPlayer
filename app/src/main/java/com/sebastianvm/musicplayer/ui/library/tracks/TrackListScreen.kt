package com.sebastianvm.musicplayer.ui.library.tracks

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicator
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicatorDelegate
import com.sebastianvm.musicplayer.ui.components.TrackRow
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview


@Composable
fun TrackListScreen(
    screenViewModel: TrackListViewModel = viewModel(),
    navigationDelegate: NavigationDelegate,
) {
    val listState = rememberLazyListState()

    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is TrackListUiEvent.ScrollToTop -> listState.scrollToItem(0)
            }
        },
        navigationDelegate = navigationDelegate,
        topBar = { state ->
            LibraryTopBar(
                title = state.trackListName ?: stringResource(id = R.string.all_songs),
                delegate = object : LibraryTopBarDelegate {
                    override fun upButtonClicked() {
                        screenViewModel.onUpButtonClicked()
                    }

                    override fun sortByClicked() {
                        screenViewModel.onSortByClicked()
                    }
                })
        },
    ) { state ->
        TrackListLayout(
            state = state,
            listState = listState,
            delegate = object : TrackListScreenDelegate {
                override fun onTrackClicked(trackIndex: Int) {
                    screenViewModel.onTrackClicked(trackIndex)
                }

                override fun onOverflowMenuIconClicked(trackIndex: Int, trackId: Long) {
                    screenViewModel.onTrackOverflowMenuIconClicked(trackIndex, trackId)
                }

                override fun onDismissRequest() {
                    screenViewModel.onClosePlaybackErrorDialog()
                }
            })
    }
}


interface TrackListScreenDelegate : PlaybackStatusIndicatorDelegate {
    fun onTrackClicked(trackIndex: Int) = Unit
    fun onOverflowMenuIconClicked(trackIndex: Int, trackId: Long) = Unit
}

@ScreenPreview
@Composable
fun TrackListScreenPreview(@PreviewParameter(TrackListStatePreviewParameterProvider::class) state: TrackListState) {
    val listState = rememberLazyListState()
    ScreenPreview(topBar = {
        LibraryTopBar(
            title = state.trackListName ?: stringResource(id = R.string.all_songs),
            delegate = object : LibraryTopBarDelegate {})
    }) {
        TrackListLayout(
            state = state,
            listState = listState,
            delegate = object : TrackListScreenDelegate {}
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrackListLayout(
    state: TrackListState,
    listState: LazyListState,
    delegate: TrackListScreenDelegate
) {
    PlaybackStatusIndicator(playbackResult = state.playbackResult, delegate = delegate)
    LazyColumn(state = listState) {
        itemsIndexed(state.trackList, key = { _, item -> item.trackId }) { index, item ->
            TrackRow(state = item, modifier = Modifier
                .animateItemPlacement()
                .clickable {
                    delegate.onTrackClicked(index)
                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            delegate.onOverflowMenuIconClicked(
                                index,
                                item.trackId
                            )
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_overflow),
                            contentDescription = stringResource(R.string.more),
                        )
                    }
                })
        }
    }
}
