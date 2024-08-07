package com.sebastianvm.musicplayer.features.test.player

import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeUiComponent
import com.sebastianvm.musicplayer.features.api.player.PlayerDelegate
import com.sebastianvm.musicplayer.features.api.player.PlayerFeature
import kotlinx.coroutines.flow.Flow

class FakePlayerFeature : PlayerFeature {
    override fun playerUiComponent(
        props: Flow<com.sebastianvm.musicplayer.features.api.player.PlayerProps>,
        delegate: PlayerDelegate,
    ): UiComponent<*> {
        return FakeUiComponent(arguments = null, name = "Player")
    }
}
