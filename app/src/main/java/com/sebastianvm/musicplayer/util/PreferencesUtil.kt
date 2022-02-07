package com.sebastianvm.musicplayer.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.sebastianvm.musicplayer.util.sort.SortSettings
import com.sebastianvm.musicplayer.util.sort.SortSettingsSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

private const val SORT_SETTINGS_DATA_STORE_FILE_NAME = "sort_settings.pb"

object PreferencesUtil {
    val SAVED_PLAYBACK_INFO_MEDIA_GROUP =
        stringPreferencesKey("SAVED_PLAYBACK_INFO_MEDIA_GROUP")
    val SAVED_PLAYBACK_INFO_MEDIA_GROUP_ID =
        stringPreferencesKey("SAVED_PLAYBACK_INFO_MEDIA_GROUP_ID")
    val SAVED_PLAYBACK_INFO_MEDIA_ID = stringPreferencesKey("CURRENT_PLAYBACK_MEDIA_ID")
    val SAVED_PLAYBACK_INFO_POSITION = longPreferencesKey("SAVED_PLAYBACK_INFO_POSITION")

}


@InstallIn(SingletonComponent::class)
@Module
object PreferencesModule {
    @Provides
    @Singleton
    fun getPreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create { context.preferencesDataStoreFile(name = "settings") }
    }

    @Provides
    @Singleton
    fun getSortSettingsDataStore(@ApplicationContext context: Context): DataStore<SortSettings> {
        return DataStoreFactory.create(
            serializer = SortSettingsSerializer,
            produceFile = { context.dataStoreFile(SORT_SETTINGS_DATA_STORE_FILE_NAME) },
            corruptionHandler = null,
            migrations = listOf(),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }
}
