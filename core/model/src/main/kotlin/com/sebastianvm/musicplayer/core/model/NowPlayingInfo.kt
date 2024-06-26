package com.sebastianvm.musicplayer.core.model

data class NowPlayingInfo(
    val nowPlayingPositionInQueue: Int = -1,
    val lastRecordedPosition: Long = -1L,
)
