package com.sebastianvm.musicplayer.features.api.playlist.details

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface PlaylistDetailsFeature : Feature {
    fun playlistDetailsUiComponent(
        arguments: PlaylistDetailsArguments,
        navController: NavController,
    ): MvvmComponent<*, *, *>

    object Key : Feature.Key
}

fun FeatureRegistry.playlistDetails(): PlaylistDetailsFeature =
    featureByKey(PlaylistDetailsFeature.Key)
