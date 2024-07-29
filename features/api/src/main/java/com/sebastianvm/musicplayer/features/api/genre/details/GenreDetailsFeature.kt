package com.sebastianvm.musicplayer.features.api.genre.details

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface GenreDetailsFeature : Feature {
    fun genreDetailsUiComponent(
        arguments: GenreDetailsArguments,
        navController: NavController,
    ): UiComponent<*>

    object Key : Feature.Key
}

fun FeatureRegistry.genreDetails(): GenreDetailsFeature = featureByKey(GenreDetailsFeature.Key)
