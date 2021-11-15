package com.sebastianvm.musicplayer.util.extensions

import android.content.ContentUris
import android.media.browse.MediaBrowser
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo


fun FullTrackInfo.toMediaMetadataCompat(): MediaMetadataCompat {
    return MediaMetadataCompat.Builder().apply {
        val uri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,  this@toMediaMetadataCompat.track.trackGid.toLong()
        )
        mediaUri = uri
        id = this@toMediaMetadataCompat.track.trackGid
        albumId = this@toMediaMetadataCompat.album.albumGid
        title = this@toMediaMetadataCompat.track.trackName
        album = this@toMediaMetadataCompat.album.albumName
        artist = this@toMediaMetadataCompat.artists.joinToString(", ") { it.artistName }
        genre = this@toMediaMetadataCompat.genres.joinToString(", ") { it.genreName }
        trackNumber = this@toMediaMetadataCompat.track.trackNumber
        flags = MediaBrowser.MediaItem.FLAG_PLAYABLE
        duration = this@toMediaMetadataCompat.track.trackDurationMs
    }.build()
}