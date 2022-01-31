package com.sebastianvm.musicplayer.repository.preferences

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.SavedPlaybackInfo
import com.sebastianvm.musicplayer.util.PreferencesUtil
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.SortSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(private val preferencesUtil: PreferencesUtil) :
    PreferencesRepository {
    override suspend fun modifyTrackListSortOptions(
        sortSettings: SortSettings,
        genreName: String?
    ) {
        preferencesUtil.dataStore.edit { settings ->
            genreName?.also {
                val sortOptionKey =
                    stringPreferencesKey("$genreName-${PreferencesUtil.TRACKS_SORT_OPTION}")
                val sortOrderKey =
                    stringPreferencesKey("$genreName-${PreferencesUtil.TRACKS_SORT_ORDER}")
                settings[sortOptionKey] = sortSettings.sortOption.name
                settings[sortOrderKey] = sortSettings.sortOrder.name
            } ?: kotlin.run {
                settings[PreferencesUtil.TRACKS_SORT_OPTION] = sortSettings.sortOption.name
                settings[PreferencesUtil.TRACKS_SORT_ORDER] = sortSettings.sortOrder.name
            }
        }
    }


    override fun getTracksListSortOptions(genreName: String?): Flow<SortSettings> {
        return preferencesUtil.dataStore.data.map { preferences ->
            genreName?.let {
                val sortOptionKey =
                    stringPreferencesKey("$genreName-${PreferencesUtil.TRACKS_SORT_OPTION}")
                val sortOrderKey =
                    stringPreferencesKey("$genreName-${PreferencesUtil.TRACKS_SORT_ORDER}")
                val sortOption = preferences[sortOptionKey]
                val sortOrder = preferences[sortOrderKey]
                if (sortOption != null && sortOrder != null) {
                    SortSettings(SortOption.valueOf(sortOption), SortOrder.valueOf(sortOrder))
                } else {
                    SortSettings(SortOption.TRACK_NAME, SortOrder.ASCENDING)
                }
            } ?: kotlin.run {
                val sortOption = preferences[PreferencesUtil.TRACKS_SORT_OPTION]
                val sortOrder = preferences[PreferencesUtil.TRACKS_SORT_ORDER]
                if (sortOption != null && sortOrder != null) {
                    SortSettings(SortOption.valueOf(sortOption), SortOrder.valueOf(sortOrder))
                } else {
                    SortSettings(SortOption.TRACK_NAME, SortOrder.ASCENDING)
                }
            }
        }.distinctUntilChanged()
    }

    override suspend fun modifyAlbumsListSortOptions(sortSettings: SortSettings) {
        preferencesUtil.dataStore.edit { settings ->
            settings[PreferencesUtil.ALBUMS_SORT_OPTION] = sortSettings.sortOption.name
            settings[PreferencesUtil.ALBUMS_SORT_ORDER] = sortSettings.sortOrder.name
        }
    }

    override fun getAlbumsListSortOptions(): Flow<SortSettings> {
        return preferencesUtil.dataStore.data.map { preferences ->
            val sortOption = preferences[PreferencesUtil.ALBUMS_SORT_OPTION]
            val sortOrder = preferences[PreferencesUtil.ALBUMS_SORT_ORDER]
            if (sortOption != null && sortOrder != null) {
                SortSettings(SortOption.valueOf(sortOption), SortOrder.valueOf(sortOrder))
            } else {
                SortSettings(SortOption.ALBUM_NAME, SortOrder.ASCENDING)
            }
        }.distinctUntilChanged()
    }


    override suspend fun modifyArtistsListSortOrder(sortOrder: SortOrder) {
        preferencesUtil.dataStore.edit { settings ->
            settings[PreferencesUtil.ARTISTS_SORT_ORDER] = sortOrder.name
        }
    }

    override fun getArtistsListSortOrder(): Flow<SortOrder> {
        return preferencesUtil.dataStore.data.map { preferences ->
            preferences[PreferencesUtil.ARTISTS_SORT_ORDER]?.let { SortOrder.valueOf(it) }
                ?: SortOrder.ASCENDING
        }.distinctUntilChanged()
    }

    override suspend fun modifyGenresListSortOrder(sortOrder: SortOrder) {
        preferencesUtil.dataStore.edit { settings ->
            settings[PreferencesUtil.GENRES_SORT_ORDER] = sortOrder.name
        }
    }

    override fun getGenresListSortOrder(): Flow<SortOrder> {
        return preferencesUtil.dataStore.data.map { preferences ->
            preferences[PreferencesUtil.GENRES_SORT_ORDER]?.let { SortOrder.valueOf(it) }
                ?: SortOrder.ASCENDING
        }.distinctUntilChanged()
    }

    override suspend fun modifyPlaylistsListSortOrder(sortOrder: SortOrder) {
        preferencesUtil.dataStore.edit { settings ->
            settings[PreferencesUtil.PLAYLISTS_SORT_ORDER] = sortOrder.name
        }
    }

    override fun getPlaylistsListSortOrder(): Flow<SortOrder> {
        return preferencesUtil.dataStore.data.map { preferences ->
            preferences[PreferencesUtil.PLAYLISTS_SORT_ORDER]?.let { SortOrder.valueOf(it) }
                ?: SortOrder.ASCENDING
        }
    }

    override suspend fun modifySavedPlaybackInfo(transform: (savedPlaybackInfo: SavedPlaybackInfo) -> SavedPlaybackInfo) {
        with(transform(getSavedPlaybackInfo().first())) {
            preferencesUtil.dataStore.edit { settings ->
                settings[PreferencesUtil.SAVED_PLAYBACK_INFO_MEDIA_GROUP] =
                    currentQueue.mediaGroupType.name
                settings[PreferencesUtil.SAVED_PLAYBACK_INFO_MEDIA_GROUP_ID] = currentQueue.mediaId
                settings[PreferencesUtil.SAVED_PLAYBACK_INFO_MEDIA_ID] = mediaId
                settings[PreferencesUtil.SAVED_PLAYBACK_INFO_POSITION] = lastRecordedPosition
            }
        }
    }

    override fun getSavedPlaybackInfo(): Flow<SavedPlaybackInfo> {
        return preferencesUtil.dataStore.data.map { preferences ->
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
