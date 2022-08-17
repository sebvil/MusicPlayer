//package com.sebastianvm.musicplayer.ui.player
//
//import android.net.Uri
//import com.sebastianvm.musicplayer.repository.playback.FakePlaybackManager
//import com.sebastianvm.musicplayer.repository.playback.MediaItemMetadata
//import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
//import com.sebastianvm.musicplayer.repository.playback.PlaybackState
//import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
//import io.mockk.spyk
//import io.mockk.verify
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.runTest
//import org.junit.Assert.assertEquals
//import org.junit.Rule
//import org.junit.Test
//
//class MusicPlayerViewModelTest : BaseTest() {
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @get:Rule
//    val dispatcherSetUpRule = DispatcherSetUpRule()
//
//    private lateinit var playbackManager: PlaybackManager
//
//
//    private fun generateViewModel(isPlaying: Boolean = false): MusicPlayerViewModel {
//        playbackManager = spyk(
//            FakePlaybackManager(
//                playbackState = PlaybackState(
//                    mediaItemMetadata = MediaItemMetadata(
//                        title = TRACK_NAME,
//                        artists = ARTIST_NAME,
//                        artworkUri = ARTWORK_URI,
//                        trackDurationMs = TRACK_LENGTH
//                    ),
//                    isPlaying = isPlaying,
//                    currentPlayTimeMs = 0L
//                )
//            )
//        )
//        return MusicPlayerViewModel(
//            mediaPlaybackRepository = playbackManager,
//            initialState = MusicPlayerState(
//                isPlaying = false,
//                trackName = null,
//                artists = null,
//                trackLengthMs = null,
//                currentPlaybackTimeMs = null,
//                trackArt = Uri.EMPTY,
//            )
//        )
//    }
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun `init sets initial state`() = testScope.runReliableTest {
//        with(generateViewModel()) {
//            assertEquals(
//                MusicPlayerState(
//                    trackName = TRACK_NAME,
//                    artists = ARTIST_NAME,
//                    trackArt = ARTWORK_URI,
//                    trackLengthMs = TRACK_LENGTH,
//                    isPlaying = false,
//                    currentPlaybackTimeMs = 0,
//                    ), state.value
//            )
//        }
//    }
//
//
//    @Test
//    fun `onPlayToggled while paused triggers playback`() {
//        with(generateViewModel()) {
//            onPlayToggled()
//            verify { playbackManager.play() }
//        }
//    }
//
//    @Test
//    fun `onPlayToggled while playing pauses playback`() {
//        with(generateViewModel(isPlaying = true)) {
//            onPlayToggled()
//            verify { playbackManager.pause() }
//        }
//    }
//
//
//    @Test
//    fun `onPreviousTapped triggers prev call`() {
//        with(generateViewModel()) {
//            onPreviousTapped()
//            verify { playbackManager.prev() }
//        }
//    }
//
//    @Test
//    fun `onNextTapped triggers next call`() {
//        with(generateViewModel()) {
//            onNextTapped()
//            verify { playbackManager.next() }
//        }
//    }
//
//    @Test
//    fun `onProgressTapped triggers seekToTrackPosition call`() {
//        with(generateViewModel()) {
//            onProgressTapped(50)
//            verify { playbackManager.seekToTrackPosition(TRACK_LENGTH / 2) }
//        }
//    }
//
//    companion object {
//        private const val TRACK_NAME = "TRACK_NAME"
//        private const val ARTIST_NAME = "ARTIST_NAME"
//        private val ARTWORK_URI = Uri.EMPTY
//        private const val TRACK_LENGTH = 300000L
//    }
//
//
//}
