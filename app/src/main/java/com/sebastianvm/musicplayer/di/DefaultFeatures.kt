package com.sebastianvm.musicplayer.di

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.album.details.DefaultAlbumDetailsFeature
import com.sebastianvm.musicplayer.features.album.list.DefaultAlbumListFeature
import com.sebastianvm.musicplayer.features.api.Features
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsFeature
import com.sebastianvm.musicplayer.features.api.album.list.AlbumListFeature
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuFeature
import com.sebastianvm.musicplayer.features.api.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.api.sort.SortMenuFeature
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuFeature
import com.sebastianvm.musicplayer.features.sort.SortMenuUiComponent
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu

class DefaultFeatures : Features {
    override val trackContextMenuFeature: TrackContextMenuFeature =
        object : TrackContextMenuFeature {
            override fun trackContextMenuUiComponent(
                arguments: TrackContextMenuArguments,
                navController: NavController,
                features: Features,
            ): UiComponent<*> {
                return TrackContextMenu(
                    arguments = arguments, navController = navController, features = features)
            }
        }

    override val albumDetailsFeature: AlbumDetailsFeature = DefaultAlbumDetailsFeature()
    override val albumListFeature: AlbumListFeature = DefaultAlbumListFeature()

    override val albumContextMenuFeature: AlbumContextMenuFeature =
        object : AlbumContextMenuFeature {
            override fun albumContextMenuUiComponent(
                arguments: AlbumContextMenuArguments,
                navController: NavController
            ): UiComponent<*> {
                return albumContextMenuUiComponent(arguments, navController)
            }
        }

    override val sortMenuFeature: SortMenuFeature =
        object : SortMenuFeature {
            override fun sortMenuUiComponent(
                arguments: SortMenuArguments,
            ): UiComponent<*> {
                return SortMenuUiComponent(arguments)
            }
        }
}
