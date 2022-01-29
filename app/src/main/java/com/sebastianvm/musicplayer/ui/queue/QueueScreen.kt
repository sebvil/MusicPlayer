package com.sebastianvm.musicplayer.ui.queue

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.sebastianvm.musicplayer.database.entities.MediaQueue
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.components.M3ExposedDropDownMenu
import com.sebastianvm.musicplayer.ui.components.M3ExposedDropDownMenuDelegate
import com.sebastianvm.musicplayer.ui.components.M3ExposedDropDownMenuState
import com.sebastianvm.musicplayer.ui.components.TrackRow
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.lists.DraggableListItemDelegate
import com.sebastianvm.musicplayer.ui.components.lists.SortableLazyColumn
import com.sebastianvm.musicplayer.ui.components.lists.SortableLazyColumnState
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

@Composable
fun QueueScreen(screenViewModel: QueueViewModel) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = {},
    ) { state ->
        QueueLayout(state = state, delegate = object : QueueScreenDelegate {
            override fun onVerticalDrag(newIndex: Int) {
                screenViewModel.handle(QueueUserAction.ItemDragged(newIndex))
            }

            override fun onDragStart(index: TrackRowState) {
                screenViewModel.handle(QueueUserAction.ItemSelectedForDrag(index))
            }

            override fun onDragEnd() {
                screenViewModel.handle(QueueUserAction.DragEnded)
            }

            override fun onTrackClicked(trackId: String) {
                screenViewModel.handle(QueueUserAction.TrackClicked(trackId))
            }

            override fun toggleExpanded() {
                screenViewModel.handle(QueueUserAction.DropdownMenuClicked)
            }

            override fun optionChosen(newOption: MediaQueue) {
                screenViewModel.handle(QueueUserAction.DropdownMenuOptionChosen(newOption))
            }

            override fun getOptionDisplayName(option: MediaQueue): String {
                return option.queueName
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

interface QueueScreenDelegate : DraggableListItemDelegate<TrackRowState>,
    M3ExposedDropDownMenuDelegate<MediaQueue> {
    fun onTrackClicked(trackId: String) = Unit
    fun onContextMenuItemClicked(trackId: String) = Unit
}

@Composable
fun QueueLayout(state: QueueState, delegate: QueueScreenDelegate) {
    Column {
        M3ExposedDropDownMenu(
            state = M3ExposedDropDownMenuState(
                expanded = state.dropdownExpanded,
                label = "Queue",
                options = state.queues,
                chosenOption = state.chosenQueue ?: MediaQueue(MediaType.ALL_TRACKS, "", "No queue")
            ),
            delegate = delegate,
            modifier = Modifier.padding(horizontal = AppDimensions.spacing.medium).padding(top = AppDimensions.spacing.medium)
        )

        SortableLazyColumn(
            state = SortableLazyColumnState(
                state.queueItems,
                state.draggedItemFinalIndex,
                state.draggedItem
            ),
            key = { item -> item.trackId },
            delegate = delegate
        ) { item ->
            if (item.trackId == state.nowPlayingTrackId) {
                TrackRow(
                    state = item,
                    modifier = Modifier.clickable {
                        delegate.onTrackClicked(item.trackId)
                    },
                    onOverflowMenuIconClicked = { delegate.onContextMenuItemClicked(item.trackId) },
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                TrackRow(
                    state = item,
                    modifier = Modifier.clickable {
                        delegate.onTrackClicked(item.trackId)
                    },
                    onOverflowMenuIconClicked = { delegate.onContextMenuItemClicked(item.trackId) })
            }
        }
    }
}
