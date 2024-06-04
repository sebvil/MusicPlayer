package com.sebastianvm.musicplayer.model

import kotlinx.serialization.Serializable

@Serializable
data class NowPlayingInfo(
    val nowPlayingPositionInQueue: Int = -1,
    val lastRecordedPosition: Long = -1L,
)
