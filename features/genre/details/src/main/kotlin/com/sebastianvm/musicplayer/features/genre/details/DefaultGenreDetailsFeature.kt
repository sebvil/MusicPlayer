package com.sebastianvm.musicplayer.features.genre.details

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.genre.details.GenreDetailsArguments
import com.sebastianvm.musicplayer.features.api.genre.details.GenreDetailsFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

class DefaultGenreDetailsFeature(
    private val repositoryProvider: RepositoryProvider,
    private val playbackManager: PlaybackManager,
    private val features: FeatureRegistry,
) : GenreDetailsFeature {
    override fun genreDetailsUiComponent(
        arguments: GenreDetailsArguments,
        navController: NavController,
    ): MvvmComponent {
        return GenreDetailsMvvmComponent(
            arguments = arguments,
            navController = navController,
            genreRepository = repositoryProvider.genreRepository,
            sortPreferencesRepository = repositoryProvider.sortPreferencesRepository,
            playbackManager = playbackManager,
            features = features,
        )
    }
}
