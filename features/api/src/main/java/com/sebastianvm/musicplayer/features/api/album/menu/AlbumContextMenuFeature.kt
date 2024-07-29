package com.sebastianvm.musicplayer.features.api.album.menu

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface AlbumContextMenuFeature : Feature {
    fun albumContextMenuUiComponent(
        arguments: AlbumContextMenuArguments,
        navController: NavController,
    ): UiComponent<*>

    object Key : Feature.Key
}

fun FeatureRegistry.albumContextMenu(): AlbumContextMenuFeature =
    featureByKey(AlbumContextMenuFeature.Key)
