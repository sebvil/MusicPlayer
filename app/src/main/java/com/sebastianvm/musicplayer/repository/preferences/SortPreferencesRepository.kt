package com.sebastianvm.musicplayer.repository.preferences

import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.util.sort.AlbumListSortOptions
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.PlaylistSortOptions
import com.sebastianvm.musicplayer.util.sort.TrackListSortOptions
import kotlinx.coroutines.flow.Flow

interface SortPreferencesRepository {
    suspend fun modifyTrackListSortPreferences(
        newPreferences: MediaSortPreferences<TrackListSortOptions>,
        tracksListType: TracksListType,
        tracksListName: String,
    )

    fun getTracksListSortPreferences(
        tracksListType: TracksListType,
        tracksListName: String
    ): Flow<MediaSortPreferences<TrackListSortOptions>>

    suspend fun modifyAlbumsListSortPreferences(newPreferences: MediaSortPreferences<AlbumListSortOptions>)
    fun getAlbumsListSortPreferences(): Flow<MediaSortPreferences<AlbumListSortOptions>>

    suspend fun modifyArtistsListSortOrder(mediaSortOrder: MediaSortOrder)
    fun getArtistsListSortOrder(): Flow<MediaSortOrder>

    suspend fun modifyGenresListSortOrder(mediaSortOrder: MediaSortOrder)
    fun getGenresListSortOrder(): Flow<MediaSortOrder>

    suspend fun modifyPlaylistsListSortOrder(mediaSortOrder: MediaSortOrder)
    fun getPlaylistsListSortOrder(): Flow<MediaSortOrder>

    suspend fun modifyPlaylistsSortPreferences(playlistName: String, newPreferences: MediaSortPreferences<PlaylistSortOptions>)
    fun getPlaylistSortPreferences(playlistName: String): Flow<MediaSortPreferences<PlaylistSortOptions>>
}