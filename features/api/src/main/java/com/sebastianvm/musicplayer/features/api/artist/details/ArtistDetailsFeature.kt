package com.sebastianvm.musicplayer.features.api.artist.details

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface ArtistDetailsFeature : Feature {
    fun artistDetailsUiComponent(
        arguments: ArtistDetailsArguments,
        navController: NavController,
    ): UiComponent<*>

    object Key : Feature.Key
}

fun FeatureRegistry.artistDetails(): ArtistDetailsFeature = featureByKey(ArtistDetailsFeature.Key)
