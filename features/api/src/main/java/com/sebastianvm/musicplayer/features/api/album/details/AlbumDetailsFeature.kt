package com.sebastianvm.musicplayer.features.api.album.details

import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface AlbumDetailsFeature : Feature<AlbumDetailsArguments, AlbumDetailsProps> {
    object Key : Feature.Key
}

fun FeatureRegistry.albumDetails(): Feature<AlbumDetailsArguments, AlbumDetailsProps> =
    featureByKey(AlbumDetailsFeature.Key)
