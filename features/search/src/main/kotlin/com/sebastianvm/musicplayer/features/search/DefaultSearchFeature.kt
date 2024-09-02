package com.sebastianvm.musicplayer.features.search

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.search.SearchFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

class DefaultSearchFeature(
    private val repositoryProvider: RepositoryProvider,
    private val playbackManager: PlaybackManager,
    private val features: FeatureRegistry,
) : SearchFeature {
    override fun searchUiComponent(navController: NavController): MvvmComponent {
        return SearchMvvmComponent(
            navController = navController,
            searchRepository = repositoryProvider.searchRepository,
            playbackManager = playbackManager,
            features = features,
        )
    }
}
