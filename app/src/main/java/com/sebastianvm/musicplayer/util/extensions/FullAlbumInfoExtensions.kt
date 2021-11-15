package com.sebastianvm.musicplayer.util.extensions

import android.media.browse.MediaBrowser
import android.support.v4.media.MediaMetadataCompat
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo

fun FullAlbumInfo.toMediaMetadataCompat() : MediaMetadataCompat {
    return MediaMetadataCompat.Builder().apply {
        id = this@toMediaMetadataCompat.album.albumGid
        album = this@toMediaMetadataCompat.album.albumName
        albumArtists = this@toMediaMetadataCompat.artists.joinToString(", ") { it.artistName }
        year = this@toMediaMetadataCompat.album.year
        numberOfTracks = this@toMediaMetadataCompat.album.numberOfTracks
        flags = MediaBrowser.MediaItem.FLAG_BROWSABLE or MediaBrowser.MediaItem.FLAG_PLAYABLE
    }.build()
}