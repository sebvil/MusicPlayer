package com.sebastianvm.musicplayer.ui.player

import com.sebastianvm.musicplayer.repository.playback.FakeMediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        mediaPlaybackRepository =
            spyk(FakeMediaPlaybackRepository(title = TRACK_NAME, artist = ARTIST_NAME))
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
                events = listOf()
            )
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init sets initial state`() = runTest {
        with(generateViewModel()) {
            assertEquals(TRACK_NAME, state.value.trackName)
            assertEquals(ARTIST_NAME, state.value.artists)
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
