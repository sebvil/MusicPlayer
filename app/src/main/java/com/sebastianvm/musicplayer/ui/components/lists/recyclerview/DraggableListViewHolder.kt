package com.sebastianvm.musicplayer.ui.components.lists.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class DraggableListViewHolder<T: DraggableListItem>(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(item: T)
}