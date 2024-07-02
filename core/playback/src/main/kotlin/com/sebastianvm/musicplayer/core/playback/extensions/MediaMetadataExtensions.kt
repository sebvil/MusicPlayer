package com.sebastianvm.musicplayer.core.playback.extensions

import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaMetadata

internal inline var MediaMetadata.Builder.title: String
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        setTitle(value)
    }

internal inline var MediaMetadata.Builder.artist: String
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        setArtist(value)
    }

internal inline var MediaMetadata.Builder.genre: String
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        setGenre(value)
    }

internal inline var MediaMetadata.Builder.artworkUri: Uri
    get() = throw IllegalAccessException("Cannot get from MediaItem.Builder")
    set(value) {
        setArtworkUri(value)
    }

internal inline var MediaMetadata.Builder.isPlayable: Boolean
    get() = throw IllegalAccessException("Cannot get from MediaItem.Builder")
    set(value) {
        setIsPlayable(value)
    }

internal inline val MediaMetadata.duration: Long
    get() = extras?.getLong(KEY_TRACK_DURATION_MS) ?: 0L

internal inline var Bundle.duration: Long
    get() = throw IllegalAccessException("Cannot get from MediaItem.Builder")
    set(value) {
        putLong(KEY_TRACK_DURATION_MS, value)
    }

internal inline val MediaMetadata.uniqueId: Long
    get() = extras?.getLong(KEY_UNIQUE_ID) ?: 0L

internal inline var Bundle.uniqueId: Long
    get() = throw IllegalAccessException("Cannot get from MediaItem.Builder")
    set(value) {
        putLong(KEY_UNIQUE_ID, value)
    }

internal inline var MediaMetadata.Builder.extras: Bundle
    get() = throw IllegalAccessException("Cannot get from MediaItem.Builder")
    set(value) {
        setExtras(value)
    }

private const val KEY_TRACK_DURATION_MS =
    "com.sebastianvm.musicplayer.util.extensions.KEY_TRACK_DURATION_MS"

private const val KEY_UNIQUE_ID = "com.sebastianvm.musicplayer.util.extensions.KEY_UNIQUE_ID"
