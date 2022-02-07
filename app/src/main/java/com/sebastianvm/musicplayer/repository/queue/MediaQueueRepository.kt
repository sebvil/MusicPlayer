package com.sebastianvm.musicplayer.repository.queue

import com.sebastianvm.musicplayer.database.entities.MediaQueue
import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import kotlinx.coroutines.flow.Flow

interface MediaQueueRepository {
    suspend fun createQueue(
        mediaGroup: MediaGroup,
        sortOption: MediaSortOption,
        sortOrder: MediaSortOrder
    ): Long

    suspend fun insertOrUpdateMediaQueueTrackCrossRefs(queue: MediaGroup, mediaQueueTrackCrossRefs: List<MediaQueueTrackCrossRef>)
    fun getAllQueues(): Flow<List<MediaQueue>>
    fun getQueue(mediaGroup: MediaGroup): Flow<MediaQueue>
    fun getMediaQueTrackCrossRefs(queue: MediaGroup): Flow<List<MediaQueueTrackCrossRef>>
    suspend fun addToQueue(trackIds: List<String>) : Boolean
}
