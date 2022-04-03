package com.sebastianvm.musicplayer.repository.preferences

import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SortPreferencesRepositoryImpl @Inject constructor(
    private val sortPreferencesDataSource: SortPreferencesDataSource
) : SortPreferencesRepository {

    override suspend fun modifyTrackListSortOptions(
        mediaSortSettings: MediaSortSettings,
        tracksListType: TracksListType,
        tracksListName: String,
    ) {
        when (tracksListType) {
            TracksListType.ALL_TRACKS -> sortPreferencesDataSource.modifyAllTracksListSortOptions(mediaSortSettings)
            TracksListType.GENRE -> sortPreferencesDataSource.modifyGenreTracksListSortOptions(tracksListName, mediaSortSettings)
            TracksListType.PLAYLIST -> sortPreferencesDataSource.modifyPlaylistTracksListSortOptions(tracksListName, mediaSortSettings)
        }
    }

    override fun getTracksListSortOptions(
        tracksListType: TracksListType,
        tracksListName: String
    ): Flow<MediaSortSettings> {
        return  when (tracksListType) {
            TracksListType.ALL_TRACKS -> sortPreferencesDataSource.getAllTracksListSortOptions()
            TracksListType.GENRE -> sortPreferencesDataSource.getGenreTracksListSortOptions(tracksListName)
            TracksListType.PLAYLIST -> sortPreferencesDataSource.getPlaylistTracksListSortOptions(tracksListName)
        }
    }


    override suspend fun modifyAlbumsListSortOptions(mediaSortSettings: MediaSortSettings) {
        sortPreferencesDataSource.modifyAlbumsListSortOptions(mediaSortSettings)
    }


    override fun getAlbumsListSortOptions(): Flow<MediaSortSettings> {
        return sortPreferencesDataSource.getAlbumsListSortOptions()
    }

    override suspend fun modifyArtistsListSortOrder(mediaSortOrder: MediaSortOrder) {
        sortPreferencesDataSource.modifyArtistsListSortOrder(mediaSortOrder)
    }


    override fun getArtistsListSortOrder(): Flow<MediaSortOrder> {
        return sortPreferencesDataSource.getArtistsListSortOrder()
    }

    override suspend fun modifyGenresListSortOrder(mediaSortOrder: MediaSortOrder) {
        sortPreferencesDataSource.modifyGenresListSortOrder(mediaSortOrder)
    }


    override fun getGenresListSortOrder(): Flow<MediaSortOrder> {
        return sortPreferencesDataSource.getGenresListSortOrder()
    }

    override suspend fun modifyPlaylistsListSortOrder(mediaSortOrder: MediaSortOrder) {
        sortPreferencesDataSource.modifyPlaylistsListSortOrder(mediaSortOrder)
    }


    override fun getPlaylistsListSortOrder(): Flow<MediaSortOrder> {
        return sortPreferencesDataSource.getPlaylistsListSortOrder()
    }

}
