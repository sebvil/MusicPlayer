package com.sebastianvm.musicplayer.ui.queue

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.databinding.ViewHolderSortableListBinding
import com.sebastianvm.musicplayer.ui.components.DraggableTrackRowState
import com.sebastianvm.musicplayer.ui.components.TrackRow
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.lists.recyclerview.DraggableListViewHolder
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions

class TrackRowViewHolder(
    context: Context,
    parent: ViewGroup,
    private val binding: ViewHolderSortableListBinding = ViewHolderSortableListBinding.inflate(
        LayoutInflater.from(
            context
        ), parent, false
    )
) : DraggableListViewHolder<DraggableTrackRowState>(binding.root) {

    override fun bind(viewRenderer: @Composable () -> Unit) {
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                viewRenderer()
            }
        }
    }

}