package com.sebastianvm.musicplayer.features.api.artist.details

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface ArtistDetailsFeature : Feature {
    fun artistDetailsUiComponent(
        arguments: ArtistDetailsArguments,
        navController: NavController,
    ): MvvmComponent

    object Key : Feature.Key
}

fun FeatureRegistry.artistDetails(): ArtistDetailsFeature = featureByKey(ArtistDetailsFeature.Key)
