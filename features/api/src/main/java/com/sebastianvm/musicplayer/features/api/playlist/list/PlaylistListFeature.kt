package com.sebastianvm.musicplayer.features.api.playlist.list

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface PlaylistListFeature : Feature {
    fun playlistListUiComponent(navController: NavController): MvvmComponent<*, *, *>

    object Key : Feature.Key
}

fun FeatureRegistry.playlistList(): PlaylistListFeature = featureByKey(PlaylistListFeature.Key)
