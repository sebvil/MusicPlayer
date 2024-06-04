package com.sebastianvm.musicplayer.features.queue

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.NoArguments
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

object QueueUiComponent :
    BaseUiComponent<NoArguments, QueueState, QueueUserAction, QueueStateHolder>() {
    override val arguments: NoArguments = NoArguments

    override fun createStateHolder(dependencies: DependencyContainer): QueueStateHolder {
        return getQueueStateHolder(dependencies)
    }

    @Composable
    override fun Content(state: QueueState, handle: Handler<QueueUserAction>, modifier: Modifier) {
        Queue(state, handle, modifier)
    }
}

@Composable
fun Queue(state: QueueState, handle: Handler<QueueUserAction>, modifier: Modifier = Modifier) {
    when (state) {
        is QueueState.Empty -> {
            TODO()
        }

        is QueueState.Data -> Queue(state, handle, modifier)
        is QueueState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Queue(state: QueueState.Data, handle: Handler<QueueUserAction>, modifier: Modifier = Modifier) {
    val lazyListState = rememberLazyListState()

    var queueItems by remember(state.queueItems) {
        mutableStateOf(state.queueItems)
    }
    var draggedItemInitialIndex by remember {
        mutableIntStateOf(-1)
    }

    var draggedItemFinalIndex by remember {
        mutableIntStateOf(-1)
    }

    val nonDraggableItems = 3
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        draggedItemFinalIndex = to.index - nonDraggableItems
        queueItems = queueItems.toMutableList().apply {
            add(to.index - nonDraggableItems, removeAt(from.index - nonDraggableItems))
        }
    }
    val view = LocalView.current

    LazyColumn(state = lazyListState, modifier = modifier) {
        item {
            Text(
                text = stringResource(R.string.now_playing),
                modifier = Modifier.padding(all = 12.dp),
                style = MaterialTheme.typography.titleMedium,
                fontSize = 20.sp
            )
        }
        item(key = state.nowPlayingItem.position) {
            ModelListItem(
                state = state.nowPlayingItem.modelListItemState,
                modifier = Modifier.animateItem(),
            )
        }

        item {
            Text(
                text = stringResource(R.string.next_up),
                modifier = Modifier.padding(all = 12.dp),
                style = MaterialTheme.typography.titleMedium,
                fontSize = 20.sp
            )
        }
        items(queueItems, key = { item -> item.position }) { item ->
            ReorderableItem(reorderableLazyListState, key = item.position) { isDragging ->
                val elevation by animateDpAsState(
                    if (isDragging) 4.dp else 0.dp,
                    label = "elevation"
                )

                ModelListItem(
                    state = item.modelListItemState,
                    modifier = Modifier
                        .clickable {
                            handle(QueueUserAction.TrackClicked(item.position))
                        },
                    trailingContent = {
                        IconButton(
                            onClick = {},
                            modifier = Modifier.draggableHandle(
                                onDragStarted = {
                                    draggedItemInitialIndex = item.position
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                        view.performHapticFeedback(HapticFeedbackConstants.DRAG_START)
                                    }
                                },
                                onDragStopped = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                        view.performHapticFeedback(HapticFeedbackConstants.GESTURE_END)
                                    }
                                    handle(
                                        QueueUserAction.DragEnded(
                                            from = draggedItemInitialIndex,
                                            to = draggedItemFinalIndex + state.nowPlayingItem.position + 1
                                        )
                                    )
                                },
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.DragIndicator,
                                contentDescription = stringResource(R.string.drag)
                            )
                        }
                    },
                    tonalElevation = elevation,
                    shadowElevation = elevation,
                )
            }
        }
    }
}
