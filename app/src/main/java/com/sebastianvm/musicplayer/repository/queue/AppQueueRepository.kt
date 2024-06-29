package com.sebastianvm.musicplayer.repository.queue

import com.sebastianvm.musicplayer.core.database.daos.MediaQueueDao
import com.sebastianvm.musicplayer.core.database.entities.QueueItemEntity
import com.sebastianvm.musicplayer.core.database.entities.QueueItemWithTrack
import com.sebastianvm.musicplayer.core.model.BasicQueuedTrack
import com.sebastianvm.musicplayer.core.model.FullQueue
import com.sebastianvm.musicplayer.core.model.NextUpQueue
import com.sebastianvm.musicplayer.core.model.NowPlayingInfo
import com.sebastianvm.musicplayer.core.model.QueuedTrack
import com.sebastianvm.musicplayer.core.model.Track
import com.sebastianvm.musicplayer.datastore.NowPlayingInfoDataSource
import com.sebastianvm.musicplayer.datastore.SavedPlaybackInfo
import com.sebastianvm.musicplayer.player.MediaPlaybackClient
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.repository.track.asExternalModel
import com.sebastianvm.musicplayer.util.extensions.toMediaItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AppQueueRepository(
    private val nowPlayingInfoDataSource: NowPlayingInfoDataSource,
    private val trackRepository: TrackRepository,
    private val mediaQueueDao: MediaQueueDao,
    private val mediaPlaybackClient: MediaPlaybackClient,
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
            }
        )
    }

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
