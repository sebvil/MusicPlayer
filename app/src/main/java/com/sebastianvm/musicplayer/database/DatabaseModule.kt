package com.sebastianvm.musicplayer.database

import android.content.Context
import androidx.room.Room
import com.sebastianvm.musicplayer.database.daos.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTrackDao(database: MusicDatabase): TrackDao {
        return database.trackDao
    }

    @Provides
    @Singleton
    fun provideArtistDao(database: MusicDatabase): ArtistDao {
        return database.artistDao
    }

    @Provides
    @Singleton
    fun provideAlbumDao(database: MusicDatabase): AlbumDao {
        return database.albumDao
    }

    @Provides
    @Singleton
    fun provideGenreDao(database: MusicDatabase): GenreDao {
        return database.genreDao
    }

    @Provides
    @Singleton
    fun provideTrackFtsDao(database: MusicDatabase): TrackFtsDao {
        return database.trackFtsDao
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): MusicDatabase {
        return Room.databaseBuilder(
            appContext,
            MusicDatabase::class.java,
            "music_database"
        ).build()
    }
}