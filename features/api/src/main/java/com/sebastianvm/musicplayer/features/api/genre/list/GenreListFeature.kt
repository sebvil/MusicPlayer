package com.sebastianvm.musicplayer.features.api.genre.list

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface GenreListFeature : Feature {
    fun genreListUiComponent(navController: NavController): MvvmComponent<*, *, *>

    object Key : Feature.Key
}

fun FeatureRegistry.genreList(): GenreListFeature = featureByKey(GenreListFeature.Key)
