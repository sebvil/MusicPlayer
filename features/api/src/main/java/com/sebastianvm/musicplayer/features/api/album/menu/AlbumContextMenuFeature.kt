package com.sebastianvm.musicplayer.features.api.album.menu

import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface AlbumContextMenuFeature : Feature<AlbumContextMenuArguments, AlbumContextMenuProps> {
    object Key : Feature.Key
}

fun FeatureRegistry.albumContextMenu(): Feature<AlbumContextMenuArguments, AlbumContextMenuProps> =
    featureByKey(AlbumContextMenuFeature.Key)
