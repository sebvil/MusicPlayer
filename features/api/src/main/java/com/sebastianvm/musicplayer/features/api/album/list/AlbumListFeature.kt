package com.sebastianvm.musicplayer.features.api.album.list

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface AlbumListFeature : Feature {
    fun albumListUiComponent(navController: NavController): UiComponent<*>

    object Key : Feature.Key
}

fun FeatureRegistry.albumList(): AlbumListFeature = featureByKey(AlbumListFeature.Key)
