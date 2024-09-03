package com.sebastianvm.musicplayer.features.player

import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.features.api.player.PlayerDelegate
import com.sebastianvm.musicplayer.features.api.player.PlayerFeature
import com.sebastianvm.musicplayer.features.api.player.PlayerProps
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import kotlinx.coroutines.flow.Flow

class DefaultPlayerFeature(
    private val playbackManager: PlaybackManager,
    private val features: FeatureRegistry,
) : PlayerFeature {
    override fun playerUiComponent(
        props: Flow<PlayerProps>,
        delegate: PlayerDelegate,
    ): MvvmComponent<*, *, *> {
        return PlayerMvvmComponent(
            delegate = delegate,
            props = props,
            playbackManager = playbackManager,
            features = features,
        )
    }
}
