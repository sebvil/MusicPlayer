package com.sebastianvm.musicplayer.features.api.track.menu

import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface TrackContextMenuFeature : Feature<TrackContextMenuArguments, TrackContextMenuProps> {
    object Key : Feature.Key
}

fun FeatureRegistry.trackContextMenu(): Feature<TrackContextMenuArguments, TrackContextMenuProps> =
    featureByKey(TrackContextMenuFeature.Key)
