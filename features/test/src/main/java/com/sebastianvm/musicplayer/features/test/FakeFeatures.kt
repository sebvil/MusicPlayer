package com.sebastianvm.musicplayer.features.test

import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.mvvm.NoProps
import com.sebastianvm.musicplayer.core.ui.mvvm.Props
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsArguments
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsFeature
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsProps
import com.sebastianvm.musicplayer.features.api.album.list.AlbumListArguments
import com.sebastianvm.musicplayer.features.api.album.list.AlbumListFeature
import com.sebastianvm.musicplayer.features.api.album.list.AlbumListProps
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuFeature
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuProps
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsArguments
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsFeature
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsProps
import com.sebastianvm.musicplayer.features.api.artist.list.ArtistListArguments
import com.sebastianvm.musicplayer.features.api.artist.list.ArtistListFeature
import com.sebastianvm.musicplayer.features.api.artist.list.ArtistListProps
import com.sebastianvm.musicplayer.features.api.artist.menu.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.features.api.artist.menu.ArtistContextMenuFeature
import com.sebastianvm.musicplayer.features.api.artistsmenu.ArtistsMenuArguments
import com.sebastianvm.musicplayer.features.api.artistsmenu.ArtistsMenuFeature
import com.sebastianvm.musicplayer.features.api.artistsmenu.ArtistsMenuProps
import com.sebastianvm.musicplayer.features.api.genre.details.GenreDetailsArguments
import com.sebastianvm.musicplayer.features.api.genre.details.GenreDetailsFeature
import com.sebastianvm.musicplayer.features.api.genre.details.GenreDetailsProps
import com.sebastianvm.musicplayer.features.api.genre.list.GenreListArguments
import com.sebastianvm.musicplayer.features.api.genre.list.GenreListFeature
import com.sebastianvm.musicplayer.features.api.genre.list.GenreListProps
import com.sebastianvm.musicplayer.features.api.genre.menu.GenreContextMenuArguments
import com.sebastianvm.musicplayer.features.api.genre.menu.GenreContextMenuFeature
import com.sebastianvm.musicplayer.features.api.home.HomeArguments
import com.sebastianvm.musicplayer.features.api.home.HomeFeature
import com.sebastianvm.musicplayer.features.api.home.HomeProps
import com.sebastianvm.musicplayer.features.api.navigation.NavigationHostArguments
import com.sebastianvm.musicplayer.features.api.navigation.NavigationHostFeature
import com.sebastianvm.musicplayer.features.api.player.PlayerArguments
import com.sebastianvm.musicplayer.features.api.player.PlayerFeature
import com.sebastianvm.musicplayer.features.api.player.PlayerProps
import com.sebastianvm.musicplayer.features.api.playlist.details.PlaylistDetailsArguments
import com.sebastianvm.musicplayer.features.api.playlist.details.PlaylistDetailsFeature
import com.sebastianvm.musicplayer.features.api.playlist.details.PlaylistDetailsProps
import com.sebastianvm.musicplayer.features.api.playlist.list.PlaylistListArguments
import com.sebastianvm.musicplayer.features.api.playlist.list.PlaylistListFeature
import com.sebastianvm.musicplayer.features.api.playlist.list.PlaylistListProps
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuFeature
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuProps
import com.sebastianvm.musicplayer.features.api.playlist.tracksearch.TrackSearchArguments
import com.sebastianvm.musicplayer.features.api.playlist.tracksearch.TrackSearchFeature
import com.sebastianvm.musicplayer.features.api.playlist.tracksearch.TrackSearchProps
import com.sebastianvm.musicplayer.features.api.queue.QueueArguments
import com.sebastianvm.musicplayer.features.api.queue.QueueFeature
import com.sebastianvm.musicplayer.features.api.search.SearchArguments
import com.sebastianvm.musicplayer.features.api.search.SearchFeature
import com.sebastianvm.musicplayer.features.api.search.SearchProps
import com.sebastianvm.musicplayer.features.api.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.api.sort.SortMenuFeature
import com.sebastianvm.musicplayer.features.api.track.list.TrackListArguments
import com.sebastianvm.musicplayer.features.api.track.list.TrackListFeature
import com.sebastianvm.musicplayer.features.api.track.list.TrackListProps
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuFeature
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuProps
import com.sebastianvm.musicplayer.features.registry.DefaultFeatureRegistry
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

fun initializeFakeFeatures(): FeatureRegistry {
    return DefaultFeatureRegistry().apply {
        register(AlbumDetailsFeature.Key, FakeFeature<AlbumDetailsArguments, AlbumDetailsProps>())
        register(AlbumListFeature.Key, FakeFeature<AlbumListArguments, AlbumListProps>())
        register(
            AlbumContextMenuFeature.Key,
            FakeFeature<AlbumContextMenuArguments, AlbumContextMenuProps>(),
        )

        register(
            ArtistDetailsFeature.Key,
            FakeFeature<ArtistDetailsArguments, ArtistDetailsProps>(),
        )
        register(ArtistListFeature.Key, FakeFeature<ArtistListArguments, ArtistListProps>())
        register(ArtistContextMenuFeature.Key, FakeFeature<ArtistContextMenuArguments, NoProps>())

        register(ArtistsMenuFeature.Key, FakeFeature<ArtistsMenuArguments, ArtistsMenuProps>())

        register(GenreDetailsFeature.Key, FakeFeature<GenreDetailsArguments, GenreDetailsProps>())
        register(GenreListFeature.Key, FakeFeature<GenreListArguments, GenreListProps>())
        register(GenreContextMenuFeature.Key, FakeFeature<GenreContextMenuArguments, NoProps>())

        register(HomeFeature.Key, FakeFeature<HomeArguments, HomeProps>())

        register(NavigationHostFeature.Key, FakeFeature<NavigationHostArguments, NoProps>())

        register(PlayerFeature.Key, FakeFeature<PlayerArguments, PlayerProps>())

        register(
            PlaylistDetailsFeature.Key,
            FakeFeature<PlaylistDetailsArguments, PlaylistDetailsProps>(),
        )
        register(PlaylistListFeature.Key, FakeFeature<PlaylistListArguments, PlaylistListProps>())
        register(
            PlaylistContextMenuFeature.Key,
            FakeFeature<PlaylistContextMenuArguments, PlaylistContextMenuProps>(),
        )
        register(TrackSearchFeature.Key, FakeFeature<TrackSearchArguments, TrackSearchProps>())

        register(SearchFeature.Key, FakeFeature<SearchArguments, SearchProps>())

        register(SortMenuFeature.Key, FakeFeature<SortMenuArguments, NoProps>())

        register(TrackListFeature.Key, FakeFeature<TrackListArguments, TrackListProps>())
        register(
            TrackContextMenuFeature.Key,
            FakeFeature<TrackContextMenuArguments, TrackContextMenuProps>(),
        )

        register(QueueFeature.Key, FakeFeature<QueueArguments, NoProps>())
    }
}

class FakeFeature<A : Arguments, P : Props> : Feature<A, P> {
    override val initializer: MvvmComponent.Initializer<A, P> =
        MvvmComponent.Initializer { args, _ -> FakeMvvmComponent(arguments = args) }
}
