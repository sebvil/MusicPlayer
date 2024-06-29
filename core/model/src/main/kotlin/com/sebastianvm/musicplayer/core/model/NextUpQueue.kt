package com.sebastianvm.musicplayer.core.model

data class NextUpQueue(val nowPlayingTrack: QueuedTrack, val nextUp: List<QueuedTrack>)
