package com.sebastianvm.musicplayer.core.database.di

import android.content.Context
import androidx.room.Room
import com.sebastianvm.musicplayer.core.common.DispatcherNames
import com.sebastianvm.musicplayer.core.database.MusicDatabase
import com.sebastianvm.musicplayer.core.database.daos.AlbumDao
import com.sebastianvm.musicplayer.core.database.daos.AlbumFtsDao
import com.sebastianvm.musicplayer.core.database.daos.ArtistDao
import com.sebastianvm.musicplayer.core.database.daos.ArtistFtsDao
import com.sebastianvm.musicplayer.core.database.daos.GenreDao
import com.sebastianvm.musicplayer.core.database.daos.GenreFtsDao
import com.sebastianvm.musicplayer.core.database.daos.MediaQueueDao
import com.sebastianvm.musicplayer.core.database.daos.PlaylistDao
import com.sebastianvm.musicplayer.core.database.daos.PlaylistFtsDao
import com.sebastianvm.musicplayer.core.database.daos.TrackDao
import com.sebastianvm.musicplayer.core.database.daos.TrackFtsDao
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
fun getDaoProvider(
    context: Context,
    @Named(DispatcherNames.IO) ioDispatcher: CoroutineDispatcher,
): DaoProvider =
    Room.databaseBuilder(context, MusicDatabase::class.java, "music_database")
        .setQueryCoroutineContext(context = ioDispatcher)
        .build()

@Factory fun getTrackDao(daoProvider: DaoProvider): TrackDao = daoProvider.getTrackDao()

@Factory fun getArtistDao(daoProvider: DaoProvider): ArtistDao = daoProvider.getArtistDao()

@Factory fun getAlbumDao(daoProvider: DaoProvider): AlbumDao = daoProvider.getAlbumDao()

@Factory fun getGenreDao(daoProvider: DaoProvider): GenreDao = daoProvider.getGenreDao()

@Factory fun getPlaylistDao(daoProvider: DaoProvider): PlaylistDao = daoProvider.getPlaylistDao()

@Factory
fun getMediaQueueDao(daoProvider: DaoProvider): MediaQueueDao = daoProvider.getMediaQueueDao()

@Factory fun getTrackFtsDao(daoProvider: DaoProvider): TrackFtsDao = daoProvider.getTrackFtsDao()

@Factory fun getArtistFtsDao(daoProvider: DaoProvider): ArtistFtsDao = daoProvider.getArtistFtsDao()

@Factory fun getAlbumFtsDao(daoProvider: DaoProvider): AlbumFtsDao = daoProvider.getAlbumFtsDao()

@Factory fun getGenreFtsDao(daoProvider: DaoProvider): GenreFtsDao = daoProvider.getGenreFtsDao()

@Factory
fun getPlaylistFtsDao(daoProvider: DaoProvider): PlaylistFtsDao = daoProvider.getPlaylistFtsDao()
