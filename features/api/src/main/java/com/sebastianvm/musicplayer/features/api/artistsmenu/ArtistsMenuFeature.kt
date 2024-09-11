package com.sebastianvm.musicplayer.features.api.artistsmenu

import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface ArtistsMenuFeature : Feature<ArtistsMenuArguments, ArtistsMenuProps> {
    object Key : Feature.Key
}

fun FeatureRegistry.artistsMenu(): Feature<ArtistsMenuArguments, ArtistsMenuProps> =
    featureByKey(ArtistsMenuFeature.Key)
