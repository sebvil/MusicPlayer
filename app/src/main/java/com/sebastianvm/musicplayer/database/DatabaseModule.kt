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
import com.sebastianvm.musicplayer.database.daos.PlaylistDao
import com.sebastianvm.musicplayer.database.daos.PlaylistFtsDao
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
        return database.getTrackDao()
    }

    @Provides
    @Singleton
    fun provideArtistDao(database: MusicDatabase): ArtistDao {
        return database.getArtistDao()
    }

    @Provides
    @Singleton
    fun provideAlbumDao(database: MusicDatabase): AlbumDao {
        return database.getAlbumDao()
    }

    @Provides
    @Singleton
    fun provideGenreDao(database: MusicDatabase): GenreDao {
        return database.getGenreDao()
    }

    @Provides
    @Singleton
    fun providePlaylistDao(database: MusicDatabase): PlaylistDao {
        return database.getPlaylistDao()
    }

    @Provides
    @Singleton
    fun provideMediaQueueDao(database: MusicDatabase): MediaQueueDao {
        return database.getMediaQueueDao()
    }

    @Provides
    @Singleton
    fun provideTrackFtsDao(database: MusicDatabase): TrackFtsDao {
        return database.getTrackFtsDao()
    }

    @Provides
    @Singleton
    fun provideArtistFtsDao(database: MusicDatabase): ArtistFtsDao {
        return database.getArtistFtsDao()
    }

    @Provides
    @Singleton
    fun provideAlbumFtsDao(database: MusicDatabase): AlbumFtsDao {
        return database.getAlbumFtsDao()
    }

    @Provides
    @Singleton
    fun provideGenreFtsDao(database: MusicDatabase): GenreFtsDao {
        return database.getGenreFtsDao()
    }

    @Provides
    @Singleton
    fun providePlaylistFtsDao(database: MusicDatabase): PlaylistFtsDao {
        return database.getPlaylistFtsDao()
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
