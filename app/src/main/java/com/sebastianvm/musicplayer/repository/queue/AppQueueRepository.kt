package com.sebastianvm.musicplayer.repository.queue

import com.sebastianvm.musicplayer.database.daos.MediaQueueDao
import com.sebastianvm.musicplayer.database.entities.QueueItemEntity
import com.sebastianvm.musicplayer.database.entities.asExternalModel
import com.sebastianvm.musicplayer.datastore.NowPlayingInfoDataSource
import com.sebastianvm.musicplayer.model.BasicQueuedTrack
import com.sebastianvm.musicplayer.model.FullQueue
import com.sebastianvm.musicplayer.model.NextUpQueue
import com.sebastianvm.musicplayer.model.NowPlayingInfo
import com.sebastianvm.musicplayer.model.QueuedTrack
import com.sebastianvm.musicplayer.model.Track
import com.sebastianvm.musicplayer.player.MediaPlaybackClient
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.util.extensions.toMediaItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AppQueueRepository(
    private val nowPlayingInfoDataSource: NowPlayingInfoDataSource,
    private val trackRepository: TrackRepository,
    private val mediaQueueDao: MediaQueueDao,
    private val mediaPlaybackClient: MediaPlaybackClient,
) : QueueRepository {

    override fun getQueue(): Flow<NextUpQueue?> {
        return combine(nowPlayingInfoDataSource.getNowPlayingInfo(), getQueuedTracks()) {
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
    }

    override fun getFullQueue(): Flow<FullQueue?> {
        return combine(nowPlayingInfoDataSource.getNowPlayingInfo(), getQueuedTracks()) {
            nowPlayingInfo,
            queuedTracks ->
            nowPlayingInfo.nowPlayingPositionInQueue.takeUnless { it == -1 } ?: return@combine null
            FullQueue(nowPlayingInfo = nowPlayingInfo, queue = queuedTracks)
        }
    }

    override suspend fun saveQueue(
        nowPlayingInfo: NowPlayingInfo,
        queuedTracks: List<BasicQueuedTrack>,
    ) {
        nowPlayingInfoDataSource.setNowPlayingInfo(nowPlayingInfo)
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
