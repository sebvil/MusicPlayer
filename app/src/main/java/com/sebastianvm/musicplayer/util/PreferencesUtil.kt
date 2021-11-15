package com.sebastianvm.musicplayer.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesUtil @Inject constructor(val dataStore: DataStore<Preferences>) {

    companion object {
        val TRACK_COUNT = longPreferencesKey("track_count")
        val ARTIST_COUNT = longPreferencesKey("artist_count")
        val ALBUM_COUNT = longPreferencesKey("album_count")
        val GENRE_COUNT = longPreferencesKey("genre_count")
    }
}


@InstallIn(SingletonComponent::class)
@Module
object PreferencesModule {

    @Provides
    @Singleton
    fun getPreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create { context.preferencesDataStoreFile(name = "settings")}
    }
}