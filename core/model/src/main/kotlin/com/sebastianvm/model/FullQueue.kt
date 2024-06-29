package com.sebastianvm.model

data class FullQueue(val nowPlayingInfo: NowPlayingInfo, val queue: List<QueuedTrack>)
