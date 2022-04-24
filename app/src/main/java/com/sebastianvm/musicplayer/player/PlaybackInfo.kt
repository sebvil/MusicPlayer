package com.sebastianvm.musicplayer.player

import com.sebastianvm.musicplayer.database.entities.Track

data class PlaybackInfo(
    val queuedTracks: List<Track>,
    val nowPlayingIndex: Int,
    val lastRecordedPosition: Long
)