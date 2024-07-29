package com.sebastianvm.musicplayer.features.api.genre.menu

import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface GenreContextMenuFeature : Feature {
    fun genreContextMenuUiComponent(arguments: GenreContextMenuArguments): UiComponent<*>

    object Key : Feature.Key
}

fun FeatureRegistry.genreContextMenu(): GenreContextMenuFeature =
    featureByKey(GenreContextMenuFeature.Key)
