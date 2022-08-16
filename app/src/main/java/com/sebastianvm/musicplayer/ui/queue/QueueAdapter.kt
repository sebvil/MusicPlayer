package com.sebastianvm.musicplayer.ui.queue

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemStateWithPosition
import com.sebastianvm.musicplayer.ui.components.lists.recyclerview.DraggableListAdapter

class QueueAdapter(override val itemRenderer: @Composable (index: Int, item: ModelListItemStateWithPosition) -> Unit) :
    DraggableListAdapter<ModelListItemStateWithPosition, TrackRowViewHolder>(itemRenderer) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackRowViewHolder {
        return TrackRowViewHolder(context = parent.context, parent = parent)
    }

    override fun onBindViewHolder(holder: TrackRowViewHolder, position: Int) {
        val item = getItem(position) as? ModelListItemStateWithPosition
        item?.also {
            holder.bind {
                itemRenderer(position, item)
            }
        }
    }
}