package com.sebastianvm.musicplayer.features.api.playlist.list

import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface PlaylistListFeature : Feature<PlaylistListArguments, PlaylistListProps> {
    object Key : Feature.Key
}

fun FeatureRegistry.playlistList(): Feature<PlaylistListArguments, PlaylistListProps> =
    featureByKey(PlaylistListFeature.Key)
