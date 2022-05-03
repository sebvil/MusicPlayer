package com.sebastianvm.musicplayer.ui.components.lists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.lists.recyclerview.SortableLazyColumnAdapter
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import kotlin.math.roundToInt


interface DraggableListItemDelegate {
    fun onDragStart(index: Int) = Unit
    fun onDragEnd() = Unit
    fun onVerticalDrag(newIndex: Int) = Unit
}

data class SortableLazyColumnState<T>(
    val itemsList: List<T>,
    val selectedIndex: Int,
    val draggedItem: T?
)

interface SortableLazyColumnDelegate {
    fun onMove(from: Int, to: Int)
    fun onItemSelectedForDrag(position: Int)
    fun onDragEnded(initialPosition: Int, finalPosition: Int)
}


@Composable
fun SortableLazyColumnView(items: List<TrackRowState>, delegate: SortableLazyColumnDelegate) {
    val listAdapter = SortableLazyColumnAdapter()
    AndroidView(modifier = Modifier.fillMaxHeight(), factory = {
        RecyclerView(it).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
            ItemTouchHelper(object : ItemTouchHelper.Callback() {
                private var initialPosition = -1

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)
                    setHasFixedSize(true)

                    if (actionState != ItemTouchHelper.ACTION_STATE_IDLE && viewHolder != null) {
                        initialPosition = viewHolder.bindingAdapterPosition
                        delegate.onItemSelectedForDrag(viewHolder.bindingAdapterPosition)
                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    setHasFixedSize(false)

                    delegate.onDragEnded(initialPosition, viewHolder.bindingAdapterPosition)
                    initialPosition = -1
                }

                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val from = viewHolder.absoluteAdapterPosition
                    val to = target.absoluteAdapterPosition
                    delegate.onMove(from, to)
                    return true
                }


                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit

            }).attachToRecyclerView(this)

        }
    },
        update = {
            (it.adapter as? SortableLazyColumnAdapter)?.submitList(items)
        })
}


// TODO improve scrolling behavior
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> SortableLazyColumnIndexed(
    state: SortableLazyColumnState<T>,
    key: ((Int, T) -> Any)?,
    delegate: DraggableListItemDelegate,
    listState: LazyListState = rememberLazyListState(),
    row: @Composable (index: Int, item: T) -> Unit
) {
    var listHeight by remember { mutableStateOf(0f) }
    val draggedItemYOffset = remember { mutableStateOf(0f) }
    var draggedItemHeight by remember { mutableStateOf(0f) }
    val haptic = LocalHapticFeedback.current


    val hoveredIndex by remember {
        derivedStateOf {
            ((draggedItemYOffset.value + listState.firstVisibleItemScrollOffset) / draggedItemHeight + listState.firstVisibleItemIndex).let { num ->
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
            listHeight = it.height.toFloat()
        }
        .fillMaxHeight()
    ) {
        LazyColumn(state = listState) {
            itemsIndexed(state.itemsList, key = key) { index, item ->
                Box(modifier = Modifier
                    .animateItemPlacement()
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                delegate.onDragStart(index)
                            },
                            onDragEnd = {
                                delegate.onDragEnd()
                            }) { _, dragAmount ->
                            val originalY = draggedItemYOffset.value
                            val newValue = originalY + dragAmount.y
                            draggedItemYOffset.value = newValue
                            delegate.onVerticalDrag(hoveredIndex)
                        }
                    }) {
                    if (item == state.draggedItem) {
                        val dpHeight = with(LocalDensity.current) {
                            draggedItemHeight.toDp()
                        }
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dpHeight),
                            tonalElevation = (0.1).dp
                        ) {}
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_drag),
                                contentDescription = stringResource(R.string.drag),
                                modifier = Modifier.padding(start = AppDimensions.spacing.medium)
                            )
                            row(index, item)
                        }
                    }
                }

            }

        }
        state.draggedItem?.also { item ->
            var heightSet by remember { mutableStateOf(false) }
            LaunchedEffect(draggedItemYOffset.value, listState.firstVisibleItemIndex) {
                if (draggedItemYOffset.value > listHeight - draggedItemHeight) {
                    listState.scrollBy(draggedItemYOffset.value - listHeight + draggedItemHeight)
                } else if (draggedItemYOffset.value <= draggedItemHeight) {
                    listState.scrollBy(draggedItemYOffset.value - draggedItemHeight)
                }
            }
            Surface(
                modifier = Modifier
                    .onSizeChanged { size ->
                        draggedItemHeight = size.height.toFloat()
                        if (!heightSet) {
                            draggedItemYOffset.value =
                                ((state.selectedIndex - listState.firstVisibleItemIndex) * draggedItemHeight - listState.firstVisibleItemScrollOffset)
                            heightSet = true
                        }

                    }
                    .offset {
                        IntOffset(0, draggedItemYOffset.value.roundToInt())
                    },
                tonalElevation = 0.5.dp,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_drag),
                        contentDescription = stringResource(id = R.string.drag),
                        modifier = Modifier.padding(start = AppDimensions.spacing.medium)
                    )
                    row(-1, item)
                }
            }
        }
    }
}

//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun <T> SortableLazyColumn(
//    state: SortableLazyColumnState<T>,
//    key: ((T) -> Any)?,
//    delegate: DraggableListItemDelegate<T>,
//    listState: LazyListState = rememberLazyListState(),
//    row: @Composable (item: T) -> Unit
//) {
//    SortableLazyColumnIndexed(
//        state = state,
//        key = key?.let { { _, item -> key(item) } },
//        delegate = delegate,
//        listState = listState) { _, item ->
//        row(item)
//    }
//}