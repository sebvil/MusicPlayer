package com.sebastianvm.musicplayer.features.api.queue

import com.sebastianvm.musicplayer.core.ui.mvvm.NoProps
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface QueueFeature : Feature<QueueArguments, NoProps> {
    object Key : Feature.Key
}

fun FeatureRegistry.queue(): Feature<QueueArguments, NoProps> = featureByKey(QueueFeature.Key)
