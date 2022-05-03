package com.sebastianvm.musicplayer.ui.queue

import android.view.ViewGroup
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.lists.recyclerview.DraggableListAdapter

class QueueAdapter : DraggableListAdapter<TrackRowState, TrackRowViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackRowViewHolder {
        return TrackRowViewHolder(context = parent.context, parent = parent)
    }

    override fun onBindViewHolder(holder: TrackRowViewHolder, position: Int) {
        val item = getItem(position) as? TrackRowState
        item?.also {
            holder.bind(item = item)
        }
    }
}