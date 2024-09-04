package com.sebastianvm.musicplayer.features.api.playlist.details

import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface PlaylistDetailsFeature : Feature<PlaylistDetailsArguments, PlaylistDetailsProps> {
    object Key : Feature.Key
}

fun FeatureRegistry.playlistDetails(): Feature<PlaylistDetailsArguments, PlaylistDetailsProps> =
    featureByKey(PlaylistDetailsFeature.Key)
