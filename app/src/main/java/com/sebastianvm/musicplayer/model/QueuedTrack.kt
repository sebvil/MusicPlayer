package com.sebastianvm.musicplayer.model

import androidx.media3.common.MediaItem
import com.sebastianvm.musicplayer.util.extensions.toMediaItem

data class QueuedTrack(val track: Track, val queuePosition: Int, val queueItemId: Long) {

    fun toMediaItem(): MediaItem {
        val item = track.toMediaItem()
        return item.buildUpon().setTag(queuePosition).build()
    }
}
