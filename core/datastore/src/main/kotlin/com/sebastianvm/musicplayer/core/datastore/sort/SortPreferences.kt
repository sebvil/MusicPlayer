package com.sebastianvm.musicplayer.core.datastore.sort

import com.sebastianvm.musicplayer.core.model.MediaSortOrder
import com.sebastianvm.musicplayer.core.model.SortOptions
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable

@Serializable
data class SortPreferences(
    val allTrackListSortPreferences: MediaSortPreferences<SortOptions.TrackListSortOption> =
        MediaSortPreferences(sortOption = SortOptions.Track),
    @Serializable(with = GenreSortPrefsSerializer::class)
    val genreTrackListSortPreferences:
        PersistentMap<Long, MediaSortPreferences<SortOptions.TrackListSortOption>> =
        persistentMapOf(),
    @Serializable(with = GenreSortPrefsSerializer::class)
    val playlistTrackListSortPreferences:
        PersistentMap<Long, MediaSortPreferences<SortOptions.TrackListSortOption>> =
        persistentMapOf(),
    val albumListSortPreferences: MediaSortPreferences<SortOptions.AlbumListSortOption> =
        MediaSortPreferences(sortOption = SortOptions.Album),
    val artistListSortOrder: MediaSortOrder = MediaSortOrder.ASCENDING,
    val genreListSortOrder: MediaSortOrder = MediaSortOrder.ASCENDING,
    val playlistListSortOrder: MediaSortOrder = MediaSortOrder.ASCENDING,
    @Serializable(with = PlaylistSortPrefsSerializer::class)
    val playlistSortPreferences:
        PersistentMap<Long, MediaSortPreferences<SortOptions.PlaylistSortOption>> =
        persistentMapOf(),
)
