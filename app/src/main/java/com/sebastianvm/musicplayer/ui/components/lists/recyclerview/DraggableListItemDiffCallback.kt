package com.sebastianvm.musicplayer.ui.components.lists.recyclerview

import androidx.recyclerview.widget.DiffUtil

class DraggableListItemDiffCallback : DiffUtil.ItemCallback<DraggableListItem>() {
    override fun areItemsTheSame(oldItem: DraggableListItem, newItem: DraggableListItem): Boolean {
        return oldItem.areItemsTheSame(newItem)
    }

    override fun areContentsTheSame(
        oldItem: DraggableListItem,
        newItem: DraggableListItem
    ): Boolean {
        return oldItem.areContentsTheSame(newItem)
    }

}