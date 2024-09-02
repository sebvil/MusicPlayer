package com.sebastianvm.musicplayer.features.api.genre.details

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface GenreDetailsFeature : Feature {
    fun genreDetailsUiComponent(
        arguments: GenreDetailsArguments,
        navController: NavController,
    ): MvvmComponent

    object Key : Feature.Key
}

fun FeatureRegistry.genreDetails(): GenreDetailsFeature = featureByKey(GenreDetailsFeature.Key)
