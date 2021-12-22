package com.sebastianvm.musicplayer.ui.queue

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntOffset
import com.sebastianvm.musicplayer.ui.components.TrackRow
import com.sebastianvm.musicplayer.ui.components.lists.ListItemDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import kotlin.math.roundToInt

@Composable
fun QueueScreen(screenViewModel: QueueViewModel) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            TODO("Not yet implemented")
        },
    ) { state ->
        QueueLayout(state = state, delegate = object : DraggableListItemDelegate {
            override fun onVerticalDrag(oldIndex: Int, newIndex: Int) {
                screenViewModel.handle(QueueUserAction.ItemDragged(oldIndex, newIndex))
            }

            override fun onDragStart(index: Int) {
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
        QueueLayout(state = state, delegate = object : DraggableListItemDelegate {})
    }
}

interface DraggableListItemDelegate {
    fun onDragStart(index: Int) = Unit
    fun onDragEnd() = Unit
    fun onDragCancel() = Unit
    fun onVerticalDrag(oldIndex: Int, newIndex: Int) = Unit
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun QueueLayout(
    state: QueueState,
    listState: LazyListState = rememberLazyListState(),
    delegate: DraggableListItemDelegate,
) {

    var boxHeight by remember { mutableStateOf(0f) }

    Box(modifier = Modifier
        .onSizeChanged {
            boxHeight = it.height.toFloat()
        }
    ) {
        LazyColumn(state = listState) {
            itemsIndexed(state.queueItems, key = { _, item -> item.trackId }) { index, item ->
                TrackRow(
                    state = item,
                    delegate = object : ListItemDelegate {
                        override fun onItemLongPressed() {
                            delegate.onDragStart(index)
                        }
                    },
                    modifier = Modifier
                        .animateItemPlacement()
                )
            }
        }

        state.draggedItem?.also {
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val firstVisibleIndexScroll = listState.firstVisibleItemScrollOffset
            val offsetX = remember { mutableStateOf(0f) }
            val offsetY = remember { mutableStateOf(0f) }
            var height by remember { mutableStateOf(0f) }
            var heightSet by remember {
                mutableStateOf(false)
            }

            val hoveredIndex by remember {
                derivedStateOf {
                    ((offsetY.value + listState.firstVisibleItemScrollOffset) / height + listState.firstVisibleItemIndex).let { num ->
                        if (num.isNaN()) {
                            num.toInt()
                        } else {
                            num.roundToInt()
                        }
                    }
                }
            }
            val oldIndex by remember {
                derivedStateOf { state.draggedItemIndex }
            }

            LaunchedEffect(offsetY.value, listState.firstVisibleItemIndex) {
                Log.i("QUEUE", "Scroll: ${offsetY.value}, $boxHeight")
                if (offsetY.value > boxHeight - 100f) {
                    listState.scrollBy(offsetY.value - boxHeight + 100f)
                }
            }
            TrackRow(
                state = it,
                delegate = object : ListItemDelegate {},
                modifier = Modifier
                    .onSizeChanged { size ->
                        height = size.height.toFloat()
                        if (!heightSet) {
                            offsetY.value =
                                ((state.draggedItemIndex - firstVisibleIndex) * height - firstVisibleIndexScroll)
                            heightSet = true
                        }

                    }
                    .offset {
                        IntOffset(
                            offsetX.value.roundToInt(),
                            offsetY.value.roundToInt()
                        )
                    }
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(onDragEnd = delegate::onDragEnd) { _, dragAmount ->
                            val originalY = offsetY.value
                            val newValue = originalY + dragAmount
                            offsetY.value = newValue.coerceIn(0f, boxHeight)
                            delegate.onVerticalDrag(
                                oldIndex,
                                hoveredIndex
                            )
                        }
                    }
            )
        }

    }

}



