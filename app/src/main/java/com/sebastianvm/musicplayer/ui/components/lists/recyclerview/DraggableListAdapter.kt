package com.sebastianvm.musicplayer.ui.components.lists.recyclerview

import androidx.compose.runtime.Composable
import androidx.recyclerview.widget.ListAdapter

abstract class DraggableListAdapter<T : DraggableListItem, V : DraggableListViewHolder<T>>(open val itemRenderer: @Composable (index: Int, item: T) -> Unit) :
    ListAdapter<DraggableListItem, V>(DraggableListItemDiffCallback())
