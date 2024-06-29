package com.sebastianvm.musicplayer.repository.queue

import com.sebastianvm.model.BasicQueuedTrack
import com.sebastianvm.model.FullQueue
import com.sebastianvm.model.NextUpQueue
import com.sebastianvm.model.NowPlayingInfo
import com.sebastianvm.model.QueuedTrack
import com.sebastianvm.musicplayer.util.FixtureProvider
import com.sebastianvm.musicplayer.util.FixtureProvider.queueItemsFixtures
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update

class FakeQueueRepository : QueueRepository {

    val queuedTracks = MutableStateFlow(queueItemsFixtures())
    val nowPlayingInfo: MutableStateFlow<NowPlayingInfo> =
        MutableStateFlow(NowPlayingInfo(nowPlayingPositionInQueue = 0, lastRecordedPosition = 0))

    override fun getQueue(): Flow<NextUpQueue> {
        return combine(nowPlayingInfo, queuedTracks) { nowPlayingInfo, queuedTracks ->
                val nowPlayingTrackIndex =
                    nowPlayingInfo.nowPlayingPositionInQueue.takeUnless { it == -1 }
                        ?: return@combine null
                NextUpQueue(
                    nowPlayingTrack = queuedTracks[nowPlayingTrackIndex],
                    nextUp =
                        queuedTracks.subList(
                            fromIndex = nowPlayingTrackIndex + 1,
                            toIndex = queuedTracks.size,
                        ),
                )
            }
            .filterNotNull()
    }

    override fun getFullQueue(): Flow<FullQueue?> {
        return combine(nowPlayingInfo, queuedTracks) { nowPlayingInfo, queuedTracks ->
            nowPlayingInfo.nowPlayingPositionInQueue.takeUnless { it == -1 } ?: return@combine null
            FullQueue(nowPlayingInfo = nowPlayingInfo, queue = queuedTracks)
        }
    }

    override suspend fun saveQueue(
        nowPlayingInfo: NowPlayingInfo,
        queuedTracks: List<BasicQueuedTrack>,
    ) {
        this.nowPlayingInfo.update { nowPlayingInfo }
        this.queuedTracks.update {
            queuedTracks.map { queuedTrack ->
                val track = FixtureProvider.track(id = queuedTrack.trackId)
                QueuedTrack(
                    track = track,
                    queuePosition = queuedTrack.queuePosition,
                    queueItemId = queuedTrack.queueItemId,
                )
            }
        }
    }

    override fun moveQueueItem(from: Int, to: Int) {
        queuedTracks.update {
            it.toMutableList()
                .apply { add(to, removeAt(from)) }
                .mapIndexed { index, item -> item.copy(queuePosition = index) }
        }
    }

    override suspend fun addToQueue(trackId: Long) {
        val track = FixtureProvider.track(trackId)
        queuedTracks.update {
            it + QueuedTrack(track = track, queuePosition = it.size, queueItemId = trackId)
        }
    }

    override fun playQueueItem(index: Int) {
        nowPlayingInfo.value =
            NowPlayingInfo(nowPlayingPositionInQueue = index, lastRecordedPosition = 0)
    }

    override fun removeItemsFromQueue(queuePositions: List<Int>) {
        queuedTracks.update {
            it.filterIndexed { index, _ -> index !in queuePositions }
                .mapIndexed { index, queuedTrack -> queuedTrack.copy(queuePosition = index) }
        }
    }
}
