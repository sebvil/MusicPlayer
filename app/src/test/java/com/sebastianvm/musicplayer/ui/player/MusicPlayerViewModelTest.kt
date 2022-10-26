package com.sebastianvm.musicplayer.ui.player

import com.sebastianvm.musicplayer.database.entities.C
import com.sebastianvm.musicplayer.repository.playback.MediaItemMetadata
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackState
import com.sebastianvm.musicplayer.util.BaseTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MusicPlayerViewModelTest : BaseTest() {
    private lateinit var playbackManager: PlaybackManager
    private val playbackState: MutableStateFlow<PlaybackState> = MutableStateFlow(
        PlaybackState(
            mediaItemMetadata = MediaItemMetadata(
                title = C.TRACK_ARGENTINA,
                artists = C.ARTIST_ANA,
                trackDurationMs = TRACK_DURATION,
                artworkUri = C.IMAGE_URI_1
            ),
            currentPlayTimeMs = 0,
            isPlaying = false
        )
    )


    @Before
    fun setUp() {
        playbackManager = mockk(relaxUnitFun = true) {
            every { this@mockk.playbackState } returns this@MusicPlayerViewModelTest.playbackState
        }
    }

    private fun generateViewModel(isPlaying: Boolean = false): MusicPlayerViewModel {
        playbackState.update { it.copy(isPlaying = isPlaying) }
        return MusicPlayerViewModel(
            playbackManager = playbackManager,
            initialState = MusicPlayerState(
                isPlaying = isPlaying,
                trackName = null,
                artists = null,
                trackLengthMs = null,
                currentPlaybackTimeMs = null,
                trackArt = "",
            )
        )
    }

    @Test
    fun `init sets initial state and listens to change in playback state`() =
        testScope.runReliableTest {
            with(generateViewModel()) {
                assertEquals(
                    MusicPlayerState(
                        trackName = C.TRACK_ARGENTINA,
                        artists = C.ARTIST_ANA,
                        trackArt = C.IMAGE_URI_1,
                        trackLengthMs = TRACK_DURATION,
                        isPlaying = false,
                        currentPlaybackTimeMs = 0,
                    ),
                    state
                )
                playbackState.value = playbackState.value.copy(isPlaying = true)
                assertEquals(
                    MusicPlayerState(
                        trackName = C.TRACK_ARGENTINA,
                        artists = C.ARTIST_ANA,
                        trackArt = C.IMAGE_URI_1,
                        trackLengthMs = TRACK_DURATION,
                        isPlaying = true,
                        currentPlaybackTimeMs = 0,
                    ),
                    state
                )
            }
        }


    @Test
    fun `PlayToggled while paused triggers playback`() {
        with(generateViewModel()) {
            handle(MusicPlayerUserAction.PlayToggled)
            verify { playbackManager.play() }
        }
    }

    @Test
    fun `PlayToggled while playing pauses playback`() {
        with(generateViewModel(isPlaying = true)) {
            handle(MusicPlayerUserAction.PlayToggled)
            verify { playbackManager.pause() }
        }
    }


    @Test
    fun `PreviousButtonClicked triggers prev call`() {
        with(generateViewModel()) {
            handle(MusicPlayerUserAction.PreviousButtonClicked)
            verify { playbackManager.prev() }
        }
    }

    @Test
    fun `NextButtonClicked triggers next call`() {
        with(generateViewModel()) {
            handle(MusicPlayerUserAction.NextButtonClicked)
            verify { playbackManager.next() }
        }
    }

    @Test
    fun `onProgressTapped triggers seekToTrackPosition call`() = testScope.runReliableTest {
        with(generateViewModel()) {
            handle(MusicPlayerUserAction.ProgressBarClicked(50))
            verify { playbackManager.seekToTrackPosition(TRACK_DURATION / 2) }
        }
    }

    companion object {
        private const val TRACK_DURATION = 180000L
    }


}
