package com.sebastianvm.musicplayer.features.track.list

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.track.list.TrackListFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

class DefaultTrackListFeature(
    private val repositoryProvider: RepositoryProvider,
    private val playbackManager: PlaybackManager,
    private val features: FeatureRegistry,
) : TrackListFeature {
    override fun trackListUiComponent(navController: NavController): MvvmComponent {
        return TrackListMvvmComponent(
            navController = navController,
            trackRepository = repositoryProvider.trackRepository,
            sortPreferencesRepository = repositoryProvider.sortPreferencesRepository,
            playbackManager = playbackManager,
            features = features,
        )
    }
}
