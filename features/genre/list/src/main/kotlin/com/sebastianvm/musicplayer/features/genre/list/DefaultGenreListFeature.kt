package com.sebastianvm.musicplayer.features.genre.list

import com.sebastianvm.musicplayer.core.data.genre.GenreRepository
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.genre.list.GenreListFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

class DefaultGenreListFeature(
    private val genreRepository: GenreRepository,
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val features: FeatureRegistry
) : GenreListFeature {
    override fun genreListUiComponent(navController: NavController): MvvmComponent {
        return GenreListMvvmComponent(
            navController = navController,
            genreRepository = genreRepository,
            sortPreferencesRepository = sortPreferencesRepository,
            features = features
        )
    }
}
