package com.sebastianvm.musicplayer.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.sebastianvm.musicplayer.annotations.AppScope
import com.sebastianvm.musicplayer.annotations.IoDispatcher
import com.sebastianvm.musicplayer.core.data.album.AlbumRepository
import com.sebastianvm.musicplayer.core.data.album.DefaultAlbumRepository
import com.sebastianvm.musicplayer.core.data.artist.ArtistRepository
import com.sebastianvm.musicplayer.core.data.artist.DefaultArtistRepository
import com.sebastianvm.musicplayer.core.data.di.RepositoryComponent
import com.sebastianvm.musicplayer.core.data.fts.DefaultFullTextSearchRepository
import com.sebastianvm.musicplayer.core.data.fts.FullTextSearchRepository
import com.sebastianvm.musicplayer.core.data.genre.DefaultGenreRepository
import com.sebastianvm.musicplayer.core.data.genre.GenreRepository
import com.sebastianvm.musicplayer.core.data.music.DefaultMusicRepository
import com.sebastianvm.musicplayer.core.data.music.MusicRepository
import com.sebastianvm.musicplayer.core.data.playlist.DefaultPlaylistRepository
import com.sebastianvm.musicplayer.core.data.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.core.data.preferences.DefaultSortPreferencesRepository
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.data.queue.DefaultQueueRepository
import com.sebastianvm.musicplayer.core.data.queue.QueueRepository
import com.sebastianvm.musicplayer.core.data.track.DefaultTrackRepository
import com.sebastianvm.musicplayer.core.data.track.TrackRepository
import com.sebastianvm.musicplayer.core.database.di.DefaultDaoProvider
import com.sebastianvm.musicplayer.core.datastore.playinfo.DefaultNowPlayingInfoDataSource
import com.sebastianvm.musicplayer.core.datastore.playinfo.NowPlayingInfoDataSource
import com.sebastianvm.musicplayer.core.datastore.playinfo.SavedPlaybackInfo
import com.sebastianvm.musicplayer.core.datastore.playinfo.SavedPlaybackInfoDataStore
import com.sebastianvm.musicplayer.core.datastore.sort.SortPreferences
import com.sebastianvm.musicplayer.core.datastore.sort.SortPreferencesDataStore
import com.sebastianvm.musicplayer.core.playback.manager.DefaultPlaybackManager
import com.sebastianvm.musicplayer.core.services.Services
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.features.album.details.DefaultAlbumDetailsFeature
import com.sebastianvm.musicplayer.features.album.list.DefaultAlbumListFeature
import com.sebastianvm.musicplayer.features.album.menu.DefaultAlbumContextMenuFeature
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsFeature
import com.sebastianvm.musicplayer.features.api.album.list.AlbumListFeature
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuFeature
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsFeature
import com.sebastianvm.musicplayer.features.api.artist.list.ArtistListFeature
import com.sebastianvm.musicplayer.features.api.artist.menu.ArtistContextMenuFeature
import com.sebastianvm.musicplayer.features.api.artistsmenu.ArtistsMenuFeature
import com.sebastianvm.musicplayer.features.api.genre.details.GenreDetailsFeature
import com.sebastianvm.musicplayer.features.api.genre.list.GenreListFeature
import com.sebastianvm.musicplayer.features.api.genre.menu.GenreContextMenuFeature
import com.sebastianvm.musicplayer.features.api.home.HomeFeature
import com.sebastianvm.musicplayer.features.api.navigation.NavigationHostFeature
import com.sebastianvm.musicplayer.features.api.player.PlayerFeature
import com.sebastianvm.musicplayer.features.api.playlist.details.PlaylistDetailsFeature
import com.sebastianvm.musicplayer.features.api.playlist.list.PlaylistListFeature
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuFeature
import com.sebastianvm.musicplayer.features.api.playlist.tracksearch.TrackSearchFeature
import com.sebastianvm.musicplayer.features.api.queue.QueueFeature
import com.sebastianvm.musicplayer.features.api.search.SearchFeature
import com.sebastianvm.musicplayer.features.api.sort.SortMenuFeature
import com.sebastianvm.musicplayer.features.api.track.list.TrackListFeature
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuFeature
import com.sebastianvm.musicplayer.features.artist.details.DefaultArtistDetailsFeature
import com.sebastianvm.musicplayer.features.artist.list.DefaultArtistListFeature
import com.sebastianvm.musicplayer.features.artist.menu.DefaultArtistContextMenuFeature
import com.sebastianvm.musicplayer.features.artistsmenu.DefaultArtistsMenuFeature
import com.sebastianvm.musicplayer.features.genre.details.DefaultGenreDetailsFeature
import com.sebastianvm.musicplayer.features.genre.list.DefaultGenreListFeature
import com.sebastianvm.musicplayer.features.genre.menu.DefaultGenreContextMenuFeature
import com.sebastianvm.musicplayer.features.home.DefaultHomeFeature
import com.sebastianvm.musicplayer.features.navigation.DefaultNavigationHostFeature
import com.sebastianvm.musicplayer.features.player.DefaultPlayerFeature
import com.sebastianvm.musicplayer.features.playlist.details.DefaultPlaylistDetailsFeature
import com.sebastianvm.musicplayer.features.playlist.list.DefaultPlaylistListFeature
import com.sebastianvm.musicplayer.features.playlist.menu.DefaultPlaylistContextMenuFeature
import com.sebastianvm.musicplayer.features.playlist.tracksearch.DefaultTrackSearchFeature
import com.sebastianvm.musicplayer.features.queue.DefaultQueueFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import com.sebastianvm.musicplayer.features.search.DefaultSearchFeature
import com.sebastianvm.musicplayer.features.sort.DefaultSortMenuFeature
import com.sebastianvm.musicplayer.features.track.list.DefaultTrackListFeature
import com.sebastianvm.musicplayer.features.track.menu.DefaultTrackContextMenuFeature
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@AppScope
@Component
abstract class AppComponent(
    @get:Provides protected val context: Context,
    @AppScope @get:Provides val ioDispatcher: @IoDispatcher CoroutineDispatcher,
) : DefaultDaoProvider(context, ioDispatcher), RepositoryComponent, Services {

    @AppScope
    @get:Provides
    protected val applicationScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    // region Repositories
    protected val DefaultPlaybackManager.bind: PlaybackManager
        @Provides get() = this

    protected val SortPreferencesDataStore.bind: DataStore<SortPreferences>
        @Provides get() = this

    protected val SavedPlaybackInfoDataStore.bind: DataStore<SavedPlaybackInfo>
        @Provides get() = this

    protected val DefaultNowPlayingInfoDataSource.bind: NowPlayingInfoDataSource
        @Provides get() = this

    protected val DefaultMusicRepository.bind: MusicRepository
        @Provides get() = this

    protected val DefaultAlbumRepository.bind: AlbumRepository
        @Provides get() = this

    protected val DefaultArtistRepository.bind: ArtistRepository
        @Provides get() = this

    protected val DefaultGenreRepository.bind: GenreRepository
        @Provides get() = this

    protected val DefaultTrackRepository.bind: TrackRepository
        @Provides get() = this

    protected val DefaultPlaylistRepository.bind: PlaylistRepository
        @Provides get() = this

    protected val DefaultFullTextSearchRepository.bind: FullTextSearchRepository
        @Provides get() = this

    protected val DefaultSortPreferencesRepository.bind: SortPreferencesRepository
        @Provides get() = this

    protected val DefaultQueueRepository.bind: QueueRepository
        @Provides get() = this

    // endregion

    // region Features
    protected val DefaultAlbumDetailsFeature.bind: AlbumDetailsFeature
        @Provides get() = this

    protected abstract val albumDetailsFeature: AlbumDetailsFeature

    protected val DefaultAlbumListFeature.bind: AlbumListFeature
        @Provides get() = this

    protected abstract val albumListFeature: AlbumListFeature

    protected val DefaultAlbumContextMenuFeature.bind: AlbumContextMenuFeature
        @Provides get() = this

    protected abstract val albumContextMenuFeature: AlbumContextMenuFeature

    protected val DefaultArtistDetailsFeature.bind: ArtistDetailsFeature
        @Provides get() = this

    protected abstract val artistDetailsFeature: ArtistDetailsFeature

    protected val DefaultArtistListFeature.bind: ArtistListFeature
        @Provides get() = this

    protected abstract val artistListFeature: ArtistListFeature

    protected val DefaultArtistContextMenuFeature.bind: ArtistContextMenuFeature
        @Provides get() = this

    protected abstract val artistContextMenuFeature: ArtistContextMenuFeature

    protected val DefaultArtistsMenuFeature.bind: ArtistsMenuFeature
        @Provides get() = this

    protected abstract val artistsMenuFeature: ArtistsMenuFeature

    protected val DefaultGenreDetailsFeature.bind: GenreDetailsFeature
        @Provides get() = this

    protected abstract val genreDetailsFeature: GenreDetailsFeature

    protected val DefaultGenreListFeature.bind: GenreListFeature
        @Provides get() = this

    protected abstract val genreListFeature: GenreListFeature

    protected val DefaultGenreContextMenuFeature.bind: GenreContextMenuFeature
        @Provides get() = this

    protected abstract val genreContextMenuFeature: GenreContextMenuFeature

    protected val DefaultHomeFeature.bind: HomeFeature
        @Provides get() = this

    protected abstract val homeFeature: HomeFeature

    protected val DefaultNavigationHostFeature.bind: NavigationHostFeature
        @Provides get() = this

    protected abstract val navigationHostFeature: NavigationHostFeature

    protected val DefaultPlayerFeature.bind: PlayerFeature
        @Provides get() = this

    protected abstract val playerFeature: PlayerFeature

    protected val DefaultPlaylistDetailsFeature.bind: PlaylistDetailsFeature
        @Provides get() = this

    protected abstract val playlistDetailsFeature: PlaylistDetailsFeature

    protected val DefaultPlaylistListFeature.bind: PlaylistListFeature
        @Provides get() = this

    protected abstract val playlistListFeature: PlaylistListFeature

    protected val DefaultPlaylistContextMenuFeature.bind: PlaylistContextMenuFeature
        @Provides get() = this

    protected abstract val playlistContextMenuFeature: PlaylistContextMenuFeature

    protected val DefaultTrackSearchFeature.bind: TrackSearchFeature
        @Provides get() = this

    protected abstract val trackSearchFeature: TrackSearchFeature

    protected val DefaultSearchFeature.bind: SearchFeature
        @Provides get() = this

    protected abstract val searchFeature: SearchFeature

    protected val DefaultSortMenuFeature.bind: SortMenuFeature
        @Provides get() = this

    protected abstract val sortMenuFeature: SortMenuFeature

    protected val DefaultTrackListFeature.bind: TrackListFeature
        @Provides get() = this

    protected abstract val trackListFeature: TrackListFeature

    protected val DefaultTrackContextMenuFeature.bind: TrackContextMenuFeature
        @Provides get() = this

    protected abstract val trackContextMenuFeature: TrackContextMenuFeature

    protected val DefaultQueueFeature.bind: QueueFeature
        @Provides get() = this

    protected abstract val queueFeature: QueueFeature

    protected val DefaultFeatures.bind: FeatureRegistry
        @AppScope @Provides get() = this

    abstract val features: FeatureRegistry
    // endregion

}
