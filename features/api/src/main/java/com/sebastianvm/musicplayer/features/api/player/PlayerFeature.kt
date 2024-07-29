package com.sebastianvm.musicplayer.features.api.player

import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import kotlinx.coroutines.flow.Flow

interface PlayerFeature : Feature {
    fun playerUiComponent(props: Flow<PlayerProps>, delegate: PlayerDelegate): UiComponent<*>

    object Key : Feature.Key
}

fun FeatureRegistry.playerFeature(): PlayerFeature = featureByKey(PlayerFeature.Key)
