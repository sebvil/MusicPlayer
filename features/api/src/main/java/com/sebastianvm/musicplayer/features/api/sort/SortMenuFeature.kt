package com.sebastianvm.musicplayer.features.api.sort

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface SortMenuFeature : Feature {
    fun sortMenuUiComponent(arguments: SortMenuArguments): MvvmComponent<*, *, *>

    object Key : Feature.Key
}

fun FeatureRegistry.sortMenu(): SortMenuFeature = featureByKey(SortMenuFeature.Key)
