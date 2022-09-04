package com.sebastianvm.musicplayer.ui.library.tracklist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface


@Composable
fun TrackListScreen(
    screenViewModel: TrackListViewModel,
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
        fab = { state ->
            if (state.trackListType == TrackListType.PLAYLIST) {
                ExtendedFloatingActionButton(
                    text = { Text(text = stringResource(id = R.string.add_tracks)) },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_plus),
                            contentDescription = "Plus"
                        )
                    },
                    onClick = { screenViewModel.handle(TrackListUserAction.AddTracksClicked) }
                )
            }

        },
        navigationDelegate = navigationDelegate,
        topBar = {
            LibraryTopBar(
                state = LibraryTopBarState(
                    it.trackListName ?: stringResource(id = R.string.all_songs),
                    hasSortButton = it.trackListType != TrackListType.ALBUM
                ),
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
        TrackListLayout(viewModel = screenViewModel, listState = listState)
    }
}


@Composable
fun TrackListLayout(
    viewModel: ViewModelInterface<TrackListState, TrackListUserAction>,
    listState: LazyListState = rememberLazyListState(),
) {
    val state by viewModel.state.collectAsState()
    PlaybackStatusIndicator(
        playbackResult = state.playbackResult,
        delegate = object : PlaybackStatusIndicatorDelegate {
            override fun onDismissRequest() {
                viewModel.handle(TrackListUserAction.DismissPlaybackErrorDialog)
            }
        })

    val maxWidth = 500.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val size = min(screenWidth - 200.dp, maxWidth)
    LazyColumn(state = listState) {
        state.headerImage?.also {
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    MediaArtImage(
                        mediaArtImageState = it,
                        modifier = Modifier
                            .size(size)
                            .align(Alignment.Center)
                    )
                }
            }
        }
        itemsIndexed(state.trackList, key = { _, item -> item.id }) { index, item ->
            ModelListItem(
                state = item,
                modifier = Modifier
                    .clickable {
                        viewModel.handle(TrackListUserAction.TrackClicked(index))
                    },
                trailingContent = {
                    IconButton(
                        onClick = {
                            viewModel.handle(
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