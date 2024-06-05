package com.sebastianvm.musicplayer.player

import com.sebastianvm.musicplayer.database.entities.QueuedTrack

data class PlaybackInfo(
    val queuedTracks: List<QueuedTrack>,
    val nowPlayingId: Long,
    val lastRecordedPosition: Long
)
