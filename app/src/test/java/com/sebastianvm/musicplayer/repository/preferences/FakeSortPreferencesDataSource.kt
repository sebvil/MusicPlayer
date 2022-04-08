package com.sebastianvm.musicplayer.repository.preferences

import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortSettings
import com.sebastianvm.musicplayer.util.sort.SortSettings
import com.sebastianvm.musicplayer.util.sort.copy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeSortPreferencesDataSource : SortPreferencesDataSource {
    private val defaultSortSettings = MutableStateFlow(SortSettings.getDefaultInstance())

    override suspend fun modifyAllTracksListSortOptions(mediaSortSettings: MediaSortSettings) {
        defaultSortSettings.value = defaultSortSettings.value.copy {
            allTracksSortSettings = mediaSortSettings
        }
    }

    override suspend fun modifyGenreTracksListSortOptions(
        genreName: String,
        mediaSortSettings: MediaSortSettings
    ) {
        defaultSortSettings.value = defaultSortSettings.value.copy {
            genreTrackListSortSettings[genreName] = mediaSortSettings
        }
    }

    override suspend fun modifyPlaylistTracksListSortOptions(
        playlistName: String,
        mediaSortSettings: MediaSortSettings
    ) {
        defaultSortSettings.value = defaultSortSettings.value.copy {
            genreTrackListSortSettings[playlistName] = mediaSortSettings
        }
    }

    override fun getAllTracksListSortOptions(): Flow<MediaSortSettings> {
        return defaultSortSettings.map { it.allTracksSortSettings }
    }

    override fun getGenreTracksListSortOptions(genreName: String): Flow<MediaSortSettings> {
        return defaultSortSettings.map { it.genreTrackListSortSettingsMap[genreName] ?: MediaSortSettings.getDefaultInstance() }
    }

    override fun getPlaylistTracksListSortOptions(playlistName: String): Flow<MediaSortSettings> {
        return defaultSortSettings.map { it.genreTrackListSortSettingsMap[playlistName] ?: MediaSortSettings.getDefaultInstance() }
    }

    override suspend fun modifyAlbumsListSortOptions(mediaSortSettings: MediaSortSettings) {
        TODO("Not yet implemented")
    }

    override fun getAlbumsListSortOptions(): Flow<MediaSortSettings> {
        TODO("Not yet implemented")
    }

    override suspend fun modifyArtistsListSortOrder(mediaSortOrder: MediaSortOrder) {
        TODO("Not yet implemented")
    }

    override fun getArtistsListSortOrder(): Flow<MediaSortOrder> {
        TODO("Not yet implemented")
    }

    override suspend fun modifyGenresListSortOrder(mediaSortOrder: MediaSortOrder) {
        defaultSortSettings.value = defaultSortSettings.value.copy { genresListSortSettings = mediaSortOrder }
    }

    override fun getGenresListSortOrder(): Flow<MediaSortOrder> {
        return defaultSortSettings.map { it.genresListSortSettings }
    }

    override suspend fun modifyPlaylistsListSortOrder(mediaSortOrder: MediaSortOrder) {
        TODO("Not yet implemented")
    }

    override fun getPlaylistsListSortOrder(): Flow<MediaSortOrder> {
        TODO("Not yet implemented")
    }

}