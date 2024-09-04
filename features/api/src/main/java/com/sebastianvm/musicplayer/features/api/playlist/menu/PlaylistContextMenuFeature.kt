package com.sebastianvm.musicplayer.features.api.playlist.menu

import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface PlaylistContextMenuFeature :
    Feature<PlaylistContextMenuArguments, PlaylistContextMenuProps> {
    object Key : Feature.Key
}

fun FeatureRegistry.playlistContextMenu():
    Feature<PlaylistContextMenuArguments, PlaylistContextMenuProps> =
    featureByKey(PlaylistContextMenuFeature.Key)
