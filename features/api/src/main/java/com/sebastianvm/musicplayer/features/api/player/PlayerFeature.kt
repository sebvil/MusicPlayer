package com.sebastianvm.musicplayer.features.api.player

import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface PlayerFeature : Feature<PlayerArguments, PlayerProps> {
    object Key : Feature.Key
}

fun FeatureRegistry.playerFeature(): Feature<PlayerArguments, PlayerProps> =
    featureByKey(PlayerFeature.Key)
