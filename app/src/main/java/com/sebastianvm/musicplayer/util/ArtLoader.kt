package com.sebastianvm.musicplayer.util

import android.content.ContentUris
import android.provider.MediaStore
import com.sebastianvm.commons.R
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.commons.util.MediaArt

class ArtLoader {

    companion object {
        fun getAlbumArt(albumGid: Long, albumName: String): MediaArt {
            val uri =
                ContentUris.withAppendedId(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumGid
                )
            return MediaArt(
                uris = listOf(uri),
                contentDescription = if (albumName.isNotEmpty()) {
                    DisplayableString.ResourceValue(
                        value = R.string.album_art_for_album,
                        arrayOf(albumName)
                    )
                } else {
                    DisplayableString.ResourceValue(value = R.string.album_art_for_unknown_album)
                },
                backupResource = R.drawable.ic_album,
                backupContentDescription = DisplayableString.ResourceValue(R.string.placeholder_album_art),
            )
        }

        fun getTrackArt(trackGid: Long, albumGid: Long, trackName: String): MediaArt {
            val tracksUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val albumsUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
            val uris = listOf(
                ContentUris.withAppendedId(tracksUri, trackGid),
                ContentUris.withAppendedId(albumsUri, albumGid)
            )
            return MediaArt(
                uris = uris,
                contentDescription = if (trackName.isNotEmpty()) {
                    DisplayableString.ResourceValue(
                        value = R.string.album_art_for_album,
                        arrayOf(trackName)
                    )
                } else {
                    DisplayableString.ResourceValue(value = R.string.album_art_for_unknown_track)
                },
                backupResource = R.drawable.ic_album,
                backupContentDescription = DisplayableString.ResourceValue(R.string.placeholder_album_art),
            )
        }
    }
}