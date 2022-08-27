package com.sebastianvm.musicplayer.ui.components.lists.tracklist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
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
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicator
import com.sebastianvm.musicplayer.ui.components.PlaybackStatusIndicatorDelegate
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.mvvm.events.HandleEvents
import com.sebastianvm.musicplayer.ui.util.mvvm.events.HandleNavEvents

@Composable
fun TrackList(
    viewModel: TrackListViewModel,
    navigationDelegate: NavigationDelegate,
    contentPadding: PaddingValues = PaddingValues(),
    listState: LazyListState = rememberLazyListState(),
    preListContent: @Composable (() -> Unit)? = null
) {
//    val listState = rememberLazyListState()
    HandleNavEvents(viewModel = viewModel, navigationDelegate = navigationDelegate)
    HandleEvents(viewModel = viewModel) { event ->
        when (event) {
            is TrackListUiEvent.ScrollToTop -> listState.scrollToItem(0)
        }
    }
    val state by viewModel.state.collectAsState()
    PlaybackStatusIndicator(
        playbackResult = state.playbackResult,
        delegate = object : PlaybackStatusIndicatorDelegate {
            override fun onDismissRequest() {
                viewModel.handle(TrackListUserAction.DismissPlaybackErrorDialog)
            }
        })

    LazyColumn(state = listState, contentPadding = contentPadding) {
        preListContent?.also {
            item {
                it()
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