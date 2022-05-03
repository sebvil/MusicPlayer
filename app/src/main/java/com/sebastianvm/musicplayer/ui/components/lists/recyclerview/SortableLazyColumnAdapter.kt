package com.sebastianvm.musicplayer.ui.components.lists.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.sebastianvm.musicplayer.ui.components.TrackRowState

class SortableLazyColumnAdapter :
    ListAdapter<TrackRowState, TrackRowViewHolder>(SortableLazyItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackRowViewHolder {
        return TrackRowViewHolder(parent = parent, context = parent.context)
    }

    override fun onBindViewHolder(holder: TrackRowViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item = item)
    }

}

class SortableLazyItemDiffCallback : DiffUtil.ItemCallback<TrackRowState>() {
    override fun areItemsTheSame(oldItem: TrackRowState, newItem: TrackRowState): Boolean {
        return oldItem.trackId == newItem.trackId
    }

    override fun areContentsTheSame(oldItem: TrackRowState, newItem: TrackRowState): Boolean {
        return oldItem == newItem
    }

}