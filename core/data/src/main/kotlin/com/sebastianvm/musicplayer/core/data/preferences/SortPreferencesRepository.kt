package com.sebastianvm.musicplayer.core.data.preferences

import com.sebastianvm.musicplayer.core.datastore.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.core.model.MediaSortOrder
import com.sebastianvm.musicplayer.core.model.SortOptions
import com.sebastianvm.musicplayer.core.model.TrackList
import kotlinx.coroutines.flow.Flow

interface SortPreferencesRepository {

    suspend fun modifyTrackListSortPreferences(
        newPreferences: MediaSortPreferences<SortOptions.TrackListSortOption>,
        trackList: TrackList,
    )

    fun getTrackListSortPreferences(
        trackList: TrackList
    ): Flow<MediaSortPreferences<SortOptions.TrackListSortOption>>

    suspend fun modifyAlbumListSortPreferences(
        newPreferences: MediaSortPreferences<SortOptions.AlbumListSortOption>
    )

    fun getAlbumListSortPreferences(): Flow<MediaSortPreferences<SortOptions.AlbumListSortOption>>

    suspend fun toggleArtistListSortOrder()

    fun getArtistListSortOrder(): Flow<MediaSortOrder>

    suspend fun toggleGenreListSortOrder()

    fun getGenreListSortOrder(): Flow<MediaSortOrder>

    suspend fun togglePlaylistListSortOder()

    fun getPlaylistsListSortOrder(): Flow<MediaSortOrder>

    suspend fun modifyPlaylistsSortPreferences(
        playlistId: Long,
        newPreferences: MediaSortPreferences<SortOptions.PlaylistSortOption>,
    )

    fun getPlaylistSortPreferences(
        playlistId: Long
    ): Flow<MediaSortPreferences<SortOptions.PlaylistSortOption>>
}
