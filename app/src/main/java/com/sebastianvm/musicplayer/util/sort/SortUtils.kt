package com.sebastianvm.musicplayer.util.sort

import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.R
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable

interface SortOptions

@Serializable
data class SortPreferences(
    val allTracksListSortPreferences: MediaSortPreferences<TrackListSortOptions> = MediaSortPreferences(sortOption = TrackListSortOptions.TRACK),
    val genreTracksListSortPreferences: PersistentMap<String, MediaSortPreferences<TrackListSortOptions>> = persistentMapOf(),
    val albumListSortPreferences: MediaSortPreferences<AlbumListSortOptions> = MediaSortPreferences(sortOption = AlbumListSortOptions.ALBUM),
    val artistListSortOrder: MediaSortOrder = MediaSortOrder.ASCENDING,
    val genreListSortOrder: MediaSortOrder = MediaSortOrder.ASCENDING,
    val playlistListSortOrder: MediaSortOrder = MediaSortOrder.ASCENDING,
    val playlistSortPreferences: PersistentMap<String, MediaSortPreferences<PlaylistSortOptions>> = persistentMapOf()
)

@Serializable
data class MediaSortPreferences<T: SortOptions>(val sortOption: T, val sortOrder: MediaSortOrder = MediaSortOrder.ASCENDING)

@Serializable
enum class TrackListSortOptions(@StringRes val stringId: Int) : SortOptions {
    TRACK(R.string.track_name),
    ARTIST(R.string.artist_name),
    ALBUM(R.string.album_name)
}

@Serializable
enum class AlbumListSortOptions(@StringRes val stringId: Int) : SortOptions {
    ALBUM(R.string.album_name),
    ARTIST(R.string.artist_name),
    YEAR(R.string.year)
}

@Serializable
enum class PlaylistSortOptions : SortOptions {
    CUSTOM,
    TRACK,
    ARTIST,
    ALBUM
}

enum class MediaSortOrder {
    ASCENDING,
    DESCENDING
}

operator fun MediaSortOrder.not(): MediaSortOrder {
    return when (this) {
        MediaSortOrder.ASCENDING -> MediaSortOrder.DESCENDING
        MediaSortOrder.DESCENDING -> MediaSortOrder.ASCENDING
    }
}


