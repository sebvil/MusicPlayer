package com.sebastianvm.musicplayer.repository.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.sebastianvm.musicplayer.util.sort.SortSettings
import com.sebastianvm.musicplayer.util.sort.SortSettingsSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val SORT_SETTINGS_DATA_STORE_FILE_NAME = "sort_settings.pb"

@InstallIn(SingletonComponent::class)
@Module
object SortPreferencesDataStoreModule {

    @Singleton
    @Provides
    fun provideSortSettingsDataStore(@ApplicationContext appContext: Context): DataStore<SortSettings> {
        return DataStoreFactory.create(
            serializer = SortSettingsSerializer,
            corruptionHandler = null,
            migrations = listOf(),
            produceFile = { appContext.dataStoreFile(SORT_SETTINGS_DATA_STORE_FILE_NAME) },
        )
    }
}