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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.DefaultViewModelInterfaceProvider
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface


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
                        screenViewModel.handle(TrackListUserAction.UpButtonClicked)
                    }

                    override fun sortByClicked() {
                        screenViewModel.handle(TrackListUserAction.SortByButtonClicked)
                    }
                })
        },
    ) {
        TrackListLayout(
            viewModel = screenViewModel,
            listState = listState
        )
    }
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
            viewModel = DefaultViewModelInterfaceProvider.getDefaultInstance(state),
            listState = listState
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrackListLayout(
    viewModel: ViewModelInterface<TrackListState, TrackListUserAction>,
    listState: LazyListState,
) {
    val state by viewModel.state.collectAsState()
    PlaybackStatusIndicator(
        playbackResult = state.playbackResult,
        delegate = object : PlaybackStatusIndicatorDelegate {
            override fun onDismissRequest() {
                viewModel.handle(TrackListUserAction.DismissPlaybackErrorDialog)
            }
        })

    LazyColumn(state = listState) {
        itemsIndexed(state.trackList, key = { _, item -> item.id }) { index, item ->
            ModelListItem(
                state = item,
                modifier = Modifier
                    .animateItemPlacement()
                    .clickable {
                        viewModel.handle(TrackListUserAction.TrackClicked(index))
                    },
                trailingContent = {
                    IconButton(
                        onClick = {
                            viewModel.handle(
                                TrackListUserAction.TrackOverflowMenuIconClicked(
                                    index,
                                    item.id
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
