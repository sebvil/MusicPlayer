package com.sebastianvm.musicplayer.repository.queue

import com.sebastianvm.musicplayer.database.entities.MediaQueue
import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef
import com.sebastianvm.musicplayer.player.MediaGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMediaQueueRepository : MediaQueueRepository {
    override suspend fun createQueue(mediaGroup: MediaGroup): Long = 1L

    override suspend fun insertOrUpdateMediaQueueTrackCrossRefs(
        queue: MediaGroup,
        mediaQueueTrackCrossRefs: List<MediaQueueTrackCrossRef>
    ) = Unit

    override fun getAllQueues(): Flow<List<MediaQueue>> = flow {  }

    override fun getQueue(mediaGroup: MediaGroup): Flow<MediaQueue> = flow {  }
    override fun getMediaQueTrackCrossRefs(queue: MediaGroup): Flow<List<MediaQueueTrackCrossRef>> = flow {  }

    override suspend fun addToQueue(trackIds: List<String>): Boolean = false
}
