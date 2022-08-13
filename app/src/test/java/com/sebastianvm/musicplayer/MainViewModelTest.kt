package com.sebastianvm.musicplayer

import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class MainViewModelTest {

    @MockK
    private lateinit var playbackManager: PlaybackManager

    @Before
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    @Test
    fun `connectToMusicService connects to music service`() {
        with(MainViewModel(MainActivityState, playbackManager)) {
            handle(MainActivityUserAction.ConnectToMusicService)
            verify {
                playbackManager.connectToService()
            }
        }
        confirmVerified(playbackManager)
    }

    @Test
    fun `disconnectFromMusicService disconnects from music service`() {
        with(MainViewModel(MainActivityState, playbackManager)) {
            handle(MainActivityUserAction.DisconnectFromMusicService)
            verify {
                playbackManager.disconnectFromService()
            }
        }
        confirmVerified(playbackManager)
    }
}