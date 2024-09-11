package com.sebastianvm.musicplayer.features.api.genre.details

import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface GenreDetailsFeature : Feature<GenreDetailsArguments, GenreDetailsProps> {

    object Key : Feature.Key
}

fun FeatureRegistry.genreDetails(): Feature<GenreDetailsArguments, GenreDetailsProps> =
    featureByKey(GenreDetailsFeature.Key)
