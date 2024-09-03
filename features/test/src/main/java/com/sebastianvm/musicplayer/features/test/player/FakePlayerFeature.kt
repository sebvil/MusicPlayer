package com.sebastianvm.musicplayer.features.test.player

import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.api.player.PlayerDelegate
import com.sebastianvm.musicplayer.features.api.player.PlayerFeature
import kotlinx.coroutines.flow.Flow

class FakePlayerFeature : PlayerFeature {
    override fun playerUiComponent(
        props: Flow<com.sebastianvm.musicplayer.features.api.player.PlayerProps>,
        delegate: PlayerDelegate,
    ): MvvmComponent<*, *, *> {
        return FakeMvvmComponent(arguments = null, name = "Player")
    }
}
