package com.sebastianvm.musicplayer.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.sebastianvm.musicplayer.core.datastore.KotlinSerializationSerializer
import com.sebastianvm.musicplayer.core.datastore.playinfo.DefaultNowPlayingInfoDataSource
import com.sebastianvm.musicplayer.core.datastore.playinfo.NowPlayingInfoDataSource
import com.sebastianvm.musicplayer.core.datastore.playinfo.SavedPlaybackInfo
import com.sebastianvm.musicplayer.core.datastore.sort.DefaultSortPreferencesDataSource
import com.sebastianvm.musicplayer.core.datastore.sort.SortPreferences
import com.sebastianvm.musicplayer.core.datastore.sort.SortPreferencesDataSource

class DataSourcesProvider(private val context: Context) {

    private val Context.sortPreferencesDataStore: DataStore<SortPreferences> by
        dataStore(
            fileName = SORT_PREFERENCES_DATA_STORE_FILE_NAME,
            serializer =
                KotlinSerializationSerializer(SortPreferences(), SortPreferences.serializer()),
        )

    val sortPreferencesDataSource: SortPreferencesDataSource
        get() = DefaultSortPreferencesDataSource(context.sortPreferencesDataStore)

    private val Context.nowPlayingInfoDataStore: DataStore<SavedPlaybackInfo> by
        dataStore(
            fileName = NOW_PLAYING_INFO_DATA_STORE_FILE_NAME,
            serializer =
                KotlinSerializationSerializer(SavedPlaybackInfo(), SavedPlaybackInfo.serializer()),
        )

    val nowPlayingInfoDataSource: NowPlayingInfoDataSource
        get() = DefaultNowPlayingInfoDataSource(context.nowPlayingInfoDataStore)

    companion object {
        private const val SORT_PREFERENCES_DATA_STORE_FILE_NAME = "sort_prefs.json"
        private const val NOW_PLAYING_INFO_DATA_STORE_FILE_NAME = "now_playing_info.json"
    }
}
