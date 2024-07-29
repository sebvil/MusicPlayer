package com.sebastianvm.musicplayer.features.api.artist.menu

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface ArtistContextMenuFeature : Feature {
    fun artistContextMenuUiComponent(
        arguments: ArtistContextMenuArguments,
        navController: NavController,
    ): UiComponent<*>

    object Key : Feature.Key
}

fun FeatureRegistry.artistContextMenu(): ArtistContextMenuFeature =
    featureByKey(ArtistContextMenuFeature.Key)
