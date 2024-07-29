package com.sebastianvm.musicplayer.features.api.playlist.tracksearch

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface TrackSearchFeature : Feature {
    fun trackSearchUiComponent(
        arguments: TrackSearchArguments,
        navController: NavController,
    ): UiComponent<*>

    object Key : Feature.Key
}

fun FeatureRegistry.trackSearch(): TrackSearchFeature = featureByKey(TrackSearchFeature.Key)
