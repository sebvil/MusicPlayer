package com.sebastianvm.musicplayer.features.api.track.menu

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface TrackContextMenuFeature : Feature {
    fun trackContextMenuUiComponent(
        arguments: TrackContextMenuArguments,
        navController: NavController,
    ): MvvmComponent<*, *, *>

    object Key : Feature.Key
}

fun FeatureRegistry.trackContextMenu(): TrackContextMenuFeature =
    featureByKey(TrackContextMenuFeature.Key)
