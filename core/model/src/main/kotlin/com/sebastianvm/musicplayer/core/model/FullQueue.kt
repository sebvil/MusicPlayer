package com.sebastianvm.musicplayer.core.model

data class FullQueue(val nowPlayingInfo: NowPlayingInfo, val queue: List<QueuedTrack>)
