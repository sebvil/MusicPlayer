package com.sebastianvm.musicplayer.repository

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sebastianvm.musicplayer.util.PreferencesUtil
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.SortSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesRepository @Inject constructor(private val preferencesUtil: PreferencesUtil) {
    suspend fun modifyTrackListSortOptions(
        sortOrder: SortOrder,
        sortOption: SortOption,
        genreName: String?
    ) {
        preferencesUtil.dataStore.edit { settings ->
            genreName?.also {
                val sortOptionKey =
                    stringPreferencesKey("$genreName-${PreferencesUtil.TRACKS_SORT_OPTION}")
                val sortOrderKey =
                    stringPreferencesKey("$genreName-${PreferencesUtil.TRACKS_SORT_ORDER}")
                settings[sortOptionKey] = sortOption.name
                settings[sortOrderKey] = sortOrder.name
            } ?: kotlin.run {
                settings[PreferencesUtil.TRACKS_SORT_OPTION] = sortOption.name
                settings[PreferencesUtil.TRACKS_SORT_ORDER] = sortOrder.name
            }
        }
    }


    fun getTracksListSortOptions(genreName: String?): Flow<SortSettings> {
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
        }
    }

    suspend fun modifyAlbumsListSortOptions(
        sortOrder: SortOrder,
        sortOption: SortOption,
    ) {
        preferencesUtil.dataStore.edit { settings ->
            settings[PreferencesUtil.ALBUMS_SORT_OPTION] = sortOption.name
            settings[PreferencesUtil.ALBUMS_SORT_ORDER] = sortOrder.name
        }
    }

    fun getAlbumsListSortOptions(): Flow<SortSettings> {
        return preferencesUtil.dataStore.data.map { preferences ->
            val sortOption = preferences[PreferencesUtil.ALBUMS_SORT_OPTION]
            val sortOrder = preferences[PreferencesUtil.ALBUMS_SORT_ORDER]
            if (sortOption != null && sortOrder != null) {
                SortSettings(SortOption.valueOf(sortOption), SortOrder.valueOf(sortOrder))
            } else {
                SortSettings(SortOption.ALBUM_NAME, SortOrder.ASCENDING)
            }
        }
    }


    suspend fun modifyArtistsListSortOrder(sortOrder: SortOrder) {
        preferencesUtil.dataStore.edit { settings ->
            settings[PreferencesUtil.ARTISTS_SORT_ORDER] = sortOrder.name
        }
    }

    fun getArtistsListSortOrder(): Flow<SortOrder> {
        return preferencesUtil.dataStore.data.map { preferences ->
            preferences[PreferencesUtil.ARTISTS_SORT_ORDER]?.let { SortOrder.valueOf(it) } ?: SortOrder.ASCENDING
        }
    }

    suspend fun modifyGenresListSortOrder(sortOrder: SortOrder) {
        preferencesUtil.dataStore.edit { settings ->
            settings[PreferencesUtil.GENRES_SORT_ORDER] = sortOrder.name
        }
    }

    fun getGenresListSortOrder(): Flow<SortOrder> {
        return preferencesUtil.dataStore.data.map { preferences ->
            preferences[PreferencesUtil.GENRES_SORT_ORDER]?.let { SortOrder.valueOf(it) } ?: SortOrder.ASCENDING
        }
    }
}