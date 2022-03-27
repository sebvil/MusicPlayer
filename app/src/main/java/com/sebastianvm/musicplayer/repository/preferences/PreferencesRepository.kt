package com.sebastianvm.musicplayer.repository.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.SavedPlaybackInfo
import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.util.PreferencesUtil
import com.sebastianvm.musicplayer.util.coroutines.IODispatcher
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortSettings
import com.sebastianvm.musicplayer.util.sort.SortSettings
import com.sebastianvm.musicplayer.util.sort.SortSettingsSerializer
import com.sebastianvm.musicplayer.util.sort.copy
import com.sebastianvm.musicplayer.util.sort.mediaSortSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = SORT_SETTINGS_PREFERENCES_FILE_NAME)
    private val Context.sortSettingsDataStore: DataStore<SortSettings> by dataStore(
        fileName = SORT_SETTINGS_DATA_STORE_FILE_NAME,
        serializer = SortSettingsSerializer,
        corruptionHandler = null,
        produceMigrations = { listOf() },
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    )

    suspend fun modifyTrackListSortOptions(
        mediaSortSettings: MediaSortSettings,
        tracksListType: TracksListType,
        tracksListName: String,
    ) {
        withContext(ioDispatcher) {
            context.sortSettingsDataStore.updateData { oldSettings ->
                when (tracksListType) {
                    TracksListType.ALL_TRACKS -> {
                        oldSettings.copy {
                            allTracksSortSettings = mediaSortSettings
                        }
                    }
                    TracksListType.GENRE -> {
                        oldSettings.copy {
                            genreTrackListSortSettings[tracksListName] = mediaSortSettings
                        }
                    }
                    TracksListType.PLAYLIST -> {
                        oldSettings.copy {
                            playlistTrackListSortSettings[tracksListName] = mediaSortSettings
                        }

                    }
                }
            }
        }
    }

    fun getTracksListSortOptions(
        tracksListType: TracksListType,
        tracksListName: String
    ): Flow<MediaSortSettings> {
        return context.sortSettingsDataStore.data.map { sortSettings ->
            when (tracksListType) {
                TracksListType.ALL_TRACKS -> sortSettings.allTracksSortSettings
                TracksListType.GENRE -> sortSettings.genreTrackListSortSettingsMap[tracksListName]
                    ?: mediaSortSettings { }
                TracksListType.PLAYLIST -> sortSettings.playlistTrackListSortSettingsMap[tracksListName]
                    ?: mediaSortSettings { }
            }
        }.distinctUntilChanged()
    }


    suspend fun modifyAlbumsListSortOptions(mediaSortSettings: MediaSortSettings) {
        withContext(ioDispatcher) {
            context.sortSettingsDataStore.updateData { sortSettings ->
                sortSettings.copy {
                    albumsListSortSettings = mediaSortSettings
                }
            }
        }
    }


    fun getAlbumsListSortOptions(): Flow<MediaSortSettings> {
        return context.sortSettingsDataStore.data.map { oldSettings ->
            var settings = oldSettings.albumsListSortSettings
            if (settings.sortOption == MediaSortOption.TRACK) {
                settings = mediaSortSettings {
                    sortOption = MediaSortOption.ALBUM
                    sortOrder = MediaSortOrder.ASCENDING
                }
            }
            settings
        }.distinctUntilChanged()
    }

    suspend fun modifyArtistsListSortOrder(mediaSortOrder: MediaSortOrder) {
        withContext(ioDispatcher) {
            context.sortSettingsDataStore.updateData { sortSettings ->
                sortSettings.copy {
                    artistListSortSettings = mediaSortOrder
                }
            }
        }
    }


    fun getArtistsListSortOrder(): Flow<MediaSortOrder> {
        return context.sortSettingsDataStore.data.map { sortSettings ->
            sortSettings.artistListSortSettings
        }.distinctUntilChanged()
    }

    suspend fun modifyGenresListSortOrder(mediaSortOrder: MediaSortOrder) {
        withContext(ioDispatcher) {
            context.sortSettingsDataStore.updateData { sortSettings ->
                sortSettings.copy {
                    genresListSortSettings = mediaSortOrder
                }
            }
        }
    }


    fun getGenresListSortOrder(): Flow<MediaSortOrder> {
        return context.sortSettingsDataStore.data.map { sortSettings ->
            sortSettings.genresListSortSettings
        }.distinctUntilChanged()
    }

    suspend fun modifyPlaylistsListSortOrder(mediaSortOrder: MediaSortOrder) {
        withContext(ioDispatcher) {
            context.sortSettingsDataStore.updateData { sortSettings ->
                sortSettings.copy {
                    playlistsListSortSettings = mediaSortOrder
                }
            }
        }
    }


    fun getPlaylistsListSortOrder(): Flow<MediaSortOrder> {
        return context.sortSettingsDataStore.data.map { sortSettings ->
            sortSettings.playlistsListSortSettings
        }.distinctUntilChanged()
    }

    suspend fun modifySavedPlaybackInfo(transform: (savedPlaybackInfo: SavedPlaybackInfo) -> SavedPlaybackInfo) {
        withContext(ioDispatcher) {
            with(transform(getSavedPlaybackInfo().first())) {
                context.preferencesDataStore.edit { settings ->
                    settings[PreferencesUtil.SAVED_PLAYBACK_INFO_MEDIA_GROUP] =
                        currentQueue.mediaGroupType.name
                    settings[PreferencesUtil.SAVED_PLAYBACK_INFO_MEDIA_GROUP_ID] =
                        currentQueue.mediaId
                    settings[PreferencesUtil.SAVED_PLAYBACK_INFO_MEDIA_ID] = mediaId
                    settings[PreferencesUtil.SAVED_PLAYBACK_INFO_POSITION] = lastRecordedPosition
                }
            }
        }
    }

    fun getSavedPlaybackInfo(): Flow<SavedPlaybackInfo> {
        return context.preferencesDataStore.data.map { preferences ->
            val mediaGroup =
                preferences[PreferencesUtil.SAVED_PLAYBACK_INFO_MEDIA_GROUP]
                    ?: MediaGroupType.UNKNOWN.name
            val mediaGroupId = preferences[PreferencesUtil.SAVED_PLAYBACK_INFO_MEDIA_GROUP_ID] ?: ""
            val mediaId = preferences[PreferencesUtil.SAVED_PLAYBACK_INFO_MEDIA_ID] ?: ""
            val position = preferences[PreferencesUtil.SAVED_PLAYBACK_INFO_POSITION] ?: 0
            SavedPlaybackInfo(
                MediaGroup(MediaGroupType.valueOf(mediaGroup), mediaGroupId),
                mediaId,
                position
            )
        }.distinctUntilChanged()
    }

    companion object {
        private const val SORT_SETTINGS_DATA_STORE_FILE_NAME = "sort_settings.pb"
        private const val SORT_SETTINGS_PREFERENCES_FILE_NAME = "sort_settings"
    }
}
