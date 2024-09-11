package com.sebastianvm.musicplayer.features.api.album.list

import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface AlbumListFeature : Feature<AlbumListArguments, AlbumListProps> {
    object Key : Feature.Key
}

fun FeatureRegistry.albumList(): Feature<AlbumListArguments, AlbumListProps> =
    featureByKey(AlbumListFeature.Key)
