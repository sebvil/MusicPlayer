package com.sebastianvm.musicplayer.util.extensions

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

inline var MediaItem.Builder.id: String
    get() = throw IllegalAccessException("Cannot get from MediaItem.Builder")
    set(value) {
        setMediaId(value)
    }

inline var MediaItem.Builder.uri: Uri
    get() = throw IllegalAccessException("Cannot get from MediaItem.Builder")
    set(value) {
        setUri(value)
    }

inline var MediaItem.Builder.mediaMetadata: MediaMetadata
    get() = throw IllegalAccessException("Cannot get from MediaItem.Builder")
    set(value) {
        setMediaMetadata(value)
    }

inline val MediaItem.uniqueId: String
    get() = mediaMetadata.uniqueId