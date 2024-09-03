package com.sebastianvm.musicplayer.features.api.album.list

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface AlbumListFeature : Feature {
    fun albumListUiComponent(navController: NavController): MvvmComponent<*, *, *>

    object Key : Feature.Key
}

fun FeatureRegistry.albumList(): AlbumListFeature = featureByKey(AlbumListFeature.Key)
