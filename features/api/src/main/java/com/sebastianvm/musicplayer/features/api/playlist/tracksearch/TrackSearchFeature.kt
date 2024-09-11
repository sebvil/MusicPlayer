package com.sebastianvm.musicplayer.features.api.playlist.tracksearch

import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface TrackSearchFeature : Feature<TrackSearchArguments, TrackSearchProps> {
    object Key : Feature.Key
}

fun FeatureRegistry.trackSearch(): Feature<TrackSearchArguments, TrackSearchProps> =
    featureByKey(TrackSearchFeature.Key)
