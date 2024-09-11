package com.sebastianvm.musicplayer.features.api.track.list

import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface TrackListFeature : Feature<TrackListArguments, TrackListProps> {
    object Key : Feature.Key
}

fun FeatureRegistry.trackListFeature(): Feature<TrackListArguments, TrackListProps> =
    featureByKey(TrackListFeature.Key)
