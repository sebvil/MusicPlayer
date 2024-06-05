package com.sebastianvm.musicplayer.model

import com.sebastianvm.musicplayer.database.entities.QueuedTrack

data class NextUpQueue(
    val nowPlayingTrack: QueuedTrack,
    val nextUp: List<QueuedTrack>
)
