package com.sebastianvm.musicplayer.features.artist.list

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.artist.list.ArtistListFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

class DefaultArtistListFeature(
    private val repositoryProvider: RepositoryProvider,
    private val features: FeatureRegistry,
) : ArtistListFeature {
    override fun artistListUiComponent(navController: NavController): MvvmComponent {
        return ArtistListMvvmComponent(
            navController = navController,
            artistRepository = repositoryProvider.artistRepository,
            sortPreferencesRepository = repositoryProvider.sortPreferencesRepository,
            features = features,
        )
    }
}
