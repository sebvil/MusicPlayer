package com.sebastianvm.musicplayer.features.api.navigation

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface NavigationFeature : Feature {
    fun navigationUiComponent(): MvvmComponent

    object Key : Feature.Key
}

fun FeatureRegistry.navigationFeature(): NavigationFeature = featureByKey(NavigationFeature.Key)
