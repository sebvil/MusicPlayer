package com.sebastianvm.musicplayer.player

import com.sebastianvm.musicplayer.database.entities.TrackWithQueueId

data class PlaybackInfo(
    val queuedTracks: List<TrackWithQueueId>,
    val nowPlayingId: String,
    val lastRecordedPosition: Long
)