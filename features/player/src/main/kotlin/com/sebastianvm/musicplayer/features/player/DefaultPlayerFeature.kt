package com.sebastianvm.musicplayer.features.player

import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.player.PlayerDelegate
import com.sebastianvm.musicplayer.features.api.player.PlayerFeature
import com.sebastianvm.musicplayer.features.api.player.PlayerProps
import kotlinx.coroutines.flow.Flow

class DefaultPlayerFeature : PlayerFeature {
    override fun playerUiComponent(
        props: Flow<PlayerProps>,
        delegate: PlayerDelegate,
    ): UiComponent<*> {
        return PlayerUiComponent(delegate, props)
    }
}
