package com.sebastianvm.musicplayer.di

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.album.details.DefaultAlbumDetailsFeature
import com.sebastianvm.musicplayer.features.album.list.DefaultAlbumListFeature
import com.sebastianvm.musicplayer.features.album.menu.DefaultAlbumContextMenuFeature
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsFeature
import com.sebastianvm.musicplayer.features.api.album.list.AlbumListFeature
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuFeature
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsFeature
import com.sebastianvm.musicplayer.features.api.artist.list.ArtistListFeature
import com.sebastianvm.musicplayer.features.api.artist.menu.ArtistContextMenuFeature
import com.sebastianvm.musicplayer.features.api.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.api.sort.SortMenuFeature
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuFeature
import com.sebastianvm.musicplayer.features.artist.details.DefaultArtistDetailsFeature
import com.sebastianvm.musicplayer.features.artist.list.DefaultArtistListFeature
import com.sebastianvm.musicplayer.features.artist.menu.DefaultArtistContextMenuFeature
import com.sebastianvm.musicplayer.features.registry.DefaultFeatureRegistry
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import com.sebastianvm.musicplayer.features.sort.SortMenuUiComponent
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu

fun initializeFeatures(): FeatureRegistry {
    return DefaultFeatureRegistry().apply {
        register(AlbumListFeature.Key, DefaultAlbumListFeature())
        register(AlbumDetailsFeature.Key, DefaultAlbumDetailsFeature())
        register(AlbumContextMenuFeature.Key, DefaultAlbumContextMenuFeature())

        register(ArtistListFeature.Key, DefaultArtistListFeature())
        register(ArtistDetailsFeature.Key, DefaultArtistDetailsFeature())
        register(ArtistContextMenuFeature.Key, DefaultArtistContextMenuFeature())

        register(
            TrackContextMenuFeature.Key,
            object : TrackContextMenuFeature {
                override fun trackContextMenuUiComponent(
                    arguments: TrackContextMenuArguments,
                    navController: NavController,
                ): UiComponent<*> {
                    return TrackContextMenu(arguments = arguments, navController = navController)
                }
            })

        register(
            SortMenuFeature.Key,
            object : SortMenuFeature {
                override fun sortMenuUiComponent(
                    arguments: SortMenuArguments,
                ): UiComponent<*> {
                    return SortMenuUiComponent(arguments)
                }
            })
    }
}
