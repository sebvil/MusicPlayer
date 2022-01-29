package com.sebastianvm.musicplayer.util.extensions

import android.content.ContentUris
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo

fun FullTrackInfo.toMediaItem(): MediaItem {
    return MediaItem.Builder().apply {
        id = track.trackId
        uri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            track.trackId.toLong()
        )
        mediaMetadata = getMediaMetadata()
    }.build()
}

fun FullTrackInfo.getMediaMetadata(): MediaMetadata {
    return MediaMetadata.Builder().apply {
        title = track.trackName
        artist = artists.joinToString(", ") { it.artistName }
        uri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            track.trackId.toLong()
        )
        duration = track.trackDurationMs
    }.build()
}