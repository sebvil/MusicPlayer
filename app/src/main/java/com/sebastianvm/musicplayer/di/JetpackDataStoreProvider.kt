package com.sebastianvm.musicplayer.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.sebastianvm.musicplayer.util.sort.SortPreferences
import com.sebastianvm.musicplayer.util.sort.SortPreferencesSerializer

class JetpackDataStoreProvider(private val context: Context) {

    private val Context.sortPreferencesDataStore: DataStore<SortPreferences> by dataStore(
        fileName = SORT_PREFERENCES_DATA_STORE_FILE_NAME,
        serializer = SortPreferencesSerializer
    )

    val sortPreferencesDataStore: DataStore<SortPreferences>
        get() = context.sortPreferencesDataStore

    private val Context.playbackInfoDataStore: DataStore<Preferences> by preferencesDataStore(
        name = PLAYBACK_INFO_PREFERENCES_FILE,
    )

    val playbackInfoDataStore: DataStore<Preferences>
        get() = context.playbackInfoDataStore

    companion object {
        private const val SORT_PREFERENCES_DATA_STORE_FILE_NAME = "sort_prefs.json"
        private const val PLAYBACK_INFO_PREFERENCES_FILE = "playback_info"
    }
}
