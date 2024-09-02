package com.sebastianvm.musicplayer.features.api.track.list

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface TrackListFeature : Feature {
    fun trackListUiComponent(navController: NavController): MvvmComponent

    object Key : Feature.Key
}

fun FeatureRegistry.trackListFeature(): TrackListFeature = featureByKey(TrackListFeature.Key)
