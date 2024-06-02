package com.sebastianvm.musicplayer.repository.queue

import com.sebastianvm.musicplayer.database.daos.MediaQueueDao
import com.sebastianvm.musicplayer.database.entities.MediaQueueItem
import com.sebastianvm.musicplayer.datastore.NowPlayingInfoDataSource
import com.sebastianvm.musicplayer.model.NowPlayingInfo
import com.sebastianvm.musicplayer.model.Queue
import com.sebastianvm.musicplayer.util.extensions.indexOfFirstOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class AppQueueRepository(
    private val nowPlayingInfoDataSource: NowPlayingInfoDataSource,
    private val mediaQueueDao: MediaQueueDao
) : QueueRepository {
    override fun getQueue(): Flow<Queue> {
        return combine(
            nowPlayingInfoDataSource.getNowPlayingInfo(),
            mediaQueueDao.getQueuedTracks()
        ) { nowPlayingInfo, queuedTracks ->
            val nowPlayingTrackIndex =
                queuedTracks.indexOfFirstOrNull { it.queuePosition == nowPlayingInfo.nowPlayingPositionInQueue }
                    ?: error("Cannot find now playing item")
            Queue(
                queuedTracks[nowPlayingTrackIndex],
                queuedTracks.subList(
                    fromIndex = nowPlayingTrackIndex + 1,
                    toIndex = queuedTracks.lastIndex
                )
            )
        }
    }

    override suspend fun saveQueue(nowPlayingInfo: NowPlayingInfo, queuedTracksIds: List<Long>) {
        nowPlayingInfoDataSource.setNowPlayingInfo(nowPlayingInfo)
        mediaQueueDao.saveQueue(
            queuedTracksIds.mapIndexed { index, id ->
                MediaQueueItem(trackId = id, position = index)
            }
        )
    }
}