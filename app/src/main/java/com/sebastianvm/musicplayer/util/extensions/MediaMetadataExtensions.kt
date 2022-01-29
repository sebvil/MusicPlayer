package com.sebastianvm.musicplayer.util.extensions

import android.net.Uri
import androidx.core.os.bundleOf
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

inline var MediaMetadata.Builder.albumTitle: String
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        setAlbumTitle(value)
    }

inline var MediaMetadata.Builder.genre: String
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        setGenre(value)
    }

inline var MediaMetadata.Builder.uri: Uri
    get() = throw IllegalAccessException("Cannot get from MediaItem.Builder")
    set(value) {
        setMediaUri(value)
        setArtworkUri(value)
    }

inline var MediaMetadata.Builder.duration: Long
    get() = throw IllegalAccessException("Cannot get from MediaItem.Builder")
    set(value) {
        setExtras(bundleOf(KEY_TRACK_DURATION_MS to value))
    }

const val KEY_TRACK_DURATION_MS =
    "com.sebastianvm.musicplayer.util.extensions.KEY_TRACK_DURATION_MS"