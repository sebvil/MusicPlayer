package com.sebastianvm.musicplayer.repository.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val preferencesDataStore: DataStore<Preferences>,
    private val sortSettingsDataStore: DataStore<SortSettings>,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : PreferencesRepository {

    override suspend fun modifyTrackListSortOptions(
        mediaSortSettings: MediaSortSettings,
        tracksListType: TracksListType,
        tracksListName: String,
    ) {
        withContext(ioDispatcher) {
            sortSettingsDataStore.updateData { sortSettings ->
                when (tracksListType) {
                    TracksListType.ALL_TRACKS -> {
                        sortSettings.toBuilder().setAllTracksSortSettings(mediaSortSettings).build()
                    }
                    TracksListType.GENRE -> {
                        sortSettings.toBuilder()
                            .putGenreTrackListSortSettings(tracksListName, mediaSortSettings)
                            .build()
                    }
                    TracksListType.PLAYLIST -> {
                        sortSettings.toBuilder()
                            .putPlaylistTrackListSortSettings(tracksListName, mediaSortSettings)
                            .build()
                    }
                }
            }
        }
    }

    override fun getTracksListSortOptions(
        tracksListType: TracksListType,
        tracksListName: String
    ): Flow<MediaSortSettings> {
        return sortSettingsDataStore.data.map { sortSettings ->
            when (tracksListType) {
                TracksListType.ALL_TRACKS -> sortSettings.allTracksSortSettings
                TracksListType.GENRE -> sortSettings.genreTrackListSortSettingsMap[tracksListName]
                    ?: MediaSortSettings.getDefaultInstance()
                TracksListType.PLAYLIST -> sortSettings.playlistTrackListSortSettingsMap[tracksListName]
                    ?: MediaSortSettings.getDefaultInstance()
            }
        }.distinctUntilChanged()
    }


    override suspend fun modifyAlbumsListSortOptions(mediaSortSettings: MediaSortSettings) {
        withContext(ioDispatcher) {
            sortSettingsDataStore.updateData { sortSettings ->
                sortSettings.toBuilder().setAlbumsListSortSettings(mediaSortSettings).build()
            }
        }
    }


    override fun getAlbumsListSortOptions(): Flow<MediaSortSettings> {
        return sortSettingsDataStore.data.map { sortSettings ->
            var settings = sortSettings.albumsListSortSettings
            if (settings.sortOption == MediaSortOption.TRACK) {
                settings =
                    MediaSortSettings.newBuilder().setSortOption(MediaSortOption.ALBUM).build()
            }
            settings
        }.distinctUntilChanged()
    }

    override suspend fun modifyArtistsListSortOrder(mediaSortOrder: MediaSortOrder) {
        withContext(ioDispatcher) {
            sortSettingsDataStore.updateData { sortSettings ->
                sortSettings.toBuilder().setArtistListSortSettings(mediaSortOrder).build()
            }
        }
    }


    override fun getArtistsListSortOrder(): Flow<MediaSortOrder> {
        return sortSettingsDataStore.data.map { sortSettings ->
            sortSettings.artistListSortSettings
        }.distinctUntilChanged()
    }

    override suspend fun modifyGenresListSortOrder(mediaSortOrder: MediaSortOrder) {
        withContext(ioDispatcher) {
            sortSettingsDataStore.updateData { sortSettings ->
                sortSettings.toBuilder().setGenresListSortSettings(mediaSortOrder).build()
            }
        }
    }


    override fun getGenresListSortOrder(): Flow<MediaSortOrder> {
        return sortSettingsDataStore.data.map { sortSettings ->
            sortSettings.genresListSortSettings
        }.distinctUntilChanged()
    }

    override suspend fun modifyPlaylistsListSortOrder(mediaSortOrder: MediaSortOrder) {
        withContext(ioDispatcher) {
            sortSettingsDataStore.updateData { sortSettings ->
                sortSettings.toBuilder().setPlaylistsListSortSettings(mediaSortOrder).build()
            }
        }
    }


    override fun getPlaylistsListSortOrder(): Flow<MediaSortOrder> {
        return sortSettingsDataStore.data.map { sortSettings ->
            sortSettings.playlistsListSortSettings
        }.distinctUntilChanged()
    }

    override suspend fun modifySavedPlaybackInfo(transform: (savedPlaybackInfo: SavedPlaybackInfo) -> SavedPlaybackInfo) {
        withContext(ioDispatcher) {
            with(transform(getSavedPlaybackInfo().first())) {
                preferencesDataStore.edit { settings ->
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

    override fun getSavedPlaybackInfo(): Flow<SavedPlaybackInfo> {
        return preferencesDataStore.data.map { preferences ->
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
}
