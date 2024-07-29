package com.sebastianvm.musicplayer.features.api.artist.list

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface ArtistListFeature : Feature {
    fun artistListUiComponent(navController: NavController): UiComponent<*>

    object Key : Feature.Key
}

fun FeatureRegistry.artistList(): ArtistListFeature = featureByKey(ArtistListFeature.Key)
