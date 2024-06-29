package com.sebastianvm.musicplayer.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.sebastianvm.musicplayer.datastore.KotlinSerializationSerializer
import com.sebastianvm.musicplayer.datastore.SavedPlaybackInfo
import com.sebastianvm.musicplayer.util.sort.SortPreferences

class JetpackDataStoreProvider(private val context: Context) {

    private val Context.sortPreferencesDataStore: DataStore<SortPreferences> by
        dataStore(
            fileName = SORT_PREFERENCES_DATA_STORE_FILE_NAME,
            serializer =
                KotlinSerializationSerializer(SortPreferences(), SortPreferences.serializer()),
        )

    val sortPreferencesDataStore: DataStore<SortPreferences>
        get() = context.sortPreferencesDataStore

    private val Context.nowPlayingInfoDataStore: DataStore<SavedPlaybackInfo> by
        dataStore(
            fileName = NOW_PLAYING_INFO_DATA_STORE_FILE_NAME,
            serializer =
                KotlinSerializationSerializer(SavedPlaybackInfo(), SavedPlaybackInfo.serializer()),
        )

    val nowPlayingInfoDataStore: DataStore<SavedPlaybackInfo>
        get() = context.nowPlayingInfoDataStore

    companion object {
        private const val SORT_PREFERENCES_DATA_STORE_FILE_NAME = "sort_prefs.json"
        private const val NOW_PLAYING_INFO_DATA_STORE_FILE_NAME = "now_playing_info.json"
    }
}
