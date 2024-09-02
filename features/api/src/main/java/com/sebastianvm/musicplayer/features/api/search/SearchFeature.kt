package com.sebastianvm.musicplayer.features.api.search

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface SearchFeature : Feature {
    fun searchUiComponent(navController: NavController): MvvmComponent

    object Key : Feature.Key
}

fun FeatureRegistry.searchFeature(): SearchFeature = featureByKey(SearchFeature.Key)
