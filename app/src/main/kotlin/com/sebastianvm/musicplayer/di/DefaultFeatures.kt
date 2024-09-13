package com.sebastianvm.musicplayer.di

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
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureFactory
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import me.tatarka.inject.annotations.Inject

@Inject
class DefaultFeatures(
    albumDetailsFeature: () -> AlbumDetailsFeature,
    albumListFeature: () -> AlbumListFeature,
    albumContextMenuFeature: () -> AlbumContextMenuFeature,
    artistDetailsFeature: () -> ArtistDetailsFeature,
    artistListFeature: () -> ArtistListFeature,
    artistContextMenuFeature: () -> ArtistContextMenuFeature,
    artistsMenuFeature: () -> ArtistsMenuFeature,
    genreDetailsFeature: () -> GenreDetailsFeature,
    genreListFeature: () -> GenreListFeature,
    genreContextMenuFeature: () -> GenreContextMenuFeature,
    homeFeature: () -> HomeFeature,
    navigationHostFeature: () -> NavigationHostFeature,
    playerFeature: () -> PlayerFeature,
    playlistDetailsFeature: () -> PlaylistDetailsFeature,
    playlistListFeature: () -> PlaylistListFeature,
    playlistContextMenuFeature: () -> PlaylistContextMenuFeature,
    trackSearchFeature: () -> TrackSearchFeature,
    searchFeature: () -> SearchFeature,
    sortMenuFeature: () -> SortMenuFeature,
    trackListFeature: () -> TrackListFeature,
    trackContextMenuFeature: () -> TrackContextMenuFeature,
    queueFeature: () -> QueueFeature,
) : FeatureRegistry {

    private val features = mutableMapOf<Feature.Key, FeatureFactory>()

    private fun register(key: Feature.Key, featureFactory: FeatureFactory) {
        features[key] = featureFactory
    }

    @Suppress("UNCHECKED_CAST")
    override fun <F : Feature<*, *>> featureByKey(key: Feature.Key): F {
        return features[key]?.invoke() as F
    }

    init {
        register(key = AlbumDetailsFeature.Key, featureFactory = albumDetailsFeature)
        register(key = AlbumListFeature.Key, featureFactory = albumListFeature)
        register(key = AlbumContextMenuFeature.Key, featureFactory = albumContextMenuFeature)
        register(key = ArtistDetailsFeature.Key, featureFactory = artistDetailsFeature)
        register(key = ArtistListFeature.Key, featureFactory = artistListFeature)
        register(key = ArtistContextMenuFeature.Key, featureFactory = artistContextMenuFeature)
        register(key = ArtistsMenuFeature.Key, featureFactory = artistsMenuFeature)
        register(key = GenreDetailsFeature.Key, featureFactory = genreDetailsFeature)
        register(key = GenreListFeature.Key, featureFactory = genreListFeature)
        register(key = GenreContextMenuFeature.Key, featureFactory = genreContextMenuFeature)
        register(key = HomeFeature.Key, featureFactory = homeFeature)
        register(key = NavigationHostFeature.Key, featureFactory = navigationHostFeature)
        register(key = PlayerFeature.Key, featureFactory = playerFeature)
        register(key = PlaylistDetailsFeature.Key, featureFactory = playlistDetailsFeature)
        register(key = PlaylistListFeature.Key, featureFactory = playlistListFeature)
        register(key = PlaylistContextMenuFeature.Key, featureFactory = playlistContextMenuFeature)
        register(key = TrackSearchFeature.Key, featureFactory = trackSearchFeature)
        register(key = SearchFeature.Key, featureFactory = searchFeature)
        register(key = SortMenuFeature.Key, featureFactory = sortMenuFeature)
        register(key = TrackListFeature.Key, featureFactory = trackListFeature)
        register(key = TrackContextMenuFeature.Key, featureFactory = trackContextMenuFeature)
        register(key = QueueFeature.Key, featureFactory = queueFeature)
    }
}
