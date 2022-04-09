package com.sebastianvm.musicplayer.repository.preferences

import androidx.datastore.core.DataStore
import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortSettings
import com.sebastianvm.musicplayer.util.sort.SortSettings
import com.sebastianvm.musicplayer.util.sort.copy
import com.sebastianvm.musicplayer.util.sort.mediaSortSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SortPreferencesRepositoryImpl @Inject constructor(
    private val sortSettingsDataStore: DataStore<SortSettings>,
) : SortPreferencesRepository {

    private suspend fun modifyAllTracksListSortOptions(mediaSortSettings: MediaSortSettings) {
        sortSettingsDataStore.updateData { oldSettings ->
            oldSettings.copy {
                allTracksSortSettings = mediaSortSettings
            }
        }

    }

    private suspend fun modifyGenreTracksListSortOptions(
        genreName: String,
        mediaSortSettings: MediaSortSettings
    ) {
        sortSettingsDataStore.updateData { oldSettings ->
            oldSettings.copy {
                genreTrackListSortSettings[genreName] = mediaSortSettings
            }
        }

    }

    private suspend fun modifyPlaylistTracksListSortOptions(
        playlistName: String,
        mediaSortSettings: MediaSortSettings
    ) {
        sortSettingsDataStore.updateData { oldSettings ->
            oldSettings.copy {
                playlistTrackListSortSettings[playlistName] = mediaSortSettings
            }
        }

    }

    override suspend fun modifyTrackListSortOptions(
        mediaSortSettings: MediaSortSettings,
        tracksListType: TracksListType,
        tracksListName: String,
    ) {
        when (tracksListType) {
            TracksListType.ALL_TRACKS -> modifyAllTracksListSortOptions(
                mediaSortSettings
            )
            TracksListType.GENRE -> modifyGenreTracksListSortOptions(
                tracksListName,
                mediaSortSettings
            )
            TracksListType.PLAYLIST -> modifyPlaylistTracksListSortOptions(
                tracksListName,
                mediaSortSettings
            )
        }
    }

    private fun getAllTracksListSortOptions(): Flow<MediaSortSettings> {
        return sortSettingsDataStore.data.map { sortSettings ->
            sortSettings.allTracksSortSettings
        }
    }

    private fun getGenreTracksListSortOptions(genreName: String): Flow<MediaSortSettings> {
        return sortSettingsDataStore.data.map { sortSettings ->
            sortSettings.genreTrackListSortSettingsMap[genreName]
                ?: MediaSortSettings.getDefaultInstance()
        }
    }

    private fun getPlaylistTracksListSortOptions(playlistName: String): Flow<MediaSortSettings> {
        return sortSettingsDataStore.data.map { sortSettings ->
            sortSettings.playlistTrackListSortSettingsMap[playlistName]
                ?: MediaSortSettings.getDefaultInstance()
        }
    }

    override fun getTracksListSortOptions(
        tracksListType: TracksListType,
        tracksListName: String
    ): Flow<MediaSortSettings> {
        return when (tracksListType) {
            TracksListType.ALL_TRACKS -> getAllTracksListSortOptions()
            TracksListType.GENRE -> getGenreTracksListSortOptions(
                tracksListName
            )
            TracksListType.PLAYLIST -> getPlaylistTracksListSortOptions(
                tracksListName
            )
        }
    }


    override suspend fun modifyAlbumsListSortOptions(mediaSortSettings: MediaSortSettings) {
        sortSettingsDataStore.updateData { sortSettings ->
            sortSettings.copy {
                albumsListSortSettings = mediaSortSettings
            }
        }

    }


    override fun getAlbumsListSortOptions(): Flow<MediaSortSettings> {
        return sortSettingsDataStore.data.map { oldSettings ->
            var settings = oldSettings.albumsListSortSettings
            if (settings.sortOption == MediaSortOption.TRACK) {
                settings = mediaSortSettings {
                    sortOption = MediaSortOption.ALBUM
                    sortOrder = MediaSortOrder.ASCENDING
                }
            }
            settings
        }
    }

    override suspend fun modifyArtistsListSortOrder(mediaSortOrder: MediaSortOrder) {
        sortSettingsDataStore.updateData { sortSettings ->
            sortSettings.copy {
                artistListSortSettings = mediaSortOrder
            }
        }
    }


    override fun getArtistsListSortOrder(): Flow<MediaSortOrder> {
        return sortSettingsDataStore.data.map { sortSettings ->
            sortSettings.artistListSortSettings
        }
    }

    override suspend fun modifyGenresListSortOrder(mediaSortOrder: MediaSortOrder) {
        sortSettingsDataStore.updateData { sortSettings ->
            sortSettings.copy {
                genresListSortSettings = mediaSortOrder
            }
        }

    }


    override fun getGenresListSortOrder(): Flow<MediaSortOrder> {
        return sortSettingsDataStore.data.map { sortSettings ->
            sortSettings.genresListSortSettings
        }
    }

    override suspend fun modifyPlaylistsListSortOrder(mediaSortOrder: MediaSortOrder) {
        sortSettingsDataStore.updateData { sortSettings ->
            sortSettings.copy {
                playlistsListSortSettings = mediaSortOrder
            }
        }

    }


    override fun getPlaylistsListSortOrder(): Flow<MediaSortOrder> {
        return sortSettingsDataStore.data.map { sortSettings ->
            sortSettings.playlistsListSortSettings
        }
    }

}
