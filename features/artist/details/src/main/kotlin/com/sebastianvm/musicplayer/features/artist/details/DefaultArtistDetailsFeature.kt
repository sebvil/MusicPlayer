package com.sebastianvm.musicplayer.features.artist.details

import com.sebastianvm.musicplayer.core.data.artist.ArtistRepository
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsArguments
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

class DefaultArtistDetailsFeature(
    private val artistRepository: ArtistRepository,
    private val features: FeatureRegistry
) : ArtistDetailsFeature {
    override fun artistDetailsUiComponent(
        arguments: ArtistDetailsArguments,
        navController: NavController,
    ): MvvmComponent {
        return ArtistDetailsMvvmComponent(
            arguments = arguments,
            navController = navController,
            artistRepository = artistRepository,
            features = features
        )
    }
}
