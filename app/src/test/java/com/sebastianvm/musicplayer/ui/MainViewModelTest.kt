package com.sebastianvm.musicplayer.ui

import com.sebastianvm.musicplayer.repository.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.repository.playback.NotPlayingState
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import com.sebastianvm.musicplayer.ui.icons.Album
import com.sebastianvm.musicplayer.ui.icons.Icons
import com.sebastianvm.musicplayer.ui.player.PlaybackIcon
import com.sebastianvm.musicplayer.ui.player.PlayerViewState
import com.sebastianvm.musicplayer.ui.player.TrackInfoState
import com.sebastianvm.musicplayer.ui.player.TrackProgressState
import com.sebastianvm.musicplayer.ui.util.CloseableCoroutineScope
import com.sebastianvm.musicplayer.util.FixtureProvider
import com.sebastianvm.musicplayer.util.testStateHolderState
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.seconds

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

    "init sets state correctly and subscribes to changes" - {
        withData(FixtureProvider.playbackStateFixtures().toList()) { playbackState ->
            val subject = getSubject()
            testStateHolderState(subject) {
                playbackManagerDep.getPlaybackStateValue.emit(NotPlayingState)
                awaitItem().playerViewState shouldBe null
                playbackManagerDep.getPlaybackStateValue.emit(playbackState)
                awaitItem().playerViewState shouldBe PlayerViewState(
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

                playbackManagerDep.getPlaybackStateValue.emit(NotPlayingState)
                awaitItem().playerViewState shouldBe null
            }
        }
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

    "PlayToggled toggles play" {
        val subject = getSubject()
        subject.handle(MainUserAction.PlayToggled)
        playbackManagerDep.togglePlayInvocations shouldContainExactly listOf(emptyList())
    }

    "NextButtonClicked seeks next song" {
        val subject = getSubject()
        subject.handle(MainUserAction.NextButtonClicked)
        playbackManagerDep.nextInvocations shouldContainExactly listOf(emptyList())
    }

    "PreviousButtonClicked seeks prev song" {
        val subject = getSubject()
        subject.handle(MainUserAction.PreviousButtonClicked)
        playbackManagerDep.prevInvocations shouldContainExactly listOf(emptyList())
    }

    "ProgressBarClicked seeks to position in track when there is playback info" {
        val subject = getSubject()
        subject.handle(MainUserAction.ProgressBarClicked(10, 100.seconds))
        playbackManagerDep.seekToTrackPositionInvocations shouldContainExactly listOf(listOf((10 * 1_000).toLong()))
    }
})
