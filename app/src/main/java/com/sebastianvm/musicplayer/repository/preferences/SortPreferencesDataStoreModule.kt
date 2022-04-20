package com.sebastianvm.musicplayer.repository.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.sebastianvm.musicplayer.util.sort.SortPreferences
import com.sebastianvm.musicplayer.util.sort.SortPreferencesSerializer
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
    fun provideSortPreferencesDataStore(@ApplicationContext appContext: Context): DataStore<SortPreferences> {
        return DataStoreFactory.create(
            serializer = SortPreferencesSerializer,
            corruptionHandler = null,
            migrations = listOf(),
            produceFile = { appContext.dataStoreFile(SORT_SETTINGS_DATA_STORE_FILE_NAME) },
        )
    }
}