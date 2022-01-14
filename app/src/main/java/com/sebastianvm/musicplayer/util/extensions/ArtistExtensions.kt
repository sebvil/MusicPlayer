package com.sebastianvm.musicplayer.util.extensions

import android.media.browse.MediaBrowser
import android.support.v4.media.MediaMetadataCompat
import com.sebastianvm.musicplayer.database.entities.Artist

fun Artist.toMediaMetadataCompat(): MediaMetadataCompat {
    return MediaMetadataCompat.Builder().apply {
        id = this@toMediaMetadataCompat.artistName
        artist = this@toMediaMetadataCompat.artistName
        flags = MediaBrowser.MediaItem.FLAG_BROWSABLE or MediaBrowser.MediaItem.FLAG_PLAYABLE
    }.build()
}
