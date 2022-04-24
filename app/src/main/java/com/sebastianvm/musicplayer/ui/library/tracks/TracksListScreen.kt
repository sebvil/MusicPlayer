package com.sebastianvm.musicplayer.ui.library.tracks

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.components.TrackRow
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview


interface TracksListScreenNavigationDelegate {
    fun navigateToPlayer()
    fun navigateUp()
    fun openSortMenu(mediaId: String)
    fun openContextMenu(mediaId: String, mediaGroup: MediaGroup, trackIndex: Int)
}

@Composable
fun TracksListScreen(
    screenViewModel: TracksListViewModel = viewModel(),
    delegate: TracksListScreenNavigationDelegate
) {
    val listState = rememberLazyListState()

    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is TracksListUiEvent.NavigateToPlayer -> {
                    delegate.navigateToPlayer()
                }
                is TracksListUiEvent.ShowSortBottomSheet -> {
                    delegate.openSortMenu(mediaId = event.mediaId)
                }
                is TracksListUiEvent.OpenContextMenu -> {
                    delegate.openContextMenu(mediaId = event.trackId, mediaGroup = event.mediaGroup, trackIndex = event.trackIndex)
                }
                is TracksListUiEvent.NavigateUp -> delegate.navigateUp()
                is TracksListUiEvent.ScrollToTop -> listState.scrollToItem(0)
            }
        },
        topBar = { state ->
            LibraryTopBar(
                title = state.tracksListName.takeUnless { it.isEmpty() }
                    ?: stringResource(id = R.string.all_songs),
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
        TracksListLayout(
            state = state,
            listState = listState,
            delegate = object : TracksListScreenDelegate {
                override fun onTrackClicked(trackIndex: Int) {
                    screenViewModel.onTrackClicked(trackIndex)
                }

                override fun onOverflowMenuIconClicked(trackIndex: Int, trackId: String) {
                    screenViewModel.onTrackOverflowMenuIconClicked(trackIndex, trackId)
                }
            })
    }
}


interface TracksListScreenDelegate {
    fun onTrackClicked(trackIndex: Int) = Unit
    fun onOverflowMenuIconClicked(trackIndex: Int, trackId: String) = Unit
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TracksListScreenPreview(@PreviewParameter(TracksListStatePreviewParameterProvider::class) state: TracksListState) {
    val listState = rememberLazyListState()
    ScreenPreview(topBar = {
        LibraryTopBar(
            title = state.tracksListName,
            delegate = object : LibraryTopBarDelegate {})
    }) {
        TracksListLayout(
            state = state,
            listState = listState,
            delegate = object : TracksListScreenDelegate {}
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TracksListLayout(
    state: TracksListState,
    listState: LazyListState,
    delegate: TracksListScreenDelegate
) {
    LazyColumn(state = listState) {
        itemsIndexed(state.tracksList, key = { _, item -> item.trackId }) { index, item ->
            TrackRow(
                state = item,
                modifier = Modifier
                    .animateItemPlacement()
                    .clickable {
                        delegate.onTrackClicked(index)
                    },
                onOverflowMenuIconClicked = { delegate.onOverflowMenuIconClicked(index, item.trackId) }
            )
        }
    }
}
