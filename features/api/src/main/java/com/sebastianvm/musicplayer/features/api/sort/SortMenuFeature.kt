package com.sebastianvm.musicplayer.features.api.sort

import com.sebastianvm.musicplayer.core.ui.mvvm.NoProps
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface SortMenuFeature : Feature<SortMenuArguments, NoProps> {
    object Key : Feature.Key
}

fun FeatureRegistry.sortMenu(): Feature<SortMenuArguments, NoProps> =
    featureByKey(SortMenuFeature.Key)
