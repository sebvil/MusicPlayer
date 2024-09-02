package com.sebastianvm.musicplayer.features.artistsmenu

import com.sebastianvm.musicplayer.core.data.artist.ArtistRepository
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.artistsmenu.ArtistsMenuArguments
import com.sebastianvm.musicplayer.features.api.artistsmenu.ArtistsMenuFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

class DefaultArtistsMenuFeature(
    private val artistRepository: ArtistRepository,
    private val features: FeatureRegistry
) : ArtistsMenuFeature {
    override fun artistsMenuUiComponent(
        arguments: ArtistsMenuArguments,
        navController: NavController,
    ): MvvmComponent {
        return ArtistsMenuMvvmComponent(
            arguments = arguments,
            navController = navController,
            artistRepository = artistRepository,
            features = features
        )
    }
}
