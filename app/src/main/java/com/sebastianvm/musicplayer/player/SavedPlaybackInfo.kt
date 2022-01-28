package com.sebastianvm.musicplayer.player

data class SavedPlaybackInfo(val currentQueue: MediaGroup, val mediaId: String, val lastRecordedPosition: Long)
