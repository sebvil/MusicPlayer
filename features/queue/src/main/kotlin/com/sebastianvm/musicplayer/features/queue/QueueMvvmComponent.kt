package com.sebastianvm.musicplayer.features.queue

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sebastianvm.musicplayer.annotations.MvvmComponent
import com.sebastianvm.musicplayer.core.designsystems.components.MediaArtImage
import com.sebastianvm.musicplayer.core.designsystems.components.Text
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.designsystems.icons.AppIcons
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@MvvmComponent(vmClass = QueueViewModel::class)
@Composable
fun Queue(state: QueueState, handle: Handler<QueueUserAction>, modifier: Modifier = Modifier) {
    when (state) {
        is QueueState.Data -> Queue(state, handle, modifier)
        is QueueState.Loading ->
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Queue(state: QueueState.Data, handle: Handler<QueueUserAction>, modifier: Modifier = Modifier) {
    val lazyListState = rememberLazyListState()

    var queueItems by remember(state.queueItems) { mutableStateOf(state.queueItems) }
    var draggedItemInitialIndex by remember { mutableIntStateOf(-1) }

    var draggedItemFinalIndex by remember { mutableIntStateOf(-1) }

    var draggedItem: Long? by remember { mutableStateOf(null) }
    val nonDraggableItems = 3
    val reorderableLazyListState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            draggedItemFinalIndex = to.index - nonDraggableItems
            queueItems =
                queueItems.toMutableList().apply {
                    add(to.index - nonDraggableItems, removeAt(from.index - nonDraggableItems))
                }
        }
    val view = LocalView.current

    var selectedItems: Set<Int> by rememberSaveable { mutableStateOf(emptySet()) }
    LaunchedEffect(state.queueItems) {
        val itemsNotInQueue =
            selectedItems.filter { item -> item !in state.queueItems.map { it.position } }
        selectedItems = selectedItems - itemsNotInQueue.toSet()
    }

    Box(modifier = modifier) {
        LazyColumn(state = lazyListState, modifier = Modifier) {
            item {
                Text(
                    text = stringResource(RString.now_playing),
                    modifier = Modifier.padding(all = 12.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 20.sp,
                )
            }
            item(key = state.nowPlayingItem.queueItemId) {
                TrackRow(
                    state = state.nowPlayingItem.trackRow,
                    leadingContent = {
                        MediaArtImage(
                            artworkUri = state.nowPlayingItemArtworkUri,
                            modifier = Modifier.size(56.dp),
                        )
                    },
                    modifier = Modifier.animateItem(),
                )
            }

            item {
                Text(
                    text = stringResource(RString.next_up),
                    modifier = Modifier.padding(all = 12.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 20.sp,
                )
            }
            items(queueItems, key = { item -> item.queueItemId }) { item ->
                ReorderableItem(reorderableLazyListState, key = item.queueItemId) { isDragging ->
                    val elevation by
                        animateDpAsState(if (isDragging) 4.dp else 0.dp, label = "elevation")

                    TrackRow(
                        state = item.trackRow,
                        modifier =
                            Modifier.clickable {
                                handle(QueueUserAction.TrackClicked(item.position))
                            },
                        leadingContent = {
                            RadioButton(
                                selected = item.position in selectedItems,
                                onClick = {
                                    selectedItems =
                                        if (item.position in selectedItems) {
                                            selectedItems - item.position
                                        } else {
                                            selectedItems + item.position
                                        }
                                },
                            )
                        },
                        trailingContent = {
                            IconButton(
                                onClick = {},
                                modifier =
                                    Modifier.draggableHandle(
                                        onDragStarted = {
                                            draggedItemInitialIndex = item.position
                                            if (
                                                Build.VERSION.SDK_INT >=
                                                    Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                                            ) {
                                                view.performHapticFeedback(
                                                    HapticFeedbackConstants.DRAG_START
                                                )
                                            }
                                        },
                                        onDragStopped = {
                                            draggedItem = null
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                                view.performHapticFeedback(
                                                    HapticFeedbackConstants.GESTURE_END
                                                )
                                            }
                                            handle(
                                                QueueUserAction.DragEnded(
                                                    from = draggedItemInitialIndex,
                                                    to =
                                                        draggedItemFinalIndex +
                                                            state.nowPlayingItem.position +
                                                            1,
                                                )
                                            )
                                        },
                                    ),
                            ) {
                                Icon(
                                    imageVector = AppIcons.DragIndicator.icon(),
                                    contentDescription = stringResource(RString.drag),
                                )
                            }
                        },
                        tonalElevation = elevation,
                        shadowElevation = elevation,
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = selectedItems.isNotEmpty(),
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Row(
                modifier =
                    Modifier.fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(
                    onClick = {
                        handle(QueueUserAction.RemoveItemsFromQueue(selectedItems.toList()))
                        selectedItems = emptySet()
                    }
                ) {
                    Text(text = stringResource(RString.remove))
                }
            }
        }
    }
}
