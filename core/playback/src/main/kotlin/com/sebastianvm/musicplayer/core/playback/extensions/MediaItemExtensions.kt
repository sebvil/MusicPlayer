package com.sebastianvm.musicplayer.core.playback.extensions

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

internal inline var MediaItem.Builder.id: Long
    get() = throw IllegalAccessException("Cannot get from MediaItem.Builder")
    set(value) {
        setMediaId(value.toString())
    }

internal inline var MediaItem.Builder.uri: Uri
    get() = throw IllegalAccessException("Cannot get from MediaItem.Builder")
    set(value) {
        setUri(value)
    }

internal inline var MediaItem.Builder.mediaMetadata: MediaMetadata
    get() = throw IllegalAccessException("Cannot get from MediaItem.Builder")
    set(value) {
        setMediaMetadata(value)
    }

internal inline val MediaItem.uniqueId: Long
    get() = mediaMetadata.uniqueId
