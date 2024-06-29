package com.sebastianvm.musicplayer.core.model

data class QueuedTrack(val track: Track, val queuePosition: Int, val queueItemId: Long)
