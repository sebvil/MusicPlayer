package com.sebastianvm.musicplayer.features.api.album.menu

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface AlbumContextMenuFeature : Feature {
    fun albumContextMenuUiComponent(
        arguments: AlbumContextMenuArguments,
        navController: NavController,
    ): MvvmComponent

    object Key : Feature.Key
}

fun FeatureRegistry.albumContextMenu(): AlbumContextMenuFeature =
    featureByKey(AlbumContextMenuFeature.Key)
