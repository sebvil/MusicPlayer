package com.sebastianvm.musicplayer.util.sort

import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.util.resources.RString
import com.sebastianvm.musicplayer.util.serialization.GenreSortPrefsSerializer
import com.sebastianvm.musicplayer.util.serialization.PlaylistSortPrefsSerializer
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable

sealed interface SortOptions {
    val stringId: Int

    @Serializable
    enum class TrackListSortOptions(@StringRes override val stringId: Int) : SortOptions {
        TRACK(RString.track_name),
        ARTIST(RString.artist_name),
        ALBUM(RString.album_name)
    }

    @Serializable
    enum class AlbumListSortOptions(@StringRes override val stringId: Int) : SortOptions {
        ALBUM(RString.album_name),
        ARTIST(RString.artist_name),
        YEAR(RString.year)
    }

    @Serializable
    enum class PlaylistSortOptions(@StringRes override val stringId: Int) : SortOptions {
        CUSTOM(RString.custom),
        TRACK(RString.track_name),
        ARTIST(RString.artist_name),
        ALBUM(RString.album_name)
    }
}

@Serializable
data class SortPreferences(
    val allTrackListSortPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions> =
        MediaSortPreferences(sortOption = SortOptions.TrackListSortOptions.TRACK),
    @Serializable(with = GenreSortPrefsSerializer::class)
    val genreTrackListSortPreferences:
        PersistentMap<Long, MediaSortPreferences<SortOptions.TrackListSortOptions>> =
        persistentMapOf(),
    @Serializable(with = GenreSortPrefsSerializer::class)
    val playlistTrackListSortPreferences:
        PersistentMap<Long, MediaSortPreferences<SortOptions.TrackListSortOptions>> =
        persistentMapOf(),
    val albumListSortPreferences: MediaSortPreferences<SortOptions.AlbumListSortOptions> =
        MediaSortPreferences(sortOption = SortOptions.AlbumListSortOptions.ALBUM),
    val artistListSortOrder: MediaSortOrder = MediaSortOrder.ASCENDING,
    val genreListSortOrder: MediaSortOrder = MediaSortOrder.ASCENDING,
    val playlistListSortOrder: MediaSortOrder = MediaSortOrder.ASCENDING,
    @Serializable(with = PlaylistSortPrefsSerializer::class)
    val playlistSortPreferences:
        PersistentMap<Long, MediaSortPreferences<SortOptions.PlaylistSortOptions>> =
        persistentMapOf(),
)

@Serializable
data class MediaSortPreferences<T : SortOptions>(
    val sortOption: T,
    val sortOrder: MediaSortOrder = MediaSortOrder.ASCENDING,
)

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
