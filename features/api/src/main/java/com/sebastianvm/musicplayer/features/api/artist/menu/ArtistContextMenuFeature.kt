package com.sebastianvm.musicplayer.features.api.artist.menu

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface ArtistContextMenuFeature : Feature {
    fun artistContextMenuUiComponent(arguments: ArtistContextMenuArguments): MvvmComponent<*, *, *>

    object Key : Feature.Key
}

fun FeatureRegistry.artistContextMenu(): ArtistContextMenuFeature =
    featureByKey(ArtistContextMenuFeature.Key)
