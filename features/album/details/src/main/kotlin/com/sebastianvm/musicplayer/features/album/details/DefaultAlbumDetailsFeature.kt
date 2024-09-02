package com.sebastianvm.musicplayer.features.album.details

import com.sebastianvm.musicplayer.core.data.album.AlbumRepository
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsArguments
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

class DefaultAlbumDetailsFeature(
    private val albumRepository: AlbumRepository,
    private val playbackManager: PlaybackManager,
    private val features: FeatureRegistry,
) : AlbumDetailsFeature {
    override fun albumDetailsUiComponent(
        arguments: AlbumDetailsArguments,
        navController: NavController,
    ): MvvmComponent {
        return AlbumDetailsMvvmComponent(
            arguments = arguments,
            navController = navController,
            albumRepository = albumRepository,
            playbackManager = playbackManager,
            features = features
        )
    }
}
