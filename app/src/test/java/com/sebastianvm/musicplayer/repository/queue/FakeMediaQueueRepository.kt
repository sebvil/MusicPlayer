package com.sebastianvm.musicplayer.repository.queue

import com.sebastianvm.musicplayer.database.entities.MediaQueue
import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMediaQueueRepository : MediaQueueRepository {
    override suspend fun createQueue(
        mediaGroup: MediaGroup,
        sortOption: SortOption,
        sortOrder: SortOrder
    ): Long = 1L

    override suspend fun insertOrUpdateMediaQueueTrackCrossRefs(
        queue: MediaGroup,
        mediaQueueTrackCrossRefs: List<MediaQueueTrackCrossRef>
    ) = Unit

    override fun getAllQueues(): Flow<List<MediaQueue>> = flow {  }

    override fun getQueue(mediaGroup: MediaGroup): Flow<MediaQueue> = flow {  }
}
