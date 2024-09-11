package com.sebastianvm.musicplayer.features.api.genre.menu

import com.sebastianvm.musicplayer.core.ui.mvvm.NoProps
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

interface GenreContextMenuFeature : Feature<GenreContextMenuArguments, NoProps> {
    object Key : Feature.Key
}

fun FeatureRegistry.genreContextMenu(): Feature<GenreContextMenuArguments, NoProps> =
    featureByKey(GenreContextMenuFeature.Key)
