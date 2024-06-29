package com.sebastianvm.model

data class NextUpQueue(val nowPlayingTrack: QueuedTrack, val nextUp: List<QueuedTrack>)
