package com.sebastianvm.musicplayer.features.api.artist.list

import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface ArtistListFeature : Feature<ArtistListArguments, ArtistListProps> {
    object Key : Feature.Key
}

fun FeatureRegistry.artistList(): Feature<ArtistListArguments, ArtistListProps> =
    featureByKey(ArtistListFeature.Key)
