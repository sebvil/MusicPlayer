package com.sebastianvm.musicplayer.features.main

import com.sebastianvm.musicplayer.repository.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.ui.util.CloseableCoroutineScope
import com.sebastianvm.musicplayer.util.testStateHolderState
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

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

    "handle" - {
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

        "ExpandPlayer and CollapsePlayer toggle player expanded state" {
            val subject = getSubject()
            testStateHolderState(subject) {
                awaitItem().isFullscreen shouldBe false
                subject.handle(MainUserAction.ExpandPlayer)
                awaitItem().isFullscreen shouldBe true
                subject.handle(MainUserAction.CollapsePlayer)
                awaitItem().isFullscreen shouldBe false
            }
        }
    }
})
