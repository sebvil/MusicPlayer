package com.sebastianvm.musicplayer.model

import com.sebastianvm.musicplayer.database.entities.QueuedTrack

data class FullQueue(val nowPlayingInfo: NowPlayingInfo, val queue: List<QueuedTrack>)
