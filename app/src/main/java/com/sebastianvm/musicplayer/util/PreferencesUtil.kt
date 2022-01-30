package com.sebastianvm.musicplayer.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

// TODO might need to refactor this

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesUtil @Inject constructor(val dataStore: DataStore<Preferences>) {

    companion object {
        val TRACKS_SORT_OPTION = stringPreferencesKey("TRACKS_SORT_OPTION")
        val TRACKS_SORT_ORDER = stringPreferencesKey("TRACKS_SORT_ORDER")
        val ALBUMS_SORT_OPTION = stringPreferencesKey("ALBUMS_SORT_OPTION")
        val ALBUMS_SORT_ORDER = stringPreferencesKey("ALBUMS_SORT_ORDER")
        val ARTISTS_SORT_ORDER = stringPreferencesKey("ARTISTS_SORT_ORDER")
        val GENRES_SORT_ORDER = stringPreferencesKey("GENRES_SORT_ORDER")
        val PLAYLISTS_SORT_ORDER = stringPreferencesKey("PLAYLIST_SORT_ORDER")
        val SAVED_PLAYBACK_INFO_MEDIA_GROUP = stringPreferencesKey("SAVED_PLAYBACK_INFO_MEDIA_GROUP")
        val SAVED_PLAYBACK_INFO_MEDIA_GROUP_ID = stringPreferencesKey("SAVED_PLAYBACK_INFO_MEDIA_GROUP_ID")
        val SAVED_PLAYBACK_INFO_MEDIA_ID = stringPreferencesKey("CURRENT_PLAYBACK_MEDIA_ID")
        val SAVED_PLAYBACK_INFO_POSITION = longPreferencesKey("SAVED_PLAYBACK_INFO_POSITION")
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
