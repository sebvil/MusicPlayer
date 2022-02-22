package com.sebastianvm.musicplayer.ui.player

import com.sebastianvm.musicplayer.repository.playback.FakeMediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MusicPlayerViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()

    private lateinit var mediaPlaybackRepository: MediaPlaybackRepository

    @Before
    fun setUp() {
        mediaPlaybackRepository = spyk(FakeMediaPlaybackRepository())
    }

    private fun generateViewModel(): MusicPlayerViewModel {
        return MusicPlayerViewModel(
            mediaPlaybackRepository = mediaPlaybackRepository,
            initialState = MusicPlayerState(
                isPlaying = false,
                trackName = null,
                artists = null,
                trackLengthMs = null,
                currentPlaybackTimeMs = null,
                trackArt = "",
                events = null
            )
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init sets initial state`() = runTest {
        with(generateViewModel()) {
            delay(1)
            assertEquals(TRACK_NAME, state.value.trackName)
            assertEquals(ARTIST_NAME, state.value.artists)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `state changes when nowPlaying changes`() = runTest {
        with(generateViewModel()) {
            this@runTest.launch {
                with(state.drop(2).first()) {
                    assertEquals(TRACK_NAME, trackName)
                    assertEquals(ARTIST_NAME, artists)
                    assertEquals(TRACK_LENGTH, trackLengthMs)
                }
            }
            delay(1)
        }
    }

//    @Test
//    fun `TogglePlay while playing triggers pause`() {
//        whenever(musicServiceConnection.playbackState) doReturn MutableStateFlow(
//            PlaybackStateCompat.Builder().setState(
//                PlaybackStateCompat.STATE_PLAYING, 0, 1f
//            ).build()
//        )
//        with(generateViewModel()) {
//            handle(MusicPlayerUserAction.TogglePlay)
//            verify(musicServiceConnection.transportControls).pause()
//        }
//    }
//
//    @Test
//    fun `TogglePlay while paused triggers play`() {
//        whenever(musicServiceConnection.playbackState) doReturn MutableStateFlow(
//            PlaybackStateCompat.Builder().setState(
//                PlaybackStateCompat.STATE_PAUSED, 0, 1f
//            ).setActions(PlaybackStateCompat.ACTION_PLAY).build()
//        )
//        with(generateViewModel()) {
//            handle(MusicPlayerUserAction.TogglePlay)
//            verify(musicServiceConnection.transportControls).play()
//        }
//    }
//
//    @Test
//    fun `PreviousTapped triggers skipToPrevious`() {
//        with(generateViewModel()) {
//            handle(MusicPlayerUserAction.PreviousTapped)
//            verify(musicServiceConnection.transportControls).skipToPrevious()
//        }
//    }
//
//    @Test
//    fun `NextTapped triggers skipToNext`() {
//        with(generateViewModel()) {
//            handle(MusicPlayerUserAction.NextTapped)
//            verify(musicServiceConnection.transportControls).skipToNext()
//        }
//    }

    companion object {
        private const val TRACK_NAME = "TRACK_NAME"
        private const val ARTIST_NAME = "ARTIST_NAME"
        private const val TRACK_LENGTH = 300000L
    }


}
