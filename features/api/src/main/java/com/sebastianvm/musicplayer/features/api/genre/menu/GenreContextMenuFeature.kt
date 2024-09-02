package com.sebastianvm.musicplayer.features.api.genre.menu

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface GenreContextMenuFeature : Feature {
    fun genreContextMenuUiComponent(arguments: GenreContextMenuArguments): MvvmComponent

    object Key : Feature.Key
}

fun FeatureRegistry.genreContextMenu(): GenreContextMenuFeature =
    featureByKey(GenreContextMenuFeature.Key)
