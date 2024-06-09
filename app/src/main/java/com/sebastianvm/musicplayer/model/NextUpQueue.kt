package com.sebastianvm.musicplayer.model

data class NextUpQueue(val nowPlayingTrack: QueuedTrack, val nextUp: List<QueuedTrack>)
