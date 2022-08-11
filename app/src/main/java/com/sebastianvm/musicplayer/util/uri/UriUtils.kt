package com.sebastianvm.musicplayer.util.uri

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore

object UriUtils {

    fun getAlbumUri(albumId: Long): Uri =
        ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId)

    fun getAlbumUriString(albumId: Long): String =
        "${MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI}/$albumId"

    fun getTrackUri(trackId: Long): Uri =
        ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, trackId)

}