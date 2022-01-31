package com.sebastianvm.musicplayer.repository.queue

import com.sebastianvm.musicplayer.database.entities.MediaQueue
import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import kotlinx.coroutines.flow.Flow

interface MediaQueueRepository {
    suspend fun createQueue(
        mediaGroup: MediaGroup,
        sortOption: SortOption,
        sortOrder: SortOrder
    ): Long

    suspend fun insertOrUpdateMediaQueueTrackCrossRefs(queue: MediaGroup, mediaQueueTrackCrossRefs: List<MediaQueueTrackCrossRef>)
    fun getAllQueues(): Flow<List<MediaQueue>>
    fun getQueue(mediaGroup: MediaGroup): Flow<MediaQueue>
    fun getMediaQueTrackCrossRefs(queue: MediaGroup): Flow<List<MediaQueueTrackCrossRef>>
    suspend fun addToQueue(trackIds: List<String>) : Boolean
}
