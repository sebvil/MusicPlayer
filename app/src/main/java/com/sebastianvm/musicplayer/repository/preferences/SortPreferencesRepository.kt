package com.sebastianvm.musicplayer.repository.preferences

import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import kotlinx.coroutines.flow.Flow

interface SortPreferencesRepository {
    suspend fun modifyTrackListSortPreferences(
        newPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>,
        trackListType: TrackListType,
        trackListId: Long,
    )

    fun getTrackListSortPreferences(
        trackListType: TrackListType,
        trackListId: Long = 0
    ): Flow<MediaSortPreferences<SortOptions.TrackListSortOptions>>

    suspend fun modifyAlbumListSortPreferences(newPreferences: MediaSortPreferences<SortOptions.AlbumListSortOptions>)
    fun getAlbumListSortPreferences(): Flow<MediaSortPreferences<SortOptions.AlbumListSortOptions>>

    suspend fun toggleArtistListSortOrder()
    fun getArtistListSortOrder(): Flow<MediaSortOrder>

    suspend fun toggleGenreListSortOrder()
    fun getGenreListSortOrder(): Flow<MediaSortOrder>

    suspend fun togglePlaylistListSortOder()
    fun getPlaylistsListSortOrder(): Flow<MediaSortOrder>

    suspend fun modifyPlaylistsSortPreferences(
        playlistId: Long,
        newPreferences: MediaSortPreferences<SortOptions.PlaylistSortOptions>
    )

    fun getPlaylistSortPreferences(playlistId: Long): Flow<MediaSortPreferences<SortOptions.PlaylistSortOptions>>
}