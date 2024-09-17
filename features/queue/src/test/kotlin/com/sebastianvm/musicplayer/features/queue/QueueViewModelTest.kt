package com.sebastianvm.musicplayer.features.queue

import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.commontest.extensions.awaitItemAs
import com.sebastianvm.musicplayer.core.commontest.extensions.testViewModelState
import com.sebastianvm.musicplayer.core.data.UriUtils
import com.sebastianvm.musicplayer.core.datatest.queue.FakeQueueRepository
import com.sebastianvm.musicplayer.core.model.NowPlayingInfo
import com.sebastianvm.musicplayer.core.model.QueuedTrack
import com.sebastianvm.musicplayer.core.servicestest.playback.FakePlaybackManager
import io.kotest.core.coroutines.backgroundScope
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalCoroutinesApi::class)
class QueueViewModelTest :
    FreeSpec({
        lateinit var queueRepositoryDep: FakeQueueRepository
        lateinit var playbackManagerDep: FakePlaybackManager

        beforeTest {
            queueRepositoryDep = FakeQueueRepository()
            playbackManagerDep = FakePlaybackManager()
        }

        fun TestScope.getSubject(
            tracks: List<QueuedTrack> = FixtureProvider.queueItemsFixtures(),
            nowPlayingInfo: NowPlayingInfo =
                NowPlayingInfo(nowPlayingPositionInQueue = 0, lastRecordedPosition = 0),
        ): QueueViewModel {
            // Ensure both sources are in sync
            playbackManagerDep.queuedTracks
                .onEach { queueRepositoryDep.queuedTracks.value = it }
                .launchIn(this.backgroundScope)

            playbackManagerDep.nowPlayingInfo
                .onEach { queueRepositoryDep.nowPlayingInfo.value = it }
                .launchIn(this.backgroundScope)
            playbackManagerDep.queuedTracks.value = tracks
            playbackManagerDep.nowPlayingInfo.value = nowPlayingInfo

            return QueueViewModel(
                queueRepository = queueRepositoryDep,
                playbackManager = playbackManagerDep,
                viewModelScope = this,
            )
        }

        "init subscribes to changes in queue" {
            val subject = getSubject(tracks = emptyList(), nowPlayingInfo = NowPlayingInfo())

            testViewModelState(subject) {
                awaitItem() shouldBe QueueState.Loading

                queueRepositoryDep.queuedTracks.value = FixtureProvider.queueItemsFixtures()
                queueRepositoryDep.nowPlayingInfo.value =
                    NowPlayingInfo(nowPlayingPositionInQueue = 0, lastRecordedPosition = 0)

                val queuedTracks = queueRepositoryDep.queuedTracks.first()
                awaitItem() shouldBe
                    QueueState.Data(
                        nowPlayingItem = queuedTracks.first().toQueueItem(),
                        queueItems =
                            queuedTracks.subList(1, queuedTracks.size).map { it.toQueueItem() },
                        nowPlayingItemArtworkUri =
                            UriUtils.getAlbumUriString(queuedTracks.first().track.albumId),
                    )

                queueRepositoryDep.nowPlayingInfo.value =
                    NowPlayingInfo(nowPlayingPositionInQueue = 1, lastRecordedPosition = 0)

                awaitItem() shouldBe
                    QueueState.Data(
                        nowPlayingItem = queuedTracks[1].toQueueItem(),
                        queueItems =
                            queuedTracks.subList(2, queuedTracks.size).map { it.toQueueItem() },
                        nowPlayingItemArtworkUri =
                            UriUtils.getAlbumUriString(queuedTracks.first().track.albumId),
                    )
            }
        }

        "handle" -
            {
                "DragEnded moves item" {
                    val tracks = FixtureProvider.queueItemsFixtures()
                    val subject = getSubject(tracks)

                    testViewModelState(subject) {
                        awaitItem() shouldBe QueueState.Loading

                        val queueItems = awaitItemAs<QueueState.Data>().queueItems
                        queueItems[0].trackRow.id shouldBe tracks[1].track.id
                        queueItems[1].trackRow.id shouldBe tracks[2].track.id

                        subject.handle(QueueUserAction.DragEnded(from = 2, to = 1))

                        val newQueueItems = awaitItemAs<QueueState.Data>().queueItems
                        newQueueItems[0].trackRow.id shouldBe tracks[2].track.id
                        newQueueItems[1].trackRow.id shouldBe tracks[1].track.id
                    }
                }

                "TrackClicked plays queue item" {
                    val queuedTracks = FixtureProvider.queueItemsFixtures()
                    val subject = getSubject(queuedTracks)
                    testViewModelState(subject) {
                        awaitItem() shouldBe QueueState.Loading
                        awaitItem() shouldBe
                            QueueState.Data(
                                nowPlayingItem = queuedTracks.first().toQueueItem(),
                                queueItems =
                                    queuedTracks.subList(1, queuedTracks.size).map {
                                        it.toQueueItem()
                                    },
                                nowPlayingItemArtworkUri =
                                    UriUtils.getAlbumUriString(queuedTracks.first().track.albumId),
                            )

                        subject.handle(QueueUserAction.TrackClicked(trackIndex = 1))
                        awaitItem() shouldBe
                            QueueState.Data(
                                nowPlayingItem = queuedTracks[1].toQueueItem(),
                                queueItems =
                                    queuedTracks.subList(2, queuedTracks.size).map {
                                        it.toQueueItem()
                                    },
                                nowPlayingItemArtworkUri =
                                    UriUtils.getAlbumUriString(queuedTracks.first().track.albumId),
                            )
                    }
                }

                "RemoveItemsFromQueue removes items from queue" {
                    val subject = getSubject()
                    testViewModelState(subject) {
                        skipItems(2)
                        val queuedTracks = queueRepositoryDep.queuedTracks.first()
                        subject.handle(QueueUserAction.RemoveItemsFromQueue(listOf(1, 2)))
                        awaitItem() shouldBe
                            QueueState.Data(
                                nowPlayingItem = queuedTracks[0].toQueueItem(),
                                queueItems =
                                    queuedTracks.subList(3, queuedTracks.size).map {
                                        it.toQueueItem().copy(position = it.queuePosition - 2)
                                    },
                                nowPlayingItemArtworkUri =
                                    UriUtils.getAlbumUriString(queuedTracks.first().track.albumId),
                            )
                    }
                }
            }
    })
