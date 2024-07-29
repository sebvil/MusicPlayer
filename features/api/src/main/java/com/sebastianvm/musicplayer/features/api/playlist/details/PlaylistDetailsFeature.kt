package com.sebastianvm.musicplayer.features.api.playlist.details

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface PlaylistDetailsFeature : Feature {
    fun playlistDetailsUiComponent(
        arguments: PlaylistDetailsArguments,
        navController: NavController,
    ): UiComponent<*>

    object Key : Feature.Key
}

fun FeatureRegistry.playlistDetails(): PlaylistDetailsFeature =
    featureByKey(PlaylistDetailsFeature.Key)
