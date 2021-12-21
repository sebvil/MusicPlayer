package com.sebastianvm.musicplayer.repository

import com.sebastianvm.musicplayer.database.daos.MediaQueueDao
import com.sebastianvm.musicplayer.database.entities.MediaQueue
import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Track
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaQueueRepository @Inject constructor(private val mediaQueueDao: MediaQueueDao) {
    suspend fun createQueue(tracks: List<Track>): Long {
        val queueId = mediaQueueDao.insertQueue(MediaQueue(queueId = 0))
        mediaQueueDao.insertMediaQueueTrackCrossRefs(tracks.map {
            MediaQueueTrackCrossRef(
                queueId = queueId,
                trackId = it.trackId
            )
        })
        return queueId
    }
}