package com.sebastianvm.musicplayer.features.api.artist.menu

import com.sebastianvm.musicplayer.core.ui.mvvm.NoProps
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface ArtistContextMenuFeature : Feature<ArtistContextMenuArguments, NoProps> {
    object Key : Feature.Key
}

fun FeatureRegistry.artistContextMenu(): Feature<ArtistContextMenuArguments, NoProps> =
    featureByKey(ArtistContextMenuFeature.Key)
