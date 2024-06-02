package com.sebastianvm.musicplayer.repository.queue

import com.sebastianvm.musicplayer.model.NowPlayingInfo
import com.sebastianvm.musicplayer.model.Queue
import kotlinx.coroutines.flow.Flow

interface QueueRepository {
    fun getQueue(): Flow<Queue>
    suspend fun saveQueue(nowPlayingInfo: NowPlayingInfo, queuedTracksIds: List<Long>)
}