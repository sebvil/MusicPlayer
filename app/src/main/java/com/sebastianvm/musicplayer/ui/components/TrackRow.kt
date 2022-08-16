package com.sebastianvm.musicplayer.ui.components

import com.sebastianvm.musicplayer.database.entities.TrackWithQueueId
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.recyclerview.DraggableListItem
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState

data class DraggableTrackRowState(val uniqueId: String, val trackRowState: ModelListItemState) :
    DraggableListItem() {

    override val id: String = uniqueId
    override fun areContentsTheSame(otherItem: DraggableListItem): Boolean {
        return equals(other = otherItem)
    }
}

fun TrackWithQueueId.toDraggableTrackRowState(): DraggableTrackRowState {
    return DraggableTrackRowState(uniqueQueueItemId, toTrack().toModelListItemState())
}
