package com.sebastianvm.musicplayer.util.extensions

import android.support.v4.media.MediaMetadataCompat
import com.sebastianvm.musicplayer.database.entities.Genre

fun Genre.toMediaMetadataCompat(): MediaMetadataCompat {
    return MediaMetadataCompat.Builder().apply {
        id = this@toMediaMetadataCompat.genreName
        genre = this@toMediaMetadataCompat.genreName
    }.build()
}