package com.sebastianvm.musicplayer.ui.library.tracks

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.components.TrackRow
import com.sebastianvm.musicplayer.ui.components.lists.ListItemDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder


interface TracksListScreenNavigationDelegate {
    fun navigateToPlayer()
    fun navigateUp()
    fun openSortMenu(sortOption: Int, sortOrder: SortOrder)
    fun openContextMenu(
        mediaId: String,
        mediaGroup: MediaGroup,
        currentSort: SortOption,
        sortOrder: SortOrder
    )
}

@OptIn(ExperimentalMaterialApi::class)
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
                    delegate.openSortMenu(event.sortOption, event.sortOrder)
                }
                is TracksListUiEvent.OpenContextMenu -> {
                    delegate.openContextMenu(
                        mediaId = event.trackId,
                        mediaGroup = event.genreName?.let { MediaGroup(MediaType.GENRE, it) }
                            ?: MediaGroup(MediaType.TRACK, event.trackId),
                        currentSort = event.currentSort,
                        sortOrder = event.sortOrder
                    )
                }
                is TracksListUiEvent.NavigateUp -> delegate.navigateUp()
                is TracksListUiEvent.ScrollToTop -> listState.scrollToItem(0)
            }
        },
        topBar = { state ->
            LibraryTopBar(title = state.tracksListTitle, delegate = object : LibraryTopBarDelegate {
                override fun upButtonClicked() {
                    screenViewModel.handle(TracksListUserAction.UpButtonClicked)
                }

                override fun sortByClicked() {
                    screenViewModel.handle(TracksListUserAction.SortByClicked)
                }
            })
        },
    ) { state ->
        TracksListLayout(
            state = state,
            listState = listState,
            delegate = object : TracksListScreenDelegate {
                override fun onTrackClicked(trackId: String) {
                    screenViewModel.handle(
                        TracksListUserAction.TrackClicked(
                            trackId
                        )
                    )
                }

                override fun onTrackLongPressed(trackId: String) {
                    screenViewModel.handle(
                        TracksListUserAction.TrackContextMenuClicked(
                            trackId
                        )
                    )
                }
            })
    }
}


interface TracksListScreenDelegate {
    fun onTrackClicked(trackId: String)
    fun onTrackLongPressed(trackId: String) = Unit
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TracksListScreenPreview(@PreviewParameter(TracksListStatePreviewParameterProvider::class) state: TracksListState) {
    val listState = rememberLazyListState()
    ScreenPreview(topBar = {
        LibraryTopBar(title = state.tracksListTitle, delegate = object : LibraryTopBarDelegate {})
    }) {
        TracksListLayout(
            state = state,
            listState = listState,
            delegate = object : TracksListScreenDelegate {
                override fun onTrackClicked(trackId: String) = Unit
            })
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun TracksListLayout(
    state: TracksListState,
    listState: LazyListState,
    delegate: TracksListScreenDelegate
) {
    LazyColumn(state = listState) {
        items(state.tracksList, key = { it.trackId }) { item ->
            TrackRow(
                state = item,
                delegate = object : ListItemDelegate {
                    override fun onItemClicked() {
                        delegate.onTrackClicked(item.trackId)
                    }

                    override fun onSecondaryActionIconClicked() {
                        delegate.onTrackLongPressed(item.trackId)
                    }
                },
                modifier = Modifier.animateItemPlacement()
            )
        }
    }
}


