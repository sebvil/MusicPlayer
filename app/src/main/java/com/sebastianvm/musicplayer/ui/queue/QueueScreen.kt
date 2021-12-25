package com.sebastianvm.musicplayer.ui.queue

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.sebastianvm.musicplayer.ui.components.TrackRow
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.lists.DraggableListItemDelegate
import com.sebastianvm.musicplayer.ui.components.lists.ListItemDelegate
import com.sebastianvm.musicplayer.ui.components.lists.SortableLazyColumn
import com.sebastianvm.musicplayer.ui.components.lists.SortableLazyColumnState
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

@Composable
fun QueueScreen(screenViewModel: QueueViewModel) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = {},
    ) { state ->
        QueueLayout(state = state, delegate = object : DraggableListItemDelegate<TrackRowState> {
            override fun onVerticalDrag(newIndex: Int) {
                screenViewModel.handle(QueueUserAction.ItemDragged(newIndex))
            }

            override fun onDragStart(index: TrackRowState) {
                screenViewModel.handle(QueueUserAction.ItemSelectedForDrag(index))
            }

            override fun onDragEnd() {
                screenViewModel.handle(QueueUserAction.DragEnded)
            }
        })
    }
}


@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun QueueScreenPreview(@PreviewParameter(QueueStatePreviewParameterProvider::class) state: QueueState) {
    ScreenPreview {
        QueueLayout(state = state, delegate = object : DraggableListItemDelegate<TrackRowState> {})
    }
}

@Composable
fun QueueLayout(state: QueueState, delegate: DraggableListItemDelegate<TrackRowState>) {
    SortableLazyColumn(
        state = SortableLazyColumnState(
            state.queueItems,
            state.draggedItemIndex,
            state.draggedItem
        ),
        key = { item -> item.trackId },
        delegate = delegate
    ) { item ->
        TrackRow(state = item, delegate = object : ListItemDelegate {})
    }
}