package com.sebastianvm.musicplayer.repository.preferences

import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import kotlinx.coroutines.flow.Flow

interface SortPreferencesRepository {
    suspend fun modifyTrackListSortPreferences(
        newPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>,
        tracksListType: TracksListType,
        tracksListId: Long,
    )

    fun getTracksListSortPreferences(
        tracksListType: TracksListType,
        tracksListId: Long = 0
    ): Flow<MediaSortPreferences<SortOptions.TrackListSortOptions>>

    suspend fun modifyAlbumsListSortPreferences(newPreferences: MediaSortPreferences<SortOptions.AlbumListSortOptions>)
    fun getAlbumsListSortPreferences(): Flow<MediaSortPreferences<SortOptions.AlbumListSortOptions>>

    suspend fun modifyArtistsListSortOrder(mediaSortOrder: MediaSortOrder)
    fun getArtistsListSortOrder(): Flow<MediaSortOrder>

    suspend fun modifyGenresListSortOrder(mediaSortOrder: MediaSortOrder)
    fun getGenresListSortOrder(): Flow<MediaSortOrder>

    suspend fun modifyPlaylistsListSortOrder(mediaSortOrder: MediaSortOrder)
    fun getPlaylistsListSortOrder(): Flow<MediaSortOrder>

    suspend fun modifyPlaylistsSortPreferences(playlistId: Long, newPreferences: MediaSortPreferences<SortOptions.PlaylistSortOptions>)
    fun getPlaylistSortPreferences(playlistId: Long): Flow<MediaSortPreferences<SortOptions.PlaylistSortOptions>>
}