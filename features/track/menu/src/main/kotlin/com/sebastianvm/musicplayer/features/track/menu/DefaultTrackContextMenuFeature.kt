package com.sebastianvm.musicplayer.features.track.menu

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

class DefaultTrackContextMenuFeature(
    private val repositoryProvider: RepositoryProvider,
    private val playbackManager: PlaybackManager,
    private val features: FeatureRegistry,
) : TrackContextMenuFeature {
    override fun trackContextMenuUiComponent(
        arguments: TrackContextMenuArguments,
        navController: NavController,
    ): MvvmComponent<*, *, *> {
        return TrackContextMenuMvvmComponent(
            arguments = arguments,
            navController = navController,
            trackRepository = repositoryProvider.trackRepository,
            playlistRepository = repositoryProvider.playlistRepository,
            playbackManager = playbackManager,
            features = features,
        )
    }
}
