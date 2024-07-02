package com.sebastianvm.musicplayer.core.playback.extensions

import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.sebastianvm.musicplayer.core.data.UriUtils
import com.sebastianvm.musicplayer.core.model.Track
import java.util.UUID

fun Track.toMediaItem(): MediaItem {
    return MediaItem.Builder()
        .apply {
            id = this@toMediaItem.id
            uri = UriUtils.getTrackUri(trackId = this@toMediaItem.id)
            mediaMetadata = getMediaMetadata()
        }
        .build()
}

fun Track.getMediaMetadata(): MediaMetadata {
    return MediaMetadata.Builder()
        .apply {
            title = name
            artist = artists.joinToString { it.name }
            artworkUri = UriUtils.getAlbumUri(albumId = albumId)
            extras = Bundle().apply { uniqueId = UUID.randomUUID().mostSignificantBits }
            isPlayable = true
            setIsBrowsable(false)
        }
        .build()
}
