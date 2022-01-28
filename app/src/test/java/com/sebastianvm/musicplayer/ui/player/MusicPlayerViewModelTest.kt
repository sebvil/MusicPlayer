package com.sebastianvm.musicplayer.ui.player

import android.net.Uri
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
import org.junit.Assert.assertTrue
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
                trackId = null,
                albumId = null,
                trackArt = Uri.EMPTY
            )
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `state changes when playbackState changes`() = runTest {
        with(generateViewModel()) {
            this@runTest.launch {
                assertTrue(state.drop(2).first().isPlaying)
            }
            delay(1)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `state changes when nowPlaying changes`() = runTest {
        with(generateViewModel()) {
            this@runTest.launch {
                with(state.drop(2).first()) {
                    assertEquals(TRACK_TITLE, trackName)
                    assertEquals(ARTISTS, artists)
                    assertEquals(TRACK_LENGTH, trackLengthMs)
                    assertEquals(TRACK_ID, trackId)
                    assertEquals(ALBUM_ID, albumId)
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
        private const val TRACK_TITLE = "TRACK_TITLE"
        private const val ARTISTS = "ARTISTS"
        private const val TRACK_LENGTH = 300000L
        private const val TRACK_ID = "11111"
        private const val ALBUM_ID = "22222"
    }


}
