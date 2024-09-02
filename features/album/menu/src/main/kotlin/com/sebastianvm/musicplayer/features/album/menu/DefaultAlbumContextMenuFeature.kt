package com.sebastianvm.musicplayer.features.album.menu

import com.sebastianvm.musicplayer.core.data.album.AlbumRepository
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

class DefaultAlbumContextMenuFeature(
    private val albumRepository: AlbumRepository,
    private val playbackManager: PlaybackManager,
    private val features: FeatureRegistry
) : AlbumContextMenuFeature {
    override fun albumContextMenuUiComponent(
        arguments: AlbumContextMenuArguments,
        navController: NavController,
    ): MvvmComponent {
        return AlbumContextMenuMvvmComponent(
            arguments = arguments,
            navController = navController,
            albumRepository = albumRepository,
            playbackManager = playbackManager,
            features = features
        )
    }
}
