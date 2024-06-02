package com.sebastianvm.musicplayer.model

import kotlinx.serialization.Serializable

@Serializable
data class NowPlayingInfo(
    val nowPlayingPositionInQueue: Long = -1L,
    val lastRecordedPosition: Long = -1L,
)
