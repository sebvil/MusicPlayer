package com.sebastianvm.musicplayer.features.api.playlist.list

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface PlaylistListFeature : Feature {
    fun playlistListUiComponent(navController: NavController): UiComponent<*>

    object Key : Feature.Key
}

fun FeatureRegistry.playlistList(): PlaylistListFeature = featureByKey(PlaylistListFeature.Key)
