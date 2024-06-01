package com.sebastianvm.musicplayer.ui

import com.sebastianvm.musicplayer.repository.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.ui.util.CloseableCoroutineScope
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.collections.shouldContainExactly

class MainViewModelTest : FreeSpec({

    lateinit var playbackManagerDep: FakePlaybackManager

    beforeTest {
        playbackManagerDep = FakePlaybackManager()
    }

    fun TestScope.getSubject(): MainViewModel {
        return MainViewModel(
            stateHolderScope = CloseableCoroutineScope(coroutineContext),
            playbackManager = playbackManagerDep
        )
    }

    "ConnectToMusicService connects to service" {
        val subject = getSubject()
        subject.handle(MainUserAction.ConnectToMusicService)
        playbackManagerDep.connectToServiceInvocations shouldContainExactly listOf(
            FakePlaybackManager.ConnectToServiceInvocations
        )
    }

    "DisconnectFromMusicService disconnects from service" {
        val subject = getSubject()
        subject.handle(MainUserAction.DisconnectFromMusicService)
        playbackManagerDep.disconnectFromServiceInvocations shouldContainExactly listOf(
            FakePlaybackManager.DisconnectFromServiceInvocations
        )
    }
})
