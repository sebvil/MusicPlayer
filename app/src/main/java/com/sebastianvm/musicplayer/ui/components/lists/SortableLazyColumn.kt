package com.sebastianvm.musicplayer.ui.components.lists

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.lists.recyclerview.SortableLazyColumnAdapter


interface SortableLazyColumnDelegate {
    fun onMove(from: Int, to: Int) = Unit
    fun onItemSelectedForDrag(position: Int) = Unit
    fun onDragEnded(initialPosition: Int, finalPosition: Int) = Unit
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