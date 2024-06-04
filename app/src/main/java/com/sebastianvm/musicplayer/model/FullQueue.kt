package com.sebastianvm.musicplayer.model

import com.sebastianvm.musicplayer.database.entities.TrackWithQueuePosition

data class FullQueue(
    val nowPlayingInfo: NowPlayingInfo,
    val queue: List<TrackWithQueuePosition>
)
