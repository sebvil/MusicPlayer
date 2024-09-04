package com.sebastianvm.musicplayer.features.api.home

import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface HomeFeature : Feature<HomeArguments, HomeProps> {
    object Key : Feature.Key
}

fun FeatureRegistry.home(): Feature<HomeArguments, HomeProps> = featureByKey(HomeFeature.Key)
