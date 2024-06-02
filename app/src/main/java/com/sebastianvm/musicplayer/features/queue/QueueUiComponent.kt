package com.sebastianvm.musicplayer.features.queue

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
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
    var isDragInProgress by remember {
        mutableStateOf(false)
    }

    var queueItems by remember(state.queueItems) {
        mutableStateOf(state.queueItems)
    }

    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        queueItems = queueItems.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    val view = LocalView.current

    Column {
        Text(
            text = "Now playing"
        )
        ModelListItem(
            state = state.nowPlayingItem.modelListItemState,
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
        )

        Text(
            text = "Next up"
        )

        LazyColumn(state = lazyListState, modifier = modifier) {
            items(queueItems, key = { it.position }) { item ->
                ReorderableItem(reorderableLazyListState, key = item.position) { isDragging ->

                    val backgroundColor = if (isDragging) {
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    } else {
                        MaterialTheme.colorScheme.surface
                    }

                    val elevation by animateDpAsState(
                        if (isDragging) 4.dp else 0.dp,
                        label = "elevation"
                    )

                    Surface(shadowElevation = elevation) {
                        ModelListItem(
                            state = item.modelListItemState,
                            modifier = Modifier
                                .clickable {
                                    handle(QueueUserAction.TrackClicked(item.position.toInt()))
                                }
                                .longPressDraggableHandle(
                                    onDragStarted = {
                                        isDragInProgress = true
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                            view.performHapticFeedback(HapticFeedbackConstants.DRAG_START)
                                        }
                                    },
                                    onDragStopped = {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                            view.performHapticFeedback(HapticFeedbackConstants.GESTURE_END)
                                        }
//                                handle(QueueUserAction.DragEnded)
                                    },
                                ),
                            backgroundColor = backgroundColor,
                        )
                    }
                }
            }
        }
    }
}
