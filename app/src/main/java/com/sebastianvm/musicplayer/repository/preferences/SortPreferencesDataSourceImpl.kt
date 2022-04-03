package com.sebastianvm.musicplayer.repository.preferences

import androidx.datastore.core.DataStore
import com.sebastianvm.musicplayer.util.coroutines.IODispatcher
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortSettings
import com.sebastianvm.musicplayer.util.sort.SortSettings
import com.sebastianvm.musicplayer.util.sort.copy
import com.sebastianvm.musicplayer.util.sort.mediaSortSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SortPreferencesDataSourceImpl @Inject constructor(
    private val sortSettingsDataStore: DataStore<SortSettings>,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : SortPreferencesDataSource {
    override suspend fun modifyAllTracksListSortOptions(mediaSortSettings: MediaSortSettings) {
        withContext(ioDispatcher) {
            sortSettingsDataStore.updateData { oldSettings ->
                oldSettings.copy {
                    allTracksSortSettings = mediaSortSettings
                }
            }
        }
    }

    override suspend fun modifyGenreTracksListSortOptions(
        genreName: String,
        mediaSortSettings: MediaSortSettings
    ) {
        withContext(ioDispatcher) {
            sortSettingsDataStore.updateData { oldSettings ->
                oldSettings.copy {
                    genreTrackListSortSettings[genreName] = mediaSortSettings
                }
            }
        }
    }

    override suspend fun modifyPlaylistTracksListSortOptions(
        playlistName: String,
        mediaSortSettings: MediaSortSettings
    ) {
        withContext(ioDispatcher) {
            sortSettingsDataStore.updateData { oldSettings ->
                oldSettings.copy {
                    playlistTrackListSortSettings[playlistName] = mediaSortSettings
                }
            }
        }
    }

    override fun getAllTracksListSortOptions(): Flow<MediaSortSettings> {
        return sortSettingsDataStore.data.map { sortSettings ->
            sortSettings.allTracksSortSettings
        }
    }

    override fun getGenreTracksListSortOptions(genreName: String): Flow<MediaSortSettings> {
        return sortSettingsDataStore.data.map { sortSettings ->
            sortSettings.genreTrackListSortSettingsMap[genreName]
                ?: MediaSortSettings.getDefaultInstance()
        }
    }

    override fun getPlaylistTracksListSortOptions(playlistName: String): Flow<MediaSortSettings> {
        return sortSettingsDataStore.data.map { sortSettings ->
            sortSettings.playlistTrackListSortSettingsMap[playlistName]
                ?: MediaSortSettings.getDefaultInstance()
        }
    }


    override suspend fun modifyAlbumsListSortOptions(mediaSortSettings: MediaSortSettings) {
        withContext(ioDispatcher) {
            sortSettingsDataStore.updateData { sortSettings ->
                sortSettings.copy {
                    albumsListSortSettings = mediaSortSettings
                }
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
        withContext(ioDispatcher) {
            sortSettingsDataStore.updateData { sortSettings ->
                sortSettings.copy {
                    artistListSortSettings = mediaSortOrder
                }
            }
        }
    }


    override fun getArtistsListSortOrder(): Flow<MediaSortOrder> {
        return sortSettingsDataStore.data.map { sortSettings ->
            sortSettings.artistListSortSettings
        }
    }

    override suspend fun modifyGenresListSortOrder(mediaSortOrder: MediaSortOrder) {
        withContext(ioDispatcher) {
            sortSettingsDataStore.updateData { sortSettings ->
                sortSettings.copy {
                    genresListSortSettings = mediaSortOrder
                }
            }
        }
    }


    override fun getGenresListSortOrder(): Flow<MediaSortOrder> {
        return sortSettingsDataStore.data.map { sortSettings ->
            sortSettings.genresListSortSettings
        }
    }

    override suspend fun modifyPlaylistsListSortOrder(mediaSortOrder: MediaSortOrder) {
        withContext(ioDispatcher) {
            sortSettingsDataStore.updateData { sortSettings ->
                sortSettings.copy {
                    playlistsListSortSettings = mediaSortOrder
                }
            }
        }
    }


    override fun getPlaylistsListSortOrder(): Flow<MediaSortOrder> {
        return sortSettingsDataStore.data.map { sortSettings ->
            sortSettings.playlistsListSortSettings
        }
    }

}
