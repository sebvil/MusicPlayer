package com.sebastianvm.musicplayer.core.playback.queue

import com.sebastianvm.musicplayer.core.data.track.asExternalModel
import com.sebastianvm.musicplayer.core.database.daos.MediaQueueDao
import com.sebastianvm.musicplayer.core.database.entities.QueueItemEntity
import com.sebastianvm.musicplayer.core.database.entities.QueueItemWithTrack
import com.sebastianvm.musicplayer.core.datastore.playinfo.NowPlayingInfoDataSource
import com.sebastianvm.musicplayer.core.datastore.playinfo.SavedPlaybackInfo
import com.sebastianvm.musicplayer.core.model.BasicQueuedTrack
import com.sebastianvm.musicplayer.core.model.FullQueue
import com.sebastianvm.musicplayer.core.model.NextUpQueue
import com.sebastianvm.musicplayer.core.model.NowPlayingInfo
import com.sebastianvm.musicplayer.core.model.QueuedTrack
<<<<<<<< HEAD:core/data/src/main/kotlin/com/sebastianvm/musicplayer/core/data/queue/AppQueueRepository.kt
========
import com.sebastianvm.musicplayer.core.model.Track
import com.sebastianvm.musicplayer.core.playback.extensions.toMediaItem
import com.sebastianvm.musicplayer.core.playback.player.MediaPlaybackClient
import kotlinx.coroutines.Dispatchers
>>>>>>>> 7be87a69 (progress):core/playback/src/main/java/com/sebastianvm/musicplayer/core/playback/queue/AppQueueRepository.kt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AppQueueRepository(
    private val nowPlayingInfoDataSource: NowPlayingInfoDataSource,
    private val mediaQueueDao: MediaQueueDao,
) : QueueRepository {

    override fun getQueue(): Flow<NextUpQueue> {
        return combine(nowPlayingInfoDataSource.getSavedPlaybackInfo(), getQueuedTracks()) {
                nowPlayingInfo,
                queuedTracks ->
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
        return combine(nowPlayingInfoDataSource.getSavedPlaybackInfo(), getQueuedTracks()) {
            savedPlaybackInfo,
            queuedTracks ->
            savedPlaybackInfo.nowPlayingPositionInQueue.takeUnless { it == -1 }
                ?: return@combine null
            FullQueue(nowPlayingInfo = savedPlaybackInfo.asNowPlayingInfo(), queue = queuedTracks)
        }
    }

    override suspend fun saveQueue(
        nowPlayingInfo: NowPlayingInfo,
        queuedTracks: List<BasicQueuedTrack>,
    ) {
        nowPlayingInfoDataSource.savePlaybackInfo(nowPlayingInfo.asSavedPlaybackInfo())
        mediaQueueDao.saveQueue(
            queuedTracks.map { item ->
                QueueItemEntity(
                    trackId = item.trackId,
                    queuePosition = item.queuePosition,
                    queueItemId = item.queueItemId,
                )
            })
    }

<<<<<<<< HEAD:core/data/src/main/kotlin/com/sebastianvm/musicplayer/core/data/queue/AppQueueRepository.kt
========
    override fun moveQueueItem(from: Int, to: Int) {
        mediaPlaybackClient.moveQueueItem(from, to)
    }

    private fun addToQueue(tracks: List<Track>) {
        mediaPlaybackClient.addToQueue(tracks.map { it.toMediaItem() })
    }

    override suspend fun addToQueue(trackId: Long) {
        val track = trackRepository.getTrack(trackId).first()
        addToQueue(listOf(track))
    }

    override fun playQueueItem(index: Int) {
        mediaPlaybackClient.playQueueItem(index)
    }

    override fun removeItemsFromQueue(queuePositions: List<Int>) {
        mediaPlaybackClient.removeItemsFromQueue(queuePositions)
    }

    override suspend fun initializeQueue() {
        val queue = getFullQueue().first() ?: return
        withContext(Dispatchers.Main) { mediaPlaybackClient.initializeQueue(queue) }
    }

>>>>>>>> 7be87a69 (progress):core/playback/src/main/java/com/sebastianvm/musicplayer/core/playback/queue/AppQueueRepository.kt
    private fun getQueuedTracks(): Flow<List<QueuedTrack>> {
        return mediaQueueDao.getQueuedTracks().map { tracks -> tracks.map { it.asExternalModel() } }
    }
}

fun SavedPlaybackInfo.asNowPlayingInfo(): NowPlayingInfo {
    return NowPlayingInfo(
        nowPlayingPositionInQueue = nowPlayingPositionInQueue,
        lastRecordedPosition = lastRecordedPosition,
    )
}

fun NowPlayingInfo.asSavedPlaybackInfo(): SavedPlaybackInfo {
    return SavedPlaybackInfo(
        nowPlayingPositionInQueue = nowPlayingPositionInQueue,
        lastRecordedPosition = lastRecordedPosition,
    )
}

fun QueueItemWithTrack.asExternalModel(): QueuedTrack {
    return QueuedTrack(
        track = track.asExternalModel(),
        queuePosition = queueItem.queuePosition,
        queueItemId = queueItem.queueItemId,
    )
}
