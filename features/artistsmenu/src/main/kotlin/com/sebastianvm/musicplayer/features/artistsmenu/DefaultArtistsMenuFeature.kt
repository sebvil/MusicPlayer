package com.sebastianvm.musicplayer.features.artistsmenu

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.artistsmenu.ArtistsMenuArguments
import com.sebastianvm.musicplayer.features.api.artistsmenu.ArtistsMenuFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

class DefaultArtistsMenuFeature(
    private val repositoryProvider: RepositoryProvider,
    private val features: FeatureRegistry,
) : ArtistsMenuFeature {
    override fun artistsMenuUiComponent(
        arguments: ArtistsMenuArguments,
        navController: NavController,
    ): MvvmComponent<*, *, *> {
        return ArtistsMenuMvvmComponent(
            arguments = arguments,
            navController = navController,
            artistRepository = repositoryProvider.artistRepository,
            features = features,
        )
    }
}
