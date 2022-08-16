package com.sebastianvm.musicplayer.ui.queue

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.recyclerview.widget.LinearLayoutManager
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.lists.DraggableColumnList
import com.sebastianvm.musicplayer.ui.components.lists.DraggableColumnListDelegate
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

@Composable
fun QueueScreen(screenViewModel: QueueViewModel, navigationDelegate: NavigationDelegate) {
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
        navigationDelegate = navigationDelegate
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


@ScreenPreview
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
            ModelListItem(
                state = item.trackRowState,
                modifier = Modifier
                    .clickable {
                        delegate.onTrackClicked(index)
                    },
                backgroundColor = backgroundColor,
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_drag),
                        contentDescription = stringResource(R.string.drag),
                    )
                },
                trailingContent = {
                    IconButton(
                        onClick = { /* TODO */ },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_overflow),
                            contentDescription = stringResource(R.string.more),
                        )
                    }
                })
        },
        layoutManager = layoutManager
    )
}
