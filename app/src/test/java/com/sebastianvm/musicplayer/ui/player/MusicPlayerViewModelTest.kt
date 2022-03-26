package com.sebastianvm.musicplayer.ui.player

import com.sebastianvm.musicplayer.repository.playback.FakeMediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.playback.MediaItemMetadata
import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackState
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MusicPlayerViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()

    private lateinit var mediaPlaybackRepository: MediaPlaybackRepository


    private fun generateViewModel(isPlaying: Boolean = false): MusicPlayerViewModel {
        mediaPlaybackRepository = spyk(
            FakeMediaPlaybackRepository(
                playbackState = PlaybackState(
                    mediaItemMetadata = MediaItemMetadata(
                        title = TRACK_NAME,
                        artists = ARTIST_NAME,
                        artworkUri = ARTWORK_URI,
                        trackDurationMs = TRACK_LENGTH
                    ),
                    isPlaying = isPlaying,
                    currentPlayTimeMs = 0L
                )
            )
        )
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
            assertEquals(
                MusicPlayerState(
                    trackName = TRACK_NAME,
                    artists = ARTIST_NAME,
                    trackArt = ARTWORK_URI,
                    trackLengthMs = TRACK_LENGTH,
                    isPlaying = false,
                    currentPlaybackTimeMs = 0,
                    events = listOf()
                ), state.value
            )
        }
    }


    @Test
    fun `onPlayToggled while paused triggers playback`() {
        with(generateViewModel()) {
            onPlayToggled()
            verify { mediaPlaybackRepository.play() }
        }
    }

    @Test
    fun `onPlayToggled while playing pauses playback`() {
        with(generateViewModel(isPlaying = true)) {
            onPlayToggled()
            verify { mediaPlaybackRepository.pause() }
        }
    }


    @Test
    fun `onPreviousTapped triggers prev call`() {
        with(generateViewModel()) {
            onPreviousTapped()
            verify { mediaPlaybackRepository.prev() }
        }
    }

    @Test
    fun `onNextTapped triggers next call`() {
        with(generateViewModel()) {
            onNextTapped()
            verify { mediaPlaybackRepository.next() }
        }
    }

    @Test
    fun `onProgressTapped triggers seekToTrackPosition call`() {
        with(generateViewModel()) {
            onProgressTapped(50)
            verify { mediaPlaybackRepository.seekToTrackPosition(TRACK_LENGTH / 2) }
        }
    }

    companion object {
        private const val TRACK_NAME = "TRACK_NAME"
        private const val ARTIST_NAME = "ARTIST_NAME"
        private const val ARTWORK_URI = "ARTWORK_URI"
        private const val TRACK_LENGTH = 300000L
    }


}
