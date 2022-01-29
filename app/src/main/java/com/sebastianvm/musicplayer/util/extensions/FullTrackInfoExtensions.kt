package com.sebastianvm.musicplayer.util.extensions

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.util.uri.UriUtils

fun FullTrackInfo.toMediaItem(): MediaItem {
    return MediaItem.Builder().apply {
        id = track.trackId
        uri = UriUtils.getTrackUri(trackId = track.trackId.toLong())
        mediaMetadata = getMediaMetadata()
    }.build()
}

fun FullTrackInfo.getMediaMetadata(): MediaMetadata {
    return MediaMetadata.Builder().apply {
        title = track.trackName
        artist = artists.joinToString(", ") { it.artistName }
        uri = UriUtils.getTrackUri(trackId = track.trackId.toLong())
        duration = track.trackDurationMs
    }.build()
}