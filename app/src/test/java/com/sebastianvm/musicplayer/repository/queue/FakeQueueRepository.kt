package com.sebastianvm.musicplayer.repository.queue

import com.sebastianvm.musicplayer.model.FullQueue
import com.sebastianvm.musicplayer.model.NextUpQueue
import com.sebastianvm.musicplayer.model.NowPlayingInfo
import com.sebastianvm.musicplayer.model.QueuedTrack
import com.sebastianvm.musicplayer.util.FixtureProvider.queueItemsFixtures
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

class FakeQueueRepository : QueueRepository {

    val queuedTracks = MutableStateFlow(queueItemsFixtures())
    val nowPlayingInfo: MutableStateFlow<NowPlayingInfo> =
        MutableStateFlow(NowPlayingInfo(nowPlayingPositionInQueue = 0, lastRecordedPosition = 0))

    override fun getQueue(): Flow<NextUpQueue?> {
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
    }

    override fun getFullQueue(): Flow<FullQueue?> {
        return combine(nowPlayingInfo, queuedTracks) { nowPlayingInfo, queuedTracks ->
            nowPlayingInfo.nowPlayingPositionInQueue.takeUnless { it == -1 } ?: return@combine null
            FullQueue(nowPlayingInfo = nowPlayingInfo, queue = queuedTracks)
        }
    }

    override suspend fun saveQueue(
        nowPlayingInfo: NowPlayingInfo,
        queuedTracksIds: List<QueuedTrack>,
    ) {
        this.nowPlayingInfo.update { nowPlayingInfo }
        queuedTracks.update { queuedTracksIds }
    }

    override fun moveQueueItem(from: Int, to: Int) {
        queuedTracks.update {
            it.toMutableList()
                .apply { add(to, removeAt(from)) }
                .mapIndexed { index, item -> item.copy(queuePosition = index) }
        }
    }

    override suspend fun addToQueue(trackId: Long) {
        queuedTracks.update {
            it +
                QueuedTrack(
                    id = trackId,
                    artists = "",
                    trackName = "",
                    queuePosition = it.size,
                    queueItemId = trackId,
                )
        }
    }

    override fun playQueueItem(index: Int) {
        nowPlayingInfo.value =
            NowPlayingInfo(nowPlayingPositionInQueue = index, lastRecordedPosition = 0)
    }
}
