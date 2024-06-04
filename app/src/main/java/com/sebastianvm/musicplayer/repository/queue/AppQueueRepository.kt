package com.sebastianvm.musicplayer.repository.queue

import com.sebastianvm.musicplayer.database.daos.MediaQueueDao
import com.sebastianvm.musicplayer.database.entities.MediaQueueItem
import com.sebastianvm.musicplayer.datastore.NowPlayingInfoDataSource
import com.sebastianvm.musicplayer.model.FullQueue
import com.sebastianvm.musicplayer.model.NextUpQueue
import com.sebastianvm.musicplayer.model.NowPlayingInfo
import com.sebastianvm.musicplayer.player.MediaPlaybackClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

class AppQueueRepository(
    private val nowPlayingInfoDataSource: NowPlayingInfoDataSource,
    private val mediaQueueDao: MediaQueueDao,
    private val mediaPlaybackClient: MediaPlaybackClient,
    private val ioDispatcher: CoroutineDispatcher
) : QueueRepository {
    override fun getQueue(): Flow<NextUpQueue?> {
        return combine(
            nowPlayingInfoDataSource.getNowPlayingInfo(),
            mediaQueueDao.getQueuedTracks()
        ) { nowPlayingInfo, queuedTracks ->
            val nowPlayingTrackIndex =
                nowPlayingInfo.nowPlayingPositionInQueue.takeUnless { it == -1 }
                    ?: return@combine null
            NextUpQueue(
                nowPlayingTrack = queuedTracks[nowPlayingTrackIndex],
                nextUp = queuedTracks.subList(
                    fromIndex = nowPlayingTrackIndex + 1,
                    toIndex = queuedTracks.lastIndex
                )
            )
        }
    }

    override fun getFullQueue(): Flow<FullQueue?> {
        return combine(
            nowPlayingInfoDataSource.getNowPlayingInfo(),
            mediaQueueDao.getQueuedTracks()
        ) { nowPlayingInfo, queuedTracks ->
            nowPlayingInfo.nowPlayingPositionInQueue.takeUnless { it == -1 } ?: return@combine null
            FullQueue(
                nowPlayingInfo = nowPlayingInfo,
                queue = queuedTracks
            )
        }
    }

    override suspend fun saveQueue(nowPlayingInfo: NowPlayingInfo, queuedTracksIds: List<Long>) {
        nowPlayingInfoDataSource.setNowPlayingInfo(nowPlayingInfo)
        withContext(ioDispatcher) {
            mediaQueueDao.saveQueue(
                queuedTracksIds.mapIndexed { index, id ->
                    MediaQueueItem(trackId = id, queuePosition = index)
                }
            )
        }
    }

    override fun moveQueueItem(from: Int, to: Int) {
        mediaPlaybackClient.moveQueueItem(from, to)
    }
}