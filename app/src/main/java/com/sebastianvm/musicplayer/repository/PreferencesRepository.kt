package com.sebastianvm.musicplayer.repository

import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sebastianvm.musicplayer.ui.library.tracks.SortOption
import com.sebastianvm.musicplayer.ui.library.tracks.TracksSortSettings
import com.sebastianvm.musicplayer.util.PreferencesUtil
import com.sebastianvm.musicplayer.util.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesRepository @Inject constructor(
    private val preferencesUtil: PreferencesUtil
){
    suspend fun modifyTrackListSortOptions(sortOrder: SortOrder, sortOption: SortOption, genreName: String?) {
        preferencesUtil.dataStore.edit { settings ->
            genreName?.also {
                Log.i("PREFS", "Modifying track genre prefs: $sortOption, $sortOrder, $genreName")
                val sortOptionKey = stringPreferencesKey("$genreName-${PreferencesUtil.TRACKS_SORT_OPTION}")
                val sortOrderKey = stringPreferencesKey("$genreName-${PreferencesUtil.TRACKS_SORT_ORDER}")
                settings[sortOptionKey] = sortOption.name
                settings[sortOrderKey] = sortOrder.name
            } ?: kotlin.run {
                Log.i("PREFS", "Modifying track root prefs: $sortOption, $sortOrder")
                settings[PreferencesUtil.TRACKS_SORT_OPTION] = sortOption.name
                settings[PreferencesUtil.TRACKS_SORT_ORDER] = sortOrder.name
            }
        }
    }


    fun getTrackSortOptions(genreName: String?): Flow<TracksSortSettings> {
        return preferencesUtil.dataStore.data.map { preferences ->
            genreName?.let {
                val sortOptionKey = stringPreferencesKey("$genreName-${PreferencesUtil.TRACKS_SORT_OPTION}")
                val sortOrderKey = stringPreferencesKey("$genreName-${PreferencesUtil.TRACKS_SORT_ORDER}")
                val sortOption = preferences[sortOptionKey]
                val sortOrder = preferences[sortOrderKey]
                if (sortOption != null && sortOrder != null) {
                    TracksSortSettings(SortOption.valueOf(sortOption), SortOrder.valueOf(sortOrder))
                } else {
                    TracksSortSettings(SortOption.TRACK_NAME, SortOrder.ASCENDING)
                }
            } ?: kotlin.run {
                val sortOption = preferences[PreferencesUtil.TRACKS_SORT_OPTION]
                val sortOrder = preferences[PreferencesUtil.TRACKS_SORT_ORDER]
                Log.i("PREFS", "new track root prefs: $sortOption, $sortOrder")
                if (sortOption != null && sortOrder != null) {
                    TracksSortSettings(SortOption.valueOf(sortOption), SortOrder.valueOf(sortOrder))
                } else {
                    TracksSortSettings(SortOption.TRACK_NAME, SortOrder.ASCENDING)
                }
            }
        }
    }

}