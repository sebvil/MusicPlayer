package com.sebastianvm.musicplayer.features.api.track.list

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface TrackListFeature : Feature {
    fun trackListUiComponent(navController: NavController): UiComponent<*>

    object Key : Feature.Key
}

fun FeatureRegistry.trackListFeature(): TrackListFeature = featureByKey(TrackListFeature.Key)
