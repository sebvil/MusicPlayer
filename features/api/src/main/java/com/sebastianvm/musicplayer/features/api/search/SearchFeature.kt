package com.sebastianvm.musicplayer.features.api.search

import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface SearchFeature : Feature<SearchArguments, SearchProps> {
    object Key : Feature.Key
}

fun FeatureRegistry.searchFeature(): Feature<SearchArguments, SearchProps> =
    featureByKey(SearchFeature.Key)
