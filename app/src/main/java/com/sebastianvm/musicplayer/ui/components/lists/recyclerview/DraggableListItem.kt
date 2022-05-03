package com.sebastianvm.musicplayer.ui.components.lists.recyclerview

abstract class DraggableListItem {
    abstract val id: String

    fun areItemsTheSame(otherItem: DraggableListItem): Boolean = otherItem.id == id

    abstract fun areContentsTheSame(otherItem: DraggableListItem): Boolean
}