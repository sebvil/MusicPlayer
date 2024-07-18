package com.sebastianvm.musicplayer.di

import com.sebastianvm.musicplayer.featues.album.details.DefaultAlbumDetailsFeature
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu
import com.sebastianvm.musicplayer.services.features.Features
import com.sebastianvm.musicplayer.services.features.album.details.AlbumDetailsFeature
import com.sebastianvm.musicplayer.services.features.navigation.NavController
import com.sebastianvm.musicplayer.services.features.navigation.UiComponent
import com.sebastianvm.musicplayer.services.features.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.services.features.track.menu.TrackContextMenuFeature

class DefaultFeatures : Features {
    override val trackContextMenuFeature: TrackContextMenuFeature =
        object : TrackContextMenuFeature {
            override fun trackContextMenuUiComponent(
                arguments: TrackContextMenuArguments,
                navController: NavController
            ): UiComponent<TrackContextMenuArguments, *> {
                return TrackContextMenu(arguments, navController)
            }
        }

    override val albumDetailsFeature: AlbumDetailsFeature = DefaultAlbumDetailsFeature()
}
