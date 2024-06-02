package com.sebastianvm.musicplayer.di

import android.content.Context
import com.sebastianvm.musicplayer.database.MusicDatabase
import com.sebastianvm.musicplayer.datastore.NowPlayingInfoDataSource
import com.sebastianvm.musicplayer.player.MediaPlaybackClient
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.album.AlbumRepositoryImpl
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.artist.ArtistRepositoryImpl
import com.sebastianvm.musicplayer.repository.fts.FullTextSearchRepository
import com.sebastianvm.musicplayer.repository.fts.FullTextSearchRepositoryImpl
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.genre.GenreRepositoryImpl
import com.sebastianvm.musicplayer.repository.music.MusicRepository
import com.sebastianvm.musicplayer.repository.music.MusicRepositoryImpl
import com.sebastianvm.musicplayer.repository.playback.PlaybackInfoDataSource
import com.sebastianvm.musicplayer.repository.playback.PlaybackInfoDataSourceImpl
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackManagerImpl
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepositoryImpl
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepositoryImpl
import com.sebastianvm.musicplayer.repository.queue.AppQueueRepository
import com.sebastianvm.musicplayer.repository.queue.QueueRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepositoryImpl
import kotlinx.coroutines.CoroutineScope

class RepositoryProvider(
    private val context: Context,
    private val dispatcherProvider: DispatcherProvider,
    private val database: MusicDatabase,
    private val jetpackDataStoreProvider: JetpackDataStoreProvider,
    private val applicationScope: CoroutineScope,
) {

    val musicRepository: MusicRepository
        get() = MusicRepositoryImpl(
            context = context,
            ioDispatcher = dispatcherProvider.ioDispatcher,
            musicDatabase = database,
            trackRepository = trackRepository
        )

    val albumRepository: AlbumRepository
        get() = AlbumRepositoryImpl(
            sortPreferencesRepository = sortPreferencesRepository,
            albumDao = database.getAlbumDao()
        )

    val artistRepository: ArtistRepository
        get() = ArtistRepositoryImpl(
            sortPreferencesRepository = sortPreferencesRepository,
            artistDao = database.getArtistDao()
        )

    val genreRepository: GenreRepository
        get() = GenreRepositoryImpl(
            sortPreferencesRepository = sortPreferencesRepository,
            genreDao = database.getGenreDao()
        )

    val trackRepository: TrackRepository
        get() = TrackRepositoryImpl(
            ioDispatcher = dispatcherProvider.ioDispatcher,
            sortPreferencesRepository = sortPreferencesRepository,
            trackDao = database.getTrackDao(),
            playlistRepository = playlistRepository,
            genreRepository = genreRepository,
            albumRepository = albumRepository
        )

    val playlistRepository: PlaylistRepository
        get() = PlaylistRepositoryImpl(
            sortPreferencesRepository = sortPreferencesRepository,
            playlistDao = database.getPlaylistDao(),
            ioDispatcher = dispatcherProvider.ioDispatcher,
            defaultDispatcher = dispatcherProvider.defaultDispatcher
        )

    private val playbackInfoDataSource: PlaybackInfoDataSource
        get() = PlaybackInfoDataSourceImpl(
            mediaQueueDao = database.getMediaQueueDao(),
            playbackInfoDataStore = jetpackDataStoreProvider.playbackInfoDataStore,
            ioDispatcher = dispatcherProvider.ioDispatcher
        )

    val playbackManager: PlaybackManager by lazy {
        PlaybackManagerImpl(
            mediaPlaybackClient = mediaPlaybackClient,
            playbackInfoDataSource = playbackInfoDataSource,
            ioDispatcher = dispatcherProvider.ioDispatcher,
            trackRepository = trackRepository
        )
    }

    val searchRepository: FullTextSearchRepository
        get() = FullTextSearchRepositoryImpl(
            trackFtsDao = database.getTrackFtsDao(),
            artistFtsDao = database.getArtistFtsDao(),
            albumFtsDao = database.getAlbumFtsDao(),
            genreFtsDao = database.getGenreFtsDao(),
            playlistFtsDao = database.getPlaylistFtsDao()
        )

    val sortPreferencesRepository: SortPreferencesRepository
        get() = SortPreferencesRepositoryImpl(
            sortPreferencesDataStore = jetpackDataStoreProvider.sortPreferencesDataStore
        )

    private val mediaPlaybackClient: MediaPlaybackClient by lazy {
        MediaPlaybackClient(context = context, externalScope = applicationScope)
    }

    val queueRepository: QueueRepository
        get() = AppQueueRepository(
            NowPlayingInfoDataSource(jetpackDataStoreProvider.nowPlayingInfoDataStore),
            database.getMediaQueueDao()
        )
}
