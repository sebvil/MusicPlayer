package com.sebastianvm.musicplayer.database

import android.content.Context
import androidx.room.Room
import com.sebastianvm.musicplayer.database.daos.AlbumDao
import com.sebastianvm.musicplayer.database.daos.AlbumFtsDao
import com.sebastianvm.musicplayer.database.daos.ArtistDao
import com.sebastianvm.musicplayer.database.daos.ArtistFtsDao
import com.sebastianvm.musicplayer.database.daos.GenreDao
import com.sebastianvm.musicplayer.database.daos.GenreFtsDao
import com.sebastianvm.musicplayer.database.daos.MediaQueueDao
import com.sebastianvm.musicplayer.database.daos.TrackDao
import com.sebastianvm.musicplayer.database.daos.TrackFtsDao
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
    fun provideMediaQueueDao(database: MusicDatabase): MediaQueueDao {
        return database.mediaQueueDao
    }

    @Provides
    @Singleton
    fun provideTrackFtsDao(database: MusicDatabase): TrackFtsDao {
        return database.trackFtsDao
    }

    @Provides
    @Singleton
    fun provideArtistFtsDao(database: MusicDatabase): ArtistFtsDao {
        return database.artistFtsDao
    }

    @Provides
    @Singleton
    fun provideAlbumFtsDao(database: MusicDatabase): AlbumFtsDao {
        return database.albumFtsDao
    }

    @Provides
    @Singleton
    fun provideGenreFtsDao(database: MusicDatabase): GenreFtsDao {
        return database.genreFtsDao
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