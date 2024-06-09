package com.sebastianvm.musicplayer.repository.queue

import com.sebastianvm.musicplayer.database.entities.MediaQueueItem
import com.sebastianvm.musicplayer.database.entities.QueuedTrack
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.model.FullQueue
import com.sebastianvm.musicplayer.model.NextUpQueue
import com.sebastianvm.musicplayer.model.NowPlayingInfo
import com.sebastianvm.musicplayer.util.FixtureProvider.queueItemsFixtures
import com.sebastianvm.musicplayer.util.FixtureProvider.trackFixtures
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

class FakeQueueRepository : QueueRepository {

    val tracks: MutableStateFlow<List<Track>> = MutableStateFlow(trackFixtures())
    val mediaQueueItems: MutableStateFlow<List<MediaQueueItem>> =
        MutableStateFlow(queueItemsFixtures())

    val queuedTracks =
        combine(tracks, mediaQueueItems) { tracks, mediaQueueItems ->
            mediaQueueItems.map { queueItem ->
                val track = tracks.first { it.id == queueItem.trackId }
                QueuedTrack(
                    id = track.id,
                    trackName = track.trackName,
                    trackNumber = track.trackNumber,
                    trackDurationMs = track.trackDurationMs,
                    albumName = track.albumName,
                    albumId = track.albumId,
                    artists = track.artists,
                    path = track.path,
                    queuePosition = queueItem.queuePosition,
                    queueItemId = queueItem.queueItemId,
                )
            }
        }
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
        mediaQueueItems.update {
            queuedTracksIds.map { item ->
                MediaQueueItem(
                    trackId = item.id,
                    queuePosition = item.queuePosition,
                    queueItemId = item.queueItemId,
                )
            }
        }
    }

    override fun moveQueueItem(from: Int, to: Int) {
        mediaQueueItems.update {
            it.toMutableList()
                .apply { add(to, removeAt(from)) }
                .mapIndexed { index, item -> item.copy(queuePosition = index) }
        }
    }

    override suspend fun addToQueue(trackId: Long) {
        mediaQueueItems.update {
            it + MediaQueueItem(trackId = trackId, queuePosition = it.size, queueItemId = trackId)
        }
    }

    override fun playQueueItem(index: Int) {
        nowPlayingInfo.value =
            NowPlayingInfo(nowPlayingPositionInQueue = index, lastRecordedPosition = 0)
    }
}
