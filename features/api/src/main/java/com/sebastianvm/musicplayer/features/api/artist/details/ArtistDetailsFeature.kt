package com.sebastianvm.musicplayer.features.api.artist.details

import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface ArtistDetailsFeature : Feature<ArtistDetailsArguments, ArtistDetailsProps> {
    object Key : Feature.Key
}

fun FeatureRegistry.artistDetails(): Feature<ArtistDetailsArguments, ArtistDetailsProps> =
    featureByKey(ArtistDetailsFeature.Key)
