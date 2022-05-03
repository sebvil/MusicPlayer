package com.sebastianvm.musicplayer.ui.components.lists.recyclerview

import androidx.recyclerview.widget.ListAdapter

abstract class DraggableListAdapter<T : DraggableListItem, V : DraggableListViewHolder<T>> :
    ListAdapter<DraggableListItem, V>(DraggableListItemDiffCallback())

