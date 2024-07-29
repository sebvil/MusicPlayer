package com.sebastianvm.musicplayer.di

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
import com.sebastianvm.musicplayer.features.api.navigation.NavigationFeature
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
import com.sebastianvm.musicplayer.features.navigation.DefaultNavigationFeature
import com.sebastianvm.musicplayer.features.player.DefaultPlayerFeature
import com.sebastianvm.musicplayer.features.playlist.details.DefaultPlaylistDetailsFeature
import com.sebastianvm.musicplayer.features.playlist.list.DefaultPlaylistListFeature
import com.sebastianvm.musicplayer.features.playlist.menu.DefaultPlaylistContextMenuFeature
import com.sebastianvm.musicplayer.features.playlist.tracksearch.DefaultTracksSearchFeature
import com.sebastianvm.musicplayer.features.queue.DefaultQueueFeature
import com.sebastianvm.musicplayer.features.registry.DefaultFeatureRegistry
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import com.sebastianvm.musicplayer.features.search.DefaultSearchFeature
import com.sebastianvm.musicplayer.features.sort.DefaultSortMenuFeature
import com.sebastianvm.musicplayer.features.track.list.DefaultTrackListFeature
import com.sebastianvm.musicplayer.features.track.menu.DefaultTrackContextMenuFeature

fun initializeFeatures(): FeatureRegistry {
    return DefaultFeatureRegistry().apply {
        register(AlbumDetailsFeature.Key, DefaultAlbumDetailsFeature())
        register(AlbumListFeature.Key, DefaultAlbumListFeature())
        register(AlbumContextMenuFeature.Key, DefaultAlbumContextMenuFeature())

        register(ArtistDetailsFeature.Key, DefaultArtistDetailsFeature())
        register(ArtistListFeature.Key, DefaultArtistListFeature())
        register(ArtistContextMenuFeature.Key, DefaultArtistContextMenuFeature())

        register(ArtistsMenuFeature.Key, DefaultArtistsMenuFeature())

        register(GenreDetailsFeature.Key, DefaultGenreDetailsFeature())
        register(GenreListFeature.Key, DefaultGenreListFeature())
        register(GenreContextMenuFeature.Key, DefaultGenreContextMenuFeature())

        register(HomeFeature.Key, DefaultHomeFeature())

        register(NavigationFeature.Key, DefaultNavigationFeature())

        register(PlayerFeature.Key, DefaultPlayerFeature())

        register(PlaylistDetailsFeature.Key, DefaultPlaylistDetailsFeature())
        register(PlaylistListFeature.Key, DefaultPlaylistListFeature())
        register(PlaylistContextMenuFeature.Key, DefaultPlaylistContextMenuFeature())
        register(TrackSearchFeature.Key, DefaultTracksSearchFeature())

        register(SearchFeature.Key, DefaultSearchFeature())

        register(SortMenuFeature.Key, DefaultSortMenuFeature())

        register(TrackListFeature.Key, DefaultTrackListFeature())
        register(TrackContextMenuFeature.Key, DefaultTrackContextMenuFeature())

        register(QueueFeature.Key, DefaultQueueFeature())
    }
}
