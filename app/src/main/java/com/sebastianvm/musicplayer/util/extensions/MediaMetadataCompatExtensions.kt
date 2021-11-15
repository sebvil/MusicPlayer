package com.sebastianvm.musicplayer.util.extensions

import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import com.sebastianvm.musicplayer.util.AlbumType


inline val MediaMetadataCompat.id: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)

inline var MediaMetadataCompat.Builder.id: String
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, value)
    }


inline val MediaMetadataCompat.title: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_TITLE)

inline var MediaMetadataCompat.Builder.title: String
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_TITLE, value)
    }


inline val MediaMetadataCompat.artist: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_ARTIST)

inline var MediaMetadataCompat.Builder.artist: String
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_ARTIST, value)
    }


inline val MediaMetadataCompat.album: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_ALBUM)

inline var MediaMetadataCompat.Builder.album: String
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_ALBUM, value)
    }


inline val MediaMetadataCompat.genre: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_GENRE)

inline var MediaMetadataCompat.Builder.genre: String
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_GENRE, value)
    }


inline val MediaMetadataCompat.year: Long
    get() = getLong(MediaMetadataCompat.METADATA_KEY_YEAR)


inline var MediaMetadataCompat.Builder.year: Long
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putLong(MediaMetadataCompat.METADATA_KEY_YEAR, value)
    }

inline val MediaMetadataCompat.albumArtists: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST)

inline var MediaMetadataCompat.Builder.albumArtists: String
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, value)
    }


inline val MediaMetadataCompat.trackNumber: Long
    get() = getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER)

inline var MediaMetadataCompat.Builder.trackNumber: Long
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, value)
    }

inline val MediaMetadataCompat.numberOfTracks: Long
    get() = getLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS)

inline var MediaMetadataCompat.Builder.numberOfTracks: Long
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, value)
    }

inline val MediaMetadataCompat.duration: Long
    get() = getLong(MediaMetadataCompat.METADATA_KEY_DURATION)

inline var MediaMetadataCompat.Builder.duration: Long
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putLong(MediaMetadataCompat.METADATA_KEY_DURATION, value)
    }


inline val MediaMetadataCompat.mediaUri: Uri?
    get() = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)?.toUri()

inline var MediaMetadataCompat.Builder.mediaUri: Uri
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, value.toString())
    }

inline val MediaMetadataCompat.flags: Int
    get() = getLong(METADATA_KEY_FLAGS).toInt()

inline var MediaMetadataCompat.Builder.flags: Int
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putLong(METADATA_KEY_FLAGS, value.toLong())
    }


inline val MediaMetadataCompat.iconRes: Int
    get() = getLong(METADATA_KEY_ICON_RES).toInt()

inline var MediaMetadataCompat.Builder.iconRes: Int
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putLong(METADATA_KEY_ICON_RES, value.toLong())
    }

inline val MediaMetadataCompat.counts: Long
    get() = getLong(METADATA_KEY_COUNTS)

inline var MediaMetadataCompat.Builder.counts: Long
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putLong(METADATA_KEY_COUNTS, value)
    }


inline val MediaMetadataCompat.albumType: AlbumType?
    get() = try {
        getString(METADATA_KEY_ALBUM_TYPE)?.let {
            AlbumType.valueOf(it)
        }
    } catch (e: IllegalArgumentException) {
        null
    }

inline var MediaMetadataCompat.Builder.albumType: AlbumType
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(METADATA_KEY_ALBUM_TYPE, value.name)
    }

inline val MediaMetadataCompat.albumId: String?
    get() = getString(METADATA_KEY_ALBUM_ID)

inline var MediaMetadataCompat.Builder.albumId: String
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(METADATA_KEY_ALBUM_ID, value)
    }


const val METADATA_KEY_FLAGS = "com.sebastianvm.musicplayer.util.extensions.FLAGS"
const val METADATA_KEY_ICON_RES = "com.sebastianvm.musicplayer.util.extensions.ICON_RES"
const val METADATA_KEY_COUNTS = "com.sebastianvm.musicplayer.util.extensions.COUNTS"
const val METADATA_KEY_ALBUM_TYPE = "com.sebastianvm.musicplayer.util.extensions.ALBUM_TYPE"
const val METADATA_KEY_ALBUM_ID = "com.sebastianvm.musicplayer.util.extensions.ALBUM_ID"
const val MEDIA_METADATA_COMPAT_KEY = "com.sebastianvm.musicplayer.util.extensions.MEDIA_METADATA_COMPAT"
