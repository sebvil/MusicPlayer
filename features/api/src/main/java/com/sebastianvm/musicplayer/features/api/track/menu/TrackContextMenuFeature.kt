package com.sebastianvm.musicplayer.features.api.track.menu

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface TrackContextMenuFeature : Feature {
    fun trackContextMenuUiComponent(
        arguments: TrackContextMenuArguments,
        navController: NavController,
    ): UiComponent<*>

    object Key : Feature.Key
}

fun FeatureRegistry.trackContextMenu(): TrackContextMenuFeature =
    featureByKey(TrackContextMenuFeature.Key)
