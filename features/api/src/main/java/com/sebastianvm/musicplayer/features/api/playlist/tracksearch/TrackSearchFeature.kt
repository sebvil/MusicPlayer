package com.sebastianvm.musicplayer.features.api.playlist.tracksearch

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface TrackSearchFeature : Feature {
    fun trackSearchUiComponent(
        arguments: TrackSearchArguments,
        navController: NavController,
    ): MvvmComponent<*, *, *>

    object Key : Feature.Key
}

fun FeatureRegistry.trackSearch(): TrackSearchFeature = featureByKey(TrackSearchFeature.Key)
