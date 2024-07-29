package com.sebastianvm.musicplayer.features.api.genre.list

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface GenreListFeature : Feature {
    fun genreListUiComponent(navController: NavController): UiComponent<*>

    object Key : Feature.Key
}

fun FeatureRegistry.genreList(): GenreListFeature = featureByKey(GenreListFeature.Key)
