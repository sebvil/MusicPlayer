package com.sebastianvm.musicplayer.features.genre.details

import com.sebastianvm.musicplayer.core.data.genre.GenreRepository
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.genre.details.GenreDetailsArguments
import com.sebastianvm.musicplayer.features.api.genre.details.GenreDetailsFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

class DefaultGenreDetailsFeature(
    private val genreRepository: GenreRepository,
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val playbackManager: PlaybackManager,
    private val features: FeatureRegistry
) : GenreDetailsFeature {
    override fun genreDetailsUiComponent(
        arguments: GenreDetailsArguments,
        navController: NavController,
    ): MvvmComponent {
        return GenreDetailsMvvmComponent(
            arguments = arguments,
            navController = navController,
            genreRepository = genreRepository,
            sortPreferencesRepository = sortPreferencesRepository,
            playbackManager = playbackManager,
            features = features
        )
    }
}
