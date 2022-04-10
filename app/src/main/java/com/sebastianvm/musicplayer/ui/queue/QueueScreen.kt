package com.sebastianvm.musicplayer.ui.queue

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.MediaQueue
import com.sebastianvm.musicplayer.ui.components.TrackRow
import com.sebastianvm.musicplayer.ui.components.lists.DraggableListItemDelegate
import com.sebastianvm.musicplayer.ui.components.lists.SortableLazyColumnIndexed
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

            override fun onDragStart(index: Int) {
                screenViewModel.handle(QueueUserAction.ItemSelectedForDrag(index))
            }

            override fun onDragEnd() {
                screenViewModel.handle(QueueUserAction.DragEnded)
            }

            override fun onTrackClicked(trackId: String) {
                screenViewModel.handle(QueueUserAction.TrackClicked(trackId))
            }

            override fun optionChosen(queue: MediaQueue) {
                screenViewModel.handle(QueueUserAction.DropdownMenuOptionChosen(queue))
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

interface QueueScreenDelegate : DraggableListItemDelegate {
    fun onTrackClicked(trackId: String) = Unit
    fun onContextMenuItemClicked(trackId: String) = Unit
    fun optionChosen(queue: MediaQueue) = Unit
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueLayout(state: QueueState, delegate: QueueScreenDelegate) {
    val focusManager = LocalFocusManager.current

    var isDropdownExpanded by remember { mutableStateOf(false) }
    LaunchedEffect(
        key1 = isDropdownExpanded,
        block = { if (!isDropdownExpanded) focusManager.clearFocus(force = true) })
    Column {
        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isExpanded ->
                isDropdownExpanded = isExpanded
            },
            modifier = Modifier
                .padding(horizontal = AppDimensions.spacing.medium)
                .padding(top = AppDimensions.spacing.medium)
        ) {
            OutlinedTextField(
                readOnly = true,
                value = state.chosenQueue?.queueName ?: stringResource(id = R.string.no_queue),
                onValueChange = { },
                label = { Text(text = stringResource(id = R.string.queue)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = state.dropdownExpanded
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false },
            ) {
                state.queues.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(text = selectionOption.queueName) },
                        onClick = {
                            delegate.optionChosen(selectionOption)
                            isDropdownExpanded = false
                        },
                    )
                }
            }
        }

        SortableLazyColumnIndexed(
            state = SortableLazyColumnState(
                state.queueItems,
                state.draggedItemFinalIndex,
                state.draggedItem
            ),
            key = { _, item -> "${item.trackRowState.trackId}-${item.queuePosition}" },
            delegate = delegate
        ) { index, queueItem ->
            val item = queueItem.trackRowState
            if (index == state.nowPlayingTrackIndex && state.mediaGroup == state.chosenQueue?.toMediaGroup() && state.draggedItemFinalIndex == -1) {
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
