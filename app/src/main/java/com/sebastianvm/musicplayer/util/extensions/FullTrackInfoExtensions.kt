package com.sebastianvm.musicplayer.util.extensions

import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaMetadata.FOLDER_TYPE_NONE
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.util.uri.UriUtils
import java.util.UUID

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
        extras = Bundle().apply {
            duration = trackDurationMs
            uniqueId = UUID.randomUUID().toString()
        }
        isPlayable = true
        folderType = FOLDER_TYPE_NONE
    }.build()
}