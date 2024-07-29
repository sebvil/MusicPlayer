package com.sebastianvm.musicplayer.features.api.navigation

import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface NavigationFeature : Feature {
    fun navigationUiComponent(): UiComponent<*>

    object Key : Feature.Key
}

fun FeatureRegistry.navigationFeature(): NavigationFeature = featureByKey(NavigationFeature.Key)
