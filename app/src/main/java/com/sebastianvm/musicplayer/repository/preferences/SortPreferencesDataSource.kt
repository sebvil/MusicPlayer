package com.sebastianvm.musicplayer.repository.preferences

import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortSettings
import kotlinx.coroutines.flow.Flow

interface SortPreferencesDataSource {
    suspend fun modifyAllTracksListSortOptions(mediaSortSettings: MediaSortSettings)
    suspend fun modifyGenreTracksListSortOptions(
        genreName: String,
        mediaSortSettings: MediaSortSettings
    )

    suspend fun modifyPlaylistTracksListSortOptions(
        playlistName: String,
        mediaSortSettings: MediaSortSettings
    )

    fun getAllTracksListSortOptions(): Flow<MediaSortSettings>
    fun getGenreTracksListSortOptions(genreName: String): Flow<MediaSortSettings>
    fun getPlaylistTracksListSortOptions(playlistName: String): Flow<MediaSortSettings>


    suspend fun modifyAlbumsListSortOptions(mediaSortSettings: MediaSortSettings)
    fun getAlbumsListSortOptions(): Flow<MediaSortSettings>

    suspend fun modifyArtistsListSortOrder(mediaSortOrder: MediaSortOrder)
    fun getArtistsListSortOrder(): Flow<MediaSortOrder>

    suspend fun modifyGenresListSortOrder(mediaSortOrder: MediaSortOrder)
    fun getGenresListSortOrder(): Flow<MediaSortOrder>

    suspend fun modifyPlaylistsListSortOrder(mediaSortOrder: MediaSortOrder)
    fun getPlaylistsListSortOrder(): Flow<MediaSortOrder>
}