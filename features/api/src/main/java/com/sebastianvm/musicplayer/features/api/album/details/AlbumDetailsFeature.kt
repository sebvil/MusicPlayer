package com.sebastianvm.musicplayer.features.api.album.details

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface AlbumDetailsFeature : Feature {
    fun albumDetailsUiComponent(
        arguments: AlbumDetailsArguments,
        navController: NavController,
    ): UiComponent<*>

    object Key : Feature.Key
}

fun FeatureRegistry.albumDetails(): AlbumDetailsFeature = featureByKey(AlbumDetailsFeature.Key)
