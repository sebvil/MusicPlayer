package com.sebastianvm.musicplayer.model

import androidx.media3.common.MediaItem
import com.sebastianvm.musicplayer.util.extensions.uniqueId

data class BasicQueuedTrack(val trackId: Long, val queuePosition: Int, val queueItemId: Long) {
    companion object {
        fun fromMediaItem(mediaItem: MediaItem, positionInQueue: Int): BasicQueuedTrack =
            BasicQueuedTrack(
                trackId = mediaItem.mediaId.toLong(),
                queuePosition = positionInQueue,
                queueItemId = mediaItem.uniqueId,
            )
    }
}
