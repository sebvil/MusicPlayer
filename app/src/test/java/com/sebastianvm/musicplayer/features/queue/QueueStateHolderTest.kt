package com.sebastianvm.musicplayer.features.queue

import com.sebastianvm.musicplayer.model.NowPlayingInfo
import com.sebastianvm.musicplayer.repository.queue.FakeQueueRepository
import com.sebastianvm.musicplayer.util.FixtureProvider
import com.sebastianvm.musicplayer.util.awaitItemAs
import com.sebastianvm.musicplayer.util.testStateHolderState
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class QueueStateHolderTest :
    FreeSpec({
        lateinit var queueRepositoryDep: FakeQueueRepository

        beforeTest { queueRepositoryDep = FakeQueueRepository() }

        fun TestScope.getSubject(): QueueStateHolder {
            return QueueStateHolder(queueRepository = queueRepositoryDep, stateHolderScope = this)
        }

        "init subscribes to changes in queue" {
            val subject = getSubject()

            // Simulate queue not playing
            queueRepositoryDep.queuedTracks.value = emptyList()
            queueRepositoryDep.nowPlayingInfo.value = NowPlayingInfo()

            testStateHolderState(subject) {
                awaitItem() shouldBe QueueState.Loading
                awaitItem() shouldBe QueueState.Empty

                queueRepositoryDep.queuedTracks.value = FixtureProvider.queueItemsFixtures()
                queueRepositoryDep.nowPlayingInfo.value =
                    NowPlayingInfo(nowPlayingPositionInQueue = 0, lastRecordedPosition = 0)

                val queuedTracks = queueRepositoryDep.queuedTracks.first()
                awaitItem() shouldBe
                    QueueState.Data(
                        nowPlayingItem = queuedTracks.first().toQueueItem(),
                        queueItems =
                            queuedTracks.subList(1, queuedTracks.size).map { it.toQueueItem() },
                    )

                queueRepositoryDep.nowPlayingInfo.value =
                    NowPlayingInfo(nowPlayingPositionInQueue = 1, lastRecordedPosition = 0)

                awaitItem() shouldBe
                    QueueState.Data(
                        nowPlayingItem = queuedTracks[1].toQueueItem(),
                        queueItems =
                            queuedTracks.subList(2, queuedTracks.size).map { it.toQueueItem() },
                    )

                queueRepositoryDep.nowPlayingInfo.value = NowPlayingInfo()
                awaitItem() shouldBe QueueState.Empty
            }
        }

        "handle" -
            {
                "DragEnded moves item" {
                    val subject = getSubject()
                    val tracks = queueRepositoryDep.queuedTracks.value

                    testStateHolderState(subject) {
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
                    val subject = getSubject()
                    testStateHolderState(subject) {
                        awaitItem() shouldBe QueueState.Loading
                        val queuedTracks = queueRepositoryDep.queuedTracks.first()
                        awaitItem() shouldBe
                            QueueState.Data(
                                nowPlayingItem = queuedTracks.first().toQueueItem(),
                                queueItems =
                                    queuedTracks.subList(1, queuedTracks.size).map {
                                        it.toQueueItem()
                                    },
                            )

                        subject.handle(QueueUserAction.TrackClicked(trackIndex = 1))
                        awaitItem() shouldBe
                            QueueState.Data(
                                nowPlayingItem = queuedTracks[1].toQueueItem(),
                                queueItems =
                                    queuedTracks.subList(2, queuedTracks.size).map {
                                        it.toQueueItem()
                                    },
                            )
                    }
                }

                "RemoveItemsFromQueue removes items from queue" {
                    val subject = getSubject()
                    testStateHolderState(subject) {
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
                            )
                    }
                }
            }
    })
