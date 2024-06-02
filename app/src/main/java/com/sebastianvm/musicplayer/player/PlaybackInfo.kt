package com.sebastianvm.musicplayer.player

import com.sebastianvm.musicplayer.database.entities.TrackWithQueuePosition

data class PlaybackInfo(
    val queuedTracks: List<TrackWithQueuePosition>,
    val nowPlayingId: Long,
    val lastRecordedPosition: Long
)
