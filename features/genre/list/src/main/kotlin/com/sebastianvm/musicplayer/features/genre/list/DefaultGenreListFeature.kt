package com.sebastianvm.musicplayer.features.genre.list

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.genre.list.GenreListFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

class DefaultGenreListFeature(
    private val repositoryProvider: RepositoryProvider,
    private val features: FeatureRegistry,
) : GenreListFeature {
    override fun genreListUiComponent(navController: NavController): MvvmComponent<*, *, *> {
        return GenreListMvvmComponent(
            navController = navController,
            genreRepository = repositoryProvider.genreRepository,
            sortPreferencesRepository = repositoryProvider.sortPreferencesRepository,
            features = features,
        )
    }
}
