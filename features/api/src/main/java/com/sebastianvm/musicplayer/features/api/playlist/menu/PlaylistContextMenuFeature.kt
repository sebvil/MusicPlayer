package com.sebastianvm.musicplayer.features.api.playlist.menu

import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface PlaylistContextMenuFeature : Feature {
    fun playlistContextMenuUiComponent(
        arguments: PlaylistContextMenuArguments,
        delegate: PlaylistContextMenuDelegate,
    ): UiComponent<*>

    object Key : Feature.Key
}

fun FeatureRegistry.playlistContextMenu(): PlaylistContextMenuFeature =
    featureByKey(PlaylistContextMenuFeature.Key)
