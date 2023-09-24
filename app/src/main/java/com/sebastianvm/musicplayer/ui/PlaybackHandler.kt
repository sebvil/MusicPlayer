package com.sebastianvm.musicplayer.ui

import com.sebastianvm.musicplayer.player.MediaGroup

fun interface PlaybackHandler {
    fun handlePlayback(mediaGroup: MediaGroup, initialTrackIndex: Int)

    operator fun invoke(mediaGroup: MediaGroup, initialTrackIndex: Int) {
        handlePlayback(mediaGroup, initialTrackIndex)
    }
}
