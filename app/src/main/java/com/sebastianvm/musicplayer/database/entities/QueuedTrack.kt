package com.sebastianvm.musicplayer.database.entities

import androidx.media3.common.MediaItem
import com.sebastianvm.musicplayer.model.Track
import com.sebastianvm.musicplayer.util.extensions.toMediaItem
import com.sebastianvm.musicplayer.util.extensions.uniqueId

data class QueuedTrack(
    val id: Long,
    val trackName: String,
    val artists: String,
    val queuePosition: Int,
    val queueItemId: Long,
) {
    private fun toTrack(): Track = Track(id, trackName, artists = listOf(), albumId = 0L)

    fun toMediaItem(): MediaItem {
        val item = toTrack().toMediaItem()
        return item.buildUpon().setTag(queuePosition).build()
    }

    companion object {
        fun fromMediaItem(mediaItem: MediaItem, positionInQueue: Int): QueuedTrack =
            QueuedTrack(
                id = mediaItem.mediaId.toLong(),
                trackName = "",
                artists = "",
                queuePosition = positionInQueue,
                queueItemId = mediaItem.uniqueId,
            )
    }
}
