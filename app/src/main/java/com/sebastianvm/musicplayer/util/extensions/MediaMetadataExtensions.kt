package com.sebastianvm.musicplayer.util.extensions

import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaMetadata

inline var MediaMetadata.Builder.title: String
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        setTitle(value)
    }

inline var MediaMetadata.Builder.artist: String
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        setArtist(value)
    }

inline var MediaMetadata.Builder.genre: String
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        setGenre(value)
    }

inline var MediaMetadata.Builder.uri: Uri
    get() = throw IllegalAccessException("Cannot get from MediaItem.Builder")
    set(value) {
        setArtworkUri(value)
    }


inline var MediaMetadata.Builder.isPlayable: Boolean
    get() = throw IllegalAccessException("Cannot get from MediaItem.Builder")
    set(value) {
        setIsPlayable(value)
    }

inline var MediaMetadata.Builder.folderType: Int
    get() = throw IllegalAccessException("Cannot get from MediaItem.Builder")
    set(value) {
        setFolderType(value)
    }

inline val MediaMetadata.duration: Long
    get() = extras?.getLong(KEY_TRACK_DURATION_MS) ?: 0L

inline var Bundle.duration: Long
    get() = throw IllegalAccessException("Cannot get from MediaItem.Builder")
    set(value) {
        putLong(KEY_TRACK_DURATION_MS, value)
    }

inline val MediaMetadata.uniqueId: String
    get() = extras?.getString(KEY_UNIQUE_ID) ?: ""

inline var Bundle.uniqueId: String
    get() = throw IllegalAccessException("Cannot get from MediaItem.Builder")
    set(value) {
        putString(KEY_UNIQUE_ID, value)
    }

inline var MediaMetadata.Builder.extras: Bundle
    get() = throw IllegalAccessException("Cannot get from MediaItem.Builder")
    set(value) {
        setExtras(value)
    }

const val KEY_TRACK_DURATION_MS =
    "com.sebastianvm.musicplayer.util.extensions.KEY_TRACK_DURATION_MS"

const val KEY_UNIQUE_ID =
    "com.sebastianvm.musicplayer.util.extensions.KEY_UNIQUE_ID"