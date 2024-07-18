package com.sebastianvm.musicplayer.core.data.di

import android.content.Context
import com.sebastianvm.musicplayer.core.common.coroutines.DispatcherProvider
import com.sebastianvm.musicplayer.core.data.album.AlbumRepository
import com.sebastianvm.musicplayer.core.data.album.AlbumRepositoryImpl
import com.sebastianvm.musicplayer.core.data.artist.ArtistRepository
import com.sebastianvm.musicplayer.core.data.artist.ArtistRepositoryImpl
import com.sebastianvm.musicplayer.core.data.fts.FullTextSearchRepository
import com.sebastianvm.musicplayer.core.data.fts.FullTextSearchRepositoryImpl
import com.sebastianvm.musicplayer.core.data.genre.GenreRepository
import com.sebastianvm.musicplayer.core.data.genre.GenreRepositoryImpl
import com.sebastianvm.musicplayer.core.data.music.MusicRepository
import com.sebastianvm.musicplayer.core.data.music.MusicRepositoryImpl
import com.sebastianvm.musicplayer.core.data.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.core.data.playlist.PlaylistRepositoryImpl
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepositoryImpl
import com.sebastianvm.musicplayer.core.data.queue.AppQueueRepository
import com.sebastianvm.musicplayer.core.data.queue.QueueRepository
import com.sebastianvm.musicplayer.core.data.track.TrackRepository
import com.sebastianvm.musicplayer.core.data.track.TrackRepositoryImpl
import com.sebastianvm.musicplayer.core.database.di.DaoProvider
import com.sebastianvm.musicplayer.core.datastore.di.DataSourcesProvider

class DefaultRepositoryProvider(
    private val context: Context,
    private val dispatcherProvider: DispatcherProvider,
    private val database: DaoProvider,
    private val dataSourcesProvider: DataSourcesProvider,
) : RepositoryProvider {

    override val musicRepository: MusicRepository
        get() =
            MusicRepositoryImpl(
                context = context,
                ioDispatcher = dispatcherProvider.ioDispatcher,
                trackRepository = trackRepository,
            )

    override val albumRepository: AlbumRepository
        get() =
            AlbumRepositoryImpl(
                sortPreferencesRepository = sortPreferencesRepository,
                albumDao = database.getAlbumDao(),
            )

    override val artistRepository: ArtistRepository
        get() =
            ArtistRepositoryImpl(
                sortPreferencesRepository = sortPreferencesRepository,
                artistDao = database.getArtistDao(),
            )

    override val genreRepository: GenreRepository
        get() =
            GenreRepositoryImpl(
                sortPreferencesRepository = sortPreferencesRepository,
                genreDao = database.getGenreDao(),
            )

    override val trackRepository: TrackRepository
        get() =
            TrackRepositoryImpl(
                sortPreferencesRepository = sortPreferencesRepository,
                trackDao = database.getTrackDao(),
            )

    override val playlistRepository: PlaylistRepository
        get() =
            PlaylistRepositoryImpl(
                sortPreferencesRepository = sortPreferencesRepository,
                playlistDao = database.getPlaylistDao(),
                ioDispatcher = dispatcherProvider.ioDispatcher,
            )

    override val searchRepository: FullTextSearchRepository
        get() =
            FullTextSearchRepositoryImpl(
                trackFtsDao = database.getTrackFtsDao(),
                artistFtsDao = database.getArtistFtsDao(),
                albumFtsDao = database.getAlbumFtsDao(),
                genreFtsDao = database.getGenreFtsDao(),
                playlistFtsDao = database.getPlaylistFtsDao(),
            )

    override val sortPreferencesRepository: SortPreferencesRepository
        get() =
            SortPreferencesRepositoryImpl(
                sortPreferencesDataStore = dataSourcesProvider.sortPreferencesDataSource)

    override val queueRepository: QueueRepository
        get() =
            AppQueueRepository(
                nowPlayingInfoDataSource = dataSourcesProvider.nowPlayingInfoDataSource,
                mediaQueueDao = database.getMediaQueueDao(),
            )
}
