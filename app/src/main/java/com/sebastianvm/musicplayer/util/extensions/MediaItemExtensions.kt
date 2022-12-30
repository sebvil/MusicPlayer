package com.sebastianvm.musicplayer.util.extensions

import androidx.media3.common.MediaItem


inline val MediaItem.uniqueId: Long
    get() = mediaMetadata.uniqueId