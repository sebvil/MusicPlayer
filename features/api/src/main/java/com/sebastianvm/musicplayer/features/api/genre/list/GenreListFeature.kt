package com.sebastianvm.musicplayer.features.api.genre.list

import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface GenreListFeature : Feature<GenreListArguments, GenreListProps> {
    object Key : Feature.Key
}

fun FeatureRegistry.genreList(): Feature<GenreListArguments, GenreListProps> =
    featureByKey(GenreListFeature.Key)
