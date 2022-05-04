package com.sebastianvm.musicplayer.database.entities

import androidx.media3.common.MediaItem

data class TrackWithQueueId(
    val trackId: String,
    val trackName: String,
    val trackNumber: Long,
    val trackDurationMs: Long,
    val albumName: String,
    val albumId: String,
    val artists: String,
    val uniqueQueueItemId: String
) {
    fun toTrack(): Track =
        Track(trackId, trackName, trackNumber, trackDurationMs, albumName, albumId, artists)

    companion object {
        fun fromMediaItem(mediaItem: MediaItem): TrackWithQueueId = TrackWithQueueId(
            trackId = mediaItem.mediaId,
            trackName = "",
            trackNumber = 0,
            trackDurationMs = 0,
            albumName = "",
            albumId = "",
            artists = "",
            uniqueQueueItemId = mediaItem.localConfiguration?.tag?.toString() ?: ""
        )

    }
}
