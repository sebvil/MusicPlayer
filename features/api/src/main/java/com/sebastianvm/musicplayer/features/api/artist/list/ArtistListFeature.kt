package com.sebastianvm.musicplayer.features.api.artist.list

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface ArtistListFeature : Feature {
    fun artistListUiComponent(navController: NavController): MvvmComponent<*, *, *>

    object Key : Feature.Key
}

fun FeatureRegistry.artistList(): ArtistListFeature = featureByKey(ArtistListFeature.Key)
