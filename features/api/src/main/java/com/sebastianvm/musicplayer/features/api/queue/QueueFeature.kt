package com.sebastianvm.musicplayer.features.api.queue

import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface QueueFeature : Feature {
    fun queueUiComponent(): UiComponent<*>

    object Key : Feature.Key
}

fun FeatureRegistry.queue(): QueueFeature = featureByKey(QueueFeature.Key)
