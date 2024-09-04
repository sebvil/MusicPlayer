package com.sebastianvm.musicplayer.features.player

import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.commontest.extensions.awaitItemAs
import com.sebastianvm.musicplayer.core.commontest.extensions.testViewModelState
import com.sebastianvm.musicplayer.core.model.NotPlayingState
import com.sebastianvm.musicplayer.core.servicestest.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.features.api.player.PlayerProps
import com.sebastianvm.musicplayer.features.test.initializeFakeFeatures
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class PlayerViewModelTest :
    FreeSpec({
        lateinit var playbackManagerDep: FakePlaybackManager
        lateinit var propsDep: MutableStateFlow<PlayerProps>

        beforeTest {
            playbackManagerDep = FakePlaybackManager()

            propsDep =
                MutableStateFlow(
                    PlayerProps(
                        isFullscreen = false,
                        dismissFullScreenPlayer = {
                            propsDep.update { it.copy(isFullscreen = false) }
                        },
                    )
                )
        }

        fun TestScope.getSubject(): PlayerViewModel {
            return PlayerViewModel(
                vmScope = this,
                playbackManager = playbackManagerDep,
                props = propsDep,
                features = initializeFakeFeatures(),
            )
        }

        "init" -
            {
                "sets state and subscribes to changes in playback state" -
                    {
                        withData(FixtureProvider.playbackStateFixtures().toList()) { playbackState
                            ->
                            val subject = getSubject()
                            testViewModelState(subject) {
                                awaitItem() shouldBe PlayerState.NotPlaying
                                playbackManagerDep.getPlaybackStateValue.value = playbackState
                                awaitItem() shouldBe
                                    PlayerState.FloatingState(
                                        artworkUri = playbackState.trackInfo.artworkUri,
                                        trackInfoState =
                                            TrackInfoState(
                                                trackName = playbackState.trackInfo.title,
                                                artists = playbackState.trackInfo.artists,
                                            ),
                                        trackProgressState =
                                            TrackProgressState(
                                                currentPlaybackTime =
                                                    playbackState.currentTrackProgress,
                                                trackLength = playbackState.trackInfo.trackLength,
                                            ),
                                        playbackIcon =
                                            if (playbackState.isPlaying) PlaybackIcon.PAUSE
                                            else PlaybackIcon.PLAY,
                                    )

                                playbackManagerDep.getPlaybackStateValue.value = NotPlayingState
                                awaitItem() shouldBe PlayerState.NotPlaying
                            }
                        }
                    }

                "sets state and subscribes to changes in props" {
                    val subject = getSubject()
                    playbackManagerDep.getPlaybackStateValue.value =
                        FixtureProvider.playbackStateFixtures().first()
                    testViewModelState(subject) {
                        awaitItem() shouldBe PlayerState.NotPlaying
                        awaitItemAs<PlayerState.FloatingState>()
                        propsDep.update { it.copy(isFullscreen = true) }
                        awaitItemAs<PlayerState.FullScreenState>()
                        propsDep.update { it.copy(isFullscreen = false) }
                        awaitItemAs<PlayerState.FloatingState>()
                    }
                }
            }

        "handle" -
            {
                "PlayToggled toggles play" {
                    val subject = getSubject()
                    subject.handle(PlayerUserAction.PlayToggled)
                    playbackManagerDep.togglePlayInvocations shouldContainExactly
                        listOf(emptyList())
                }

                "NextButtonClicked seeks next song" {
                    val subject = getSubject()
                    subject.handle(PlayerUserAction.NextButtonClicked)
                    playbackManagerDep.nextInvocations shouldContainExactly listOf(emptyList())
                }

                "PreviousButtonClicked seeks prev song" {
                    val subject = getSubject()
                    subject.handle(PlayerUserAction.PreviousButtonClicked)
                    playbackManagerDep.prevInvocations shouldContainExactly listOf(emptyList())
                }

                "ProgressBarClicked seeks to position in track when there is playback info" {
                    val subject = getSubject()
                    subject.handle(PlayerUserAction.ProgressBarClicked(10, 100.seconds))
                    playbackManagerDep.seekToTrackPositionInvocations shouldContainExactly
                        listOf(listOf((10 * 1_000).toLong()))
                }

                "DismissFullScreenPlayer dismisses fullscreen player" {
                    val subject = getSubject()
                    playbackManagerDep.getPlaybackStateValue.value =
                        FixtureProvider.playbackStateFixtures().first()

                    testViewModelState(subject) {
                        awaitItem() shouldBe PlayerState.NotPlaying
                        awaitItemAs<PlayerState.FloatingState>()

                        propsDep.update { it.copy(isFullscreen = true) }
                        awaitItemAs<PlayerState.FullScreenState>()

                        subject.handle(PlayerUserAction.DismissFullScreenPlayer)
                        awaitItemAs<PlayerState.FloatingState>()
                    }
                }

                "QueueTapped and DismissQueue toggle queue shown" {
                    val subject = getSubject()
                    playbackManagerDep.getPlaybackStateValue.value =
                        FixtureProvider.playbackStateFixtures().first()
                    propsDep.update { it.copy(isFullscreen = true) }

                    testViewModelState(subject) {
                        awaitItem() shouldBe PlayerState.NotPlaying
                        awaitItemAs<PlayerState.FullScreenState>()

                        subject.handle(PlayerUserAction.QueueTapped)
                        awaitItemAs<PlayerState.QueueState>()

                        subject.handle(PlayerUserAction.DismissQueue)
                        awaitItemAs<PlayerState.FullScreenState>()
                    }
                }
            }
    })
