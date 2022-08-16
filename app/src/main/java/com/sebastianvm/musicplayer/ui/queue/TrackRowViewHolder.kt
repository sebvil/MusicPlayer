package com.sebastianvm.musicplayer.ui.queue

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.sebastianvm.musicplayer.databinding.ViewHolderSortableListBinding
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemStateWithPosition
import com.sebastianvm.musicplayer.ui.components.lists.recyclerview.DraggableListViewHolder

class TrackRowViewHolder(
    context: Context,
    parent: ViewGroup,
    private val binding: ViewHolderSortableListBinding = ViewHolderSortableListBinding.inflate(
        LayoutInflater.from(
            context
        ), parent, false
    )
) : DraggableListViewHolder<ModelListItemStateWithPosition>(binding.root) {

    override fun bind(viewRenderer: @Composable () -> Unit) {
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                viewRenderer()
            }
        }
    }

}