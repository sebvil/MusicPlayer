package com.sebastianvm.musicplayer.util.extensions

import android.net.Uri
import androidx.media3.common.MediaItem


inline var MediaItem.Builder.uri: Uri
    get() = throw IllegalAccessException("Cannot get from MediaItem.Builder")
    set(value) {
        setUri(value)
    }


inline val MediaItem.uniqueId: Long
    get() = mediaMetadata.uniqueId