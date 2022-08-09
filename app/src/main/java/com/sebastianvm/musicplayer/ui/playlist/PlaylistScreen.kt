package com.sebastianvm.musicplayer.ui.playlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicator
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicatorDelegate
import com.sebastianvm.musicplayer.ui.components.TrackRow
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview


@Composable
fun PlaylistScreen(screenViewModel: PlaylistViewModel, navigationDelegate: NavigationDelegate) {
    val listState = rememberLazyListState()
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is PlaylistUiEvent.ScrollToTop -> {
                    listState.animateScrollToItem(0)
                }
            }
        },
        navigationDelegate = navigationDelegate,
        topBar = {
            LibraryTopBar(title = it.playlistName,
                delegate = object : LibraryTopBarDelegate {
                    override fun upButtonClicked() {
                        screenViewModel.onUpButtonClicked()
                    }

                    override fun sortByClicked() {
                        screenViewModel.onSortByClicked()
                    }
                })
        },
        fab = {
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(id = R.string.add_tracks)) },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_plus),
                        contentDescription = "Plus"
                    )
                },
                onClick = { screenViewModel.onAddTracksClicked() })
        }) { state ->
        PlaylistLayout(
            state = state,
            listState = listState,
            delegate = object : PlaylistScreenDelegate {

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


@ScreenPreview
@Composable
fun PlaylistScreenPreview(@PreviewParameter(PlaylistStatePreviewParameterProvider::class) state: PlaylistState) {
    ScreenPreview(topBar = {
        LibraryTopBar(title = state.playlistName, delegate = object : LibraryTopBarDelegate {})
    }, fab = {
        ExtendedFloatingActionButton(
            text = { Text(text = stringResource(R.string.add_tracks)) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = "Plus"
                )
            },
            onClick = { })
    }) {
        PlaylistLayout(
            state = state,
            listState = rememberLazyListState(),
            delegate = object : PlaylistScreenDelegate {})
    }
}

interface PlaylistScreenDelegate : PlaybackStatusIndicatorDelegate {
    fun onTrackClicked(trackIndex: Int) = Unit
    fun onOverflowMenuIconClicked(trackIndex: Int, trackId: Long) = Unit
}

@Composable
fun PlaylistLayout(
    state: PlaylistState,
    listState: LazyListState,
    delegate: PlaylistScreenDelegate
) {
    PlaybackStatusIndicator(playbackResult = state.playbackResult, delegate = delegate)

    if (state.trackList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                stringResource(R.string.playlist_without_tracks),
                modifier = Modifier
                    .padding(horizontal = AppDimensions.spacing.large)
                    .align(
                        Alignment.Center
                    ),
                textAlign = TextAlign.Center
            )
        }

    } else {
        LazyColumn(state = listState) {
            itemsIndexed(state.trackList) { index, item ->
                TrackRow(
                    state = item,
                    modifier = Modifier
                        .clickable {
                            delegate.onTrackClicked(index)
                        },
                    onOverflowMenuIconClicked = {
                        delegate.onOverflowMenuIconClicked(
                            index,
                            item.trackId
                        )
                    }
                )
            }
        }

    }
}
