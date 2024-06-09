package com.sebastianvm.musicplayer.model

data class FullQueue(val nowPlayingInfo: NowPlayingInfo, val queue: List<QueuedTrack>)
