package com.sebastianvm.musicplayer.features.api.home

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface HomeFeature : Feature {
    fun homeUiComponent(navController: NavController): MvvmComponent<*, *, *>

    object Key : Feature.Key
}

fun FeatureRegistry.home(): HomeFeature = featureByKey(HomeFeature.Key)
