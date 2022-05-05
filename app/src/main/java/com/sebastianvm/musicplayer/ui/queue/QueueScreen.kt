package com.sebastianvm.musicplayer.ui.queue

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.recyclerview.widget.LinearLayoutManager
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.TrackRow
import com.sebastianvm.musicplayer.ui.components.lists.DraggableColumnList
import com.sebastianvm.musicplayer.ui.components.lists.DraggableColumnListDelegate
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

@Composable
fun QueueScreen(screenViewModel: QueueViewModel) {
    val layoutManager = LinearLayoutManager(LocalContext.current)
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is QueueUiEvent.ScrollToNowPlayingItem -> {
                    layoutManager.scrollToPositionWithOffset(event.index, 0)
                }
            }
        },
    ) { state ->
        QueueLayout(state = state, delegate = object : QueueScreenDelegate {
            override fun onMove(from: Int, to: Int) {
                screenViewModel.onMove(to)
            }

            override fun onItemSelectedForDrag(position: Int) {
                screenViewModel.onItemSelectedForDrag(position = position)
            }

            override fun onDragEnded(initialPosition: Int, finalPosition: Int) {
                screenViewModel.onDragEnded(
                    initialPosition = initialPosition,
                    finalPosition = finalPosition
                )
            }

            override fun onTrackClicked(trackIndex: Int) {
                screenViewModel.onTrackClicked(trackIndex = trackIndex)
            }

            override fun onContextMenuItemClicked(trackId: String) {
                screenViewModel.onTrackOverflowMenuClicked(trackId = trackId)
            }

        }, layoutManager = layoutManager)
    }
}


@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun QueueScreenPreview(@PreviewParameter(QueueStatePreviewParameterProvider::class) state: QueueState) {
    ScreenPreview {
        QueueLayout(
            state = state, delegate = object : QueueScreenDelegate {}, LinearLayoutManager(
                LocalContext.current
            )
        )
    }
}

interface QueueScreenDelegate : DraggableColumnListDelegate {
    fun onTrackClicked(trackIndex: Int) = Unit
    fun onContextMenuItemClicked(trackId: String) = Unit
}

@Composable
fun QueueLayout(
    state: QueueState,
    delegate: QueueScreenDelegate,
    layoutManager: LinearLayoutManager
) {
    DraggableColumnList(
        items = state.queueItems,
        delegate = delegate,
        listAdapter = QueueAdapter { index, item ->
            val isNowPlayingItem = item.uniqueId == state.nowPlayingId
            val backgroundColor = if (isNowPlayingItem) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(color = backgroundColor).clickable { delegate.onTrackClicked(index)}
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_drag),
                    contentDescription = stringResource(R.string.drag),
                    modifier = Modifier.padding(start = AppDimensions.spacing.medium)
                )
                TrackRow(
                    state = item.trackRowState,
                    color = contentColorFor(backgroundColor = backgroundColor),
                    onOverflowMenuIconClicked = { },
                )
            }
        },
        layoutManager = layoutManager
    )
}
