package com.sebastianvm.musicplayer.features.api.navigation

import com.sebastianvm.musicplayer.core.ui.mvvm.NoProps
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface NavigationHostFeature : Feature<NavigationHostArguments, NoProps> {
    object Key : Feature.Key
}

fun FeatureRegistry.navigationFeature(): Feature<NavigationHostArguments, NoProps> =
    featureByKey(NavigationHostFeature.Key)
