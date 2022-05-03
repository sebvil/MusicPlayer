package com.sebastianvm.musicplayer.ui.queue

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.sebastianvm.musicplayer.ui.components.lists.DraggableColumnList
import com.sebastianvm.musicplayer.ui.components.lists.DraggableColumnListDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

@Composable
fun QueueScreen(screenViewModel: QueueViewModel) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = {},
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

        })
    }
}


@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun QueueScreenPreview(@PreviewParameter(QueueStatePreviewParameterProvider::class) state: QueueState) {
    ScreenPreview {
        QueueLayout(state = state, delegate = object : QueueScreenDelegate {})
    }
}

interface QueueScreenDelegate : DraggableColumnListDelegate {
    fun onTrackClicked(trackIndex: Int) = Unit
    fun onContextMenuItemClicked(trackId: String) = Unit
}

@Composable
fun QueueLayout(state: QueueState, delegate: QueueScreenDelegate) {
    DraggableColumnList(state.queueItems, delegate = delegate, listAdapter = QueueAdapter())
}
