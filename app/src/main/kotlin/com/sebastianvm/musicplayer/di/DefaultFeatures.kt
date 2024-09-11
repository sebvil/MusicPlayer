package com.sebastianvm.musicplayer.di

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
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
import com.sebastianvm.musicplayer.features.registry.DefaultFeatureRegistry
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import com.sebastianvm.musicplayer.features.search.DefaultSearchFeature
import com.sebastianvm.musicplayer.features.sort.DefaultSortMenuFeature
import com.sebastianvm.musicplayer.features.track.list.DefaultTrackListFeature
import com.sebastianvm.musicplayer.features.track.menu.DefaultTrackContextMenuFeature

fun initializeFeatures(
    repositoryProvider: RepositoryProvider,
    playbackManager: PlaybackManager,
): FeatureRegistry {
    return DefaultFeatureRegistry().apply {
        register(
            key = AlbumDetailsFeature.Key,
            feature =
                DefaultAlbumDetailsFeature(
                    albumRepository = repositoryProvider.albumRepository,
                    playbackManager = playbackManager,
                    features = this,
                ),
        )
        register(
            key = AlbumListFeature.Key,
            feature =
                DefaultAlbumListFeature(
                    albumRepository = repositoryProvider.albumRepository,
                    sortPreferencesRepository = repositoryProvider.sortPreferencesRepository,
                    features = this,
                ),
        )
        register(
            key = AlbumContextMenuFeature.Key,
            feature =
                DefaultAlbumContextMenuFeature(
                    albumRepository = repositoryProvider.albumRepository,
                    playbackManager = playbackManager,
                    features = this,
                ),
        )

        register(
            key = ArtistDetailsFeature.Key,
            feature =
                DefaultArtistDetailsFeature(
                    artistRepository = repositoryProvider.artistRepository,
                    features = this,
                ),
        )
        register(
            key = ArtistListFeature.Key,
            feature =
                DefaultArtistListFeature(
                    artistRepository = repositoryProvider.artistRepository,
                    sortPreferencesRepository = repositoryProvider.sortPreferencesRepository,
                    features = this,
                ),
        )
        register(
            key = ArtistContextMenuFeature.Key,
            feature =
                DefaultArtistContextMenuFeature(
                    artistRepository = repositoryProvider.artistRepository,
                    playbackManager = playbackManager,
                ),
        )

        register(
            key = ArtistsMenuFeature.Key,
            feature =
                DefaultArtistsMenuFeature(
                    artistRepository = repositoryProvider.artistRepository,
                    features = this,
                ),
        )

        register(
            key = GenreDetailsFeature.Key,
            feature =
                DefaultGenreDetailsFeature(
                    genreRepository = repositoryProvider.genreRepository,
                    sortPreferencesRepository = repositoryProvider.sortPreferencesRepository,
                    playbackManager = playbackManager,
                    features = this,
                ),
        )
        register(
            key = GenreListFeature.Key,
            feature =
                DefaultGenreListFeature(
                    genreRepository = repositoryProvider.genreRepository,
                    sortPreferencesRepository = repositoryProvider.sortPreferencesRepository,
                    features = this,
                ),
        )
        register(
            key = GenreContextMenuFeature.Key,
            feature =
                DefaultGenreContextMenuFeature(
                    genreRepository = repositoryProvider.genreRepository,
                    playbackManager = playbackManager,
                ),
        )

        register(key = HomeFeature.Key, feature = DefaultHomeFeature(features = this))

        register(
            key = NavigationHostFeature.Key,
            feature = DefaultNavigationHostFeature(features = this),
        )

        register(
            key = PlayerFeature.Key,
            feature = DefaultPlayerFeature(playbackManager = playbackManager, features = this),
        )

        register(
            key = PlaylistDetailsFeature.Key,
            feature =
                DefaultPlaylistDetailsFeature(
                    playlistRepository = repositoryProvider.playlistRepository,
                    sortPreferencesRepository = repositoryProvider.sortPreferencesRepository,
                    playbackManager = playbackManager,
                    features = this,
                ),
        )
        register(
            key = PlaylistListFeature.Key,
            feature =
                DefaultPlaylistListFeature(
                    playlistRepository = repositoryProvider.playlistRepository,
                    sortPreferencesRepository = repositoryProvider.sortPreferencesRepository,
                    features = this,
                ),
        )
        register(
            key = PlaylistContextMenuFeature.Key,
            feature =
                DefaultPlaylistContextMenuFeature(
                    playlistRepository = repositoryProvider.playlistRepository,
                    playbackManager = playbackManager,
                ),
        )
        register(
            key = TrackSearchFeature.Key,
            feature =
                DefaultTrackSearchFeature(
                    playlistRepository = repositoryProvider.playlistRepository,
                    searchRepository = repositoryProvider.searchRepository,
                ),
        )

        register(
            key = SearchFeature.Key,
            feature =
                DefaultSearchFeature(
                    searchRepository = repositoryProvider.searchRepository,
                    playbackManager = playbackManager,
                    features = this,
                ),
        )

        register(
            key = SortMenuFeature.Key,
            feature =
                DefaultSortMenuFeature(
                    sortPreferencesRepository = repositoryProvider.sortPreferencesRepository
                ),
        )

        register(
            key = TrackListFeature.Key,
            feature =
                DefaultTrackListFeature(
                    trackRepository = repositoryProvider.trackRepository,
                    sortPreferencesRepository = repositoryProvider.sortPreferencesRepository,
                    playbackManager = playbackManager,
                    features = this,
                ),
        )
        register(
            key = TrackContextMenuFeature.Key,
            feature =
                DefaultTrackContextMenuFeature(
                    trackRepository = repositoryProvider.trackRepository,
                    playlistRepository = repositoryProvider.playlistRepository,
                    playbackManager = playbackManager,
                    features = this,
                ),
        )

        register(
            key = QueueFeature.Key,
            feature =
                DefaultQueueFeature(
                    queueRepository = repositoryProvider.queueRepository,
                    playbackManager = playbackManager,
                ),
        )
    }
}
