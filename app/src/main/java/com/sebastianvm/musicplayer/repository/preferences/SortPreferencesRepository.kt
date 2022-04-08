package com.sebastianvm.musicplayer.repository.preferences

import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SortPreferencesRepository @Inject constructor(
    private val sortPreferencesDataSource: SortPreferencesDataSource
) {

    suspend fun modifyTrackListSortOptions(
        mediaSortSettings: MediaSortSettings,
        tracksListType: TracksListType,
        tracksListName: String,
    ) {
        when (tracksListType) {
            TracksListType.ALL_TRACKS -> sortPreferencesDataSource.modifyAllTracksListSortOptions(
                mediaSortSettings
            )
            TracksListType.GENRE -> sortPreferencesDataSource.modifyGenreTracksListSortOptions(
                tracksListName,
                mediaSortSettings
            )
            TracksListType.PLAYLIST -> sortPreferencesDataSource.modifyPlaylistTracksListSortOptions(
                tracksListName,
                mediaSortSettings
            )
        }
    }

    fun getTracksListSortOptions(
        tracksListType: TracksListType,
        tracksListName: String
    ): Flow<MediaSortSettings> {
        return when (tracksListType) {
            TracksListType.ALL_TRACKS -> sortPreferencesDataSource.getAllTracksListSortOptions()
            TracksListType.GENRE -> sortPreferencesDataSource.getGenreTracksListSortOptions(
                tracksListName
            )
            TracksListType.PLAYLIST -> sortPreferencesDataSource.getPlaylistTracksListSortOptions(
                tracksListName
            )
        }
    }


    suspend fun modifyAlbumsListSortOptions(mediaSortSettings: MediaSortSettings) {
        sortPreferencesDataSource.modifyAlbumsListSortOptions(mediaSortSettings)
    }


    fun getAlbumsListSortOptions(): Flow<MediaSortSettings> {
        return sortPreferencesDataSource.getAlbumsListSortOptions()
    }

    suspend fun modifyArtistsListSortOrder(mediaSortOrder: MediaSortOrder) {
        sortPreferencesDataSource.modifyArtistsListSortOrder(mediaSortOrder)
    }


    fun getArtistsListSortOrder(): Flow<MediaSortOrder> {
        return sortPreferencesDataSource.getArtistsListSortOrder()
    }

    suspend fun modifyGenresListSortOrder(mediaSortOrder: MediaSortOrder) {
        sortPreferencesDataSource.modifyGenresListSortOrder(mediaSortOrder)
    }


    fun getGenresListSortOrder(): Flow<MediaSortOrder> {
        return sortPreferencesDataSource.getGenresListSortOrder()
    }

    suspend fun modifyPlaylistsListSortOrder(mediaSortOrder: MediaSortOrder) {
        sortPreferencesDataSource.modifyPlaylistsListSortOrder(mediaSortOrder)
    }


    fun getPlaylistsListSortOrder(): Flow<MediaSortOrder> {
        return sortPreferencesDataSource.getPlaylistsListSortOrder()
    }

}
