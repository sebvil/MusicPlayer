package com.sebastianvm.musicplayer.ui

import com.google.common.truth.Truth
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.playback.FakePlaybackManagerImpl
import com.sebastianvm.musicplayer.repository.playback.NotPlayingState
import com.sebastianvm.musicplayer.repository.playback.TrackPlayingState
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import com.sebastianvm.musicplayer.ui.icons.Album
import com.sebastianvm.musicplayer.ui.icons.Icons
import com.sebastianvm.musicplayer.ui.player.PlaybackIcon
import com.sebastianvm.musicplayer.ui.player.PlayerViewState
import com.sebastianvm.musicplayer.ui.player.TrackInfoState
import com.sebastianvm.musicplayer.ui.player.TrackProgressState
import com.sebastianvm.musicplayer.ui.util.CloseableCoroutineScope
import com.sebastianvm.musicplayer.util.BaseTest
import com.sebastianvm.musicplayer.util.FakeProvider
import com.sebastianvm.musicplayer.util.currentState
import com.sebastianvm.musicplayer.util.runSafeTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import kotlin.time.Duration.Companion.seconds

class MainViewModelTest : BaseTest() {

    private lateinit var playbackManager: FakePlaybackManagerImpl

    @BeforeEach
    fun beforeEach() {
        playbackManager = FakeProvider.playbackManager
    }

    private fun generateViewModel(): MainViewModel {
        return MainViewModel(
            stateHolderScope = CloseableCoroutineScope(testScope.coroutineContext),
            playbackManager = playbackManager
        )
    }

    @ParameterizedTest
    @MethodSource("com.sebastianvm.musicplayer.util.FixtureProvider#playbackStateFixtures")
    fun `init sets state correctly and subscribes to changes`(playbackState: TrackPlayingState) =
        testScope.runSafeTest {
            with(generateViewModel()) {
                playbackManager.getPlaybackStateValue.emit(NotPlayingState)
                Truth.assertThat(currentState).isEqualTo(MainState(playerViewState = null))
                playbackManager.getPlaybackStateValue.emit(playbackState)
                Truth.assertThat(currentState).isEqualTo(
                    MainState(
                        playerViewState = PlayerViewState(
                            mediaArtImageState = MediaArtImageState(
                                imageUri = playbackState.trackInfo.artworkUri,
                                backupImage = Icons.Album
                            ),
                            trackInfoState = TrackInfoState(
                                trackName = playbackState.trackInfo.title,
                                artists = playbackState.trackInfo.artists
                            ),
                            trackProgressState = TrackProgressState(
                                currentPlaybackTime = playbackState.currentTrackProgress,
                                trackLength = playbackState.trackInfo.trackLength
                            ),
                            playbackIcon = if (playbackState.isPlaying) PlaybackIcon.PAUSE else PlaybackIcon.PLAY
                        )
                    )
                )
                playbackManager.getPlaybackStateValue.emit(NotPlayingState)
                Truth.assertThat(currentState).isEqualTo(MainState(playerViewState = null))
            }
        }

    @Test
    fun `ConnectToMusicService connects to service`() {
        with(generateViewModel()) {
            handle(MainUserAction.ConnectToMusicService)
            Truth.assertThat(playbackManager.connectToServiceInvocations)
                .containsExactly(listOf<Any>())
        }
    }

    @Test
    fun `DisconnectFromMusicService disconnects from service`() {
        with(generateViewModel()) {
            handle(MainUserAction.DisconnectFromMusicService)
            Truth.assertThat(playbackManager.disconnectFromServiceInvocations)
                .containsExactly(listOf<Any>())
        }
    }

    @Test
    fun `PlayToggled toggles play`() {
        with(generateViewModel()) {
            handle(MainUserAction.PlayToggled)
            Truth.assertThat(playbackManager.togglePlayInvocations)
                .containsExactly(listOf<Any>())
        }
    }

    @Test
    fun `NextButtonClicked seeks next song`() {
        with(generateViewModel()) {
            handle(MainUserAction.NextButtonClicked)
            Truth.assertThat(playbackManager.nextInvocations)
                .containsExactly(listOf<Any>())
        }
    }

    @Test
    fun `PreviousButtonClicked seeks prev song`() {
        with(generateViewModel()) {
            handle(MainUserAction.PreviousButtonClicked)
            Truth.assertThat(playbackManager.prevInvocations)
                .containsExactly(listOf<Any>())
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [10, 20, 30, 50])
    fun `ProgressBarClicked seeks to position in track when there is playback info`(position: Int) {
        with(generateViewModel()) {
            handle(MainUserAction.ProgressBarClicked(position, 100.seconds))
            Truth.assertThat(playbackManager.seekToTrackPositionInvocations)
                .containsExactly(listOf((position * 1_000).toLong()))
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [10, 20, 30, 50])
    fun `PlayMedia triggers playback`(trackIndex: Int) {
        with(generateViewModel()) {
            handle(
                MainUserAction.PlayMedia(
                    mediaGroup = MediaGroup.AllTracks,
                    initialTrackIndex = trackIndex
                )
            )
            Truth.assertThat(playbackManager.playMediaInvocations)
                .containsExactly(listOf(MediaGroup.AllTracks, trackIndex))
        }
    }
}
