package com.sebastianvm.musicplayer.model

import com.sebastianvm.musicplayer.database.entities.TrackWithQueuePosition

data class Queue(
    val nowPlayingTrack: TrackWithQueuePosition,
    val nextUp: List<TrackWithQueuePosition>
)
