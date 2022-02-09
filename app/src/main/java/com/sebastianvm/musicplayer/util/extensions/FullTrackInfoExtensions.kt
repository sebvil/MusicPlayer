package com.sebastianvm.musicplayer.util.extensions

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.util.uri.UriUtils

fun Track.toMediaItem(): MediaItem {
    return MediaItem.Builder().apply {
        id = trackId
        uri = UriUtils.getTrackUri(trackId = trackId.toLong())
        mediaMetadata = getMediaMetadata()
    }.build()
}

fun Track.getMediaMetadata(): MediaMetadata {
    return MediaMetadata.Builder().apply {
        title = trackName
        artist = artists
        uri = UriUtils.getTrackUri(trackId = trackId.toLong())
        duration = trackDurationMs
    }.build()
}