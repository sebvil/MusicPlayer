package com.sebastianvm.musicplayer.features.api.artistsmenu

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface ArtistsMenuFeature : Feature {

    fun artisteMenuUiComponent(
        arguments: ArtistsMenuArguments,
        navController: NavController
    ): UiComponent<*>

    object Key : Feature.Key
}

fun FeatureRegistry.artisteMenu(): ArtistsMenuFeature = featureByKey(ArtistsMenuFeature.Key)
