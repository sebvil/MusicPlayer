package com.sebastianvm.musicplayer.ui.components.lists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


interface DraggableListItemDelegate<T> {
    fun onDragStart(index: T) = Unit
    fun onDragEnd() = Unit
    fun onVerticalDrag(newIndex: Int) = Unit
}

data class SortableLazyColumnState<T>(
    val itemsList: List<T>,
    val selectedIndex: Int,
    val draggedItem: T?
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> SortableLazyColumn(
    state: SortableLazyColumnState<T>,
    key: ((T) -> Any)?,
    delegate: DraggableListItemDelegate<T>,
    listState: LazyListState = rememberLazyListState(),
    row: @Composable (item: T) -> Unit
) {
    var boxHeight by remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }
    val haptic = LocalHapticFeedback.current


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

    Box(modifier = Modifier
        .onSizeChanged {
            boxHeight = it.height.toFloat()
        }
        .fillMaxHeight()
    ) {
        LazyColumn(state = listState) {
            items(state.itemsList, key = key) { item ->
                Box(modifier = Modifier
                    .animateItemPlacement()
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                delegate.onDragStart(item)
                            },
                            onDragEnd = {
                                delegate.onDragEnd()
                            }) { _, dragAmount ->
                            val originalY = offsetY.value
                            val newValue = originalY + dragAmount.y
                            offsetY.value = newValue
                            delegate.onVerticalDrag(hoveredIndex)
                        }
                    }) {
                    if (item == state.draggedItem) {
                        val dpHeight = with(LocalDensity.current) {
                            height.toDp()
                        }
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dpHeight),
                            tonalElevation = (0.1).dp
                        ) {}
                    } else {
                        row(item)
                    }
                }

            }

        }
        state.draggedItem?.also { item ->
            var heightSet by remember { mutableStateOf(false) }
            LaunchedEffect(offsetY.value, listState.firstVisibleItemIndex) {
                if (offsetY.value > boxHeight - height) {
                    listState.scrollBy(offsetY.value - boxHeight + height)
                } else if (offsetY.value <= height) {
                    listState.scrollBy(offsetY.value - height)
                }
            }
            Surface(
                modifier = Modifier
                    .onSizeChanged { size ->
                        height = size.height.toFloat()
                        if (!heightSet) {
                            offsetY.value =
                                ((state.selectedIndex - listState.firstVisibleItemIndex) * height - listState.firstVisibleItemScrollOffset)
                            heightSet = true
                        }

                    }
                    .offset {
                        IntOffset(0, offsetY.value.roundToInt())
                    },
                tonalElevation = 0.5.dp,
            ) {
                row(item)
            }
        }
    }
}