package com.sebastianvm.musicplayer.database.entities

import androidx.media3.common.MediaItem
import com.sebastianvm.musicplayer.util.extensions.toMediaItem
import com.sebastianvm.musicplayer.util.extensions.uniqueId

data class TrackWithQueueId(
    val id: Long,
    val trackName: String,
    val trackNumber: Long,
    val trackDurationMs: Long,
    val albumName: String,
    val albumId: Long,
    val artists: String,
    val path: String,
    val uniqueQueueItemId: Long
) {
    private fun toTrack(): Track =
        Track(id, trackName, trackNumber, trackDurationMs, albumName, albumId, artists, path)

    fun toMediaItem(): MediaItem {
        val item = toTrack().toMediaItem()
        return item.buildUpon().setTag(uniqueQueueItemId).build()
    }

    companion object {
        fun fromMediaItem(mediaItem: MediaItem): TrackWithQueueId = TrackWithQueueId(
            id = mediaItem.mediaId.toLong(),
            trackName = "",
            trackNumber = 0,
            trackDurationMs = 0,
            albumName = "",
            albumId = 0,
            artists = "",
            path = "",
            uniqueQueueItemId = mediaItem.uniqueId
        )
    }
}
