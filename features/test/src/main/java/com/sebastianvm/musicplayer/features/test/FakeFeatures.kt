package com.sebastianvm.musicplayer.features.test

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
import com.sebastianvm.musicplayer.features.registry.DefaultFeatureRegistry
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import com.sebastianvm.musicplayer.features.test.album.details.FakeAlbumDetailsFeature
import com.sebastianvm.musicplayer.features.test.album.list.FakeAlbumListFeature
import com.sebastianvm.musicplayer.features.test.album.menu.FakeAlbumContextMenuFeature
import com.sebastianvm.musicplayer.features.test.artist.details.FakeArtistDetailsFeature
import com.sebastianvm.musicplayer.features.test.artist.list.FakeArtistListFeature
import com.sebastianvm.musicplayer.features.test.artist.menu.FakeArtistContextMenuFeature
import com.sebastianvm.musicplayer.features.test.artistsmenu.FakeArtistsMenuFeature
import com.sebastianvm.musicplayer.features.test.genre.details.FakeGenreDetailsFeature
import com.sebastianvm.musicplayer.features.test.genre.list.FakeGenreListFeature
import com.sebastianvm.musicplayer.features.test.genre.menu.FakeGenreContextMenuFeature
import com.sebastianvm.musicplayer.features.test.home.FakeHomeFeature
import com.sebastianvm.musicplayer.features.test.navigation.FakeNavigationFeature
import com.sebastianvm.musicplayer.features.test.player.FakePlayerFeature
import com.sebastianvm.musicplayer.features.test.playlist.details.FakePlaylistDetailsFeature
import com.sebastianvm.musicplayer.features.test.playlist.list.FakePlaylistListFeature
import com.sebastianvm.musicplayer.features.test.playlist.menu.FakePlaylistContextMenuFeature
import com.sebastianvm.musicplayer.features.test.playlist.tracksearch.FakeTrackSearchFeature
import com.sebastianvm.musicplayer.features.test.queue.FakeQueueFeature
import com.sebastianvm.musicplayer.features.test.search.FakeSearchFeature
import com.sebastianvm.musicplayer.features.test.sort.FakeSortMenuFeature
import com.sebastianvm.musicplayer.features.test.track.list.FakeTrackListFeature
import com.sebastianvm.musicplayer.features.test.track.menu.FakeTrackContextMenuFeature

fun initializeFakeFeatures(): FeatureRegistry {
    return DefaultFeatureRegistry().apply {
        register(AlbumDetailsFeature.Key, FakeAlbumDetailsFeature())
        register(AlbumListFeature.Key, FakeAlbumListFeature())
        register(AlbumContextMenuFeature.Key, FakeAlbumContextMenuFeature())

        register(ArtistDetailsFeature.Key, FakeArtistDetailsFeature())
        register(ArtistListFeature.Key, FakeArtistListFeature())
        register(ArtistContextMenuFeature.Key, FakeArtistContextMenuFeature())

        register(ArtistsMenuFeature.Key, FakeArtistsMenuFeature())

        register(GenreDetailsFeature.Key, FakeGenreDetailsFeature())
        register(GenreListFeature.Key, FakeGenreListFeature())
        register(GenreContextMenuFeature.Key, FakeGenreContextMenuFeature())

        register(HomeFeature.Key, FakeHomeFeature())

        register(NavigationFeature.Key, FakeNavigationFeature())

        register(PlayerFeature.Key, FakePlayerFeature())

        register(PlaylistDetailsFeature.Key, FakePlaylistDetailsFeature())
        register(PlaylistListFeature.Key, FakePlaylistListFeature())
        register(PlaylistContextMenuFeature.Key, FakePlaylistContextMenuFeature())
        register(TrackSearchFeature.Key, FakeTrackSearchFeature())

        register(SearchFeature.Key, FakeSearchFeature())

        register(SortMenuFeature.Key, FakeSortMenuFeature())

        register(TrackListFeature.Key, FakeTrackListFeature())
        register(TrackContextMenuFeature.Key, FakeTrackContextMenuFeature())

        register(QueueFeature.Key, FakeQueueFeature())
    }
}
