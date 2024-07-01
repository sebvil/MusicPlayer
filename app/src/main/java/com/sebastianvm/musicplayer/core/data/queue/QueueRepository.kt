package com.sebastianvm.musicplayer.core.data.queue

import com.sebastianvm.musicplayer.core.model.BasicQueuedTrack
import com.sebastianvm.musicplayer.core.model.FullQueue
import com.sebastianvm.musicplayer.core.model.NextUpQueue
import com.sebastianvm.musicplayer.core.model.NowPlayingInfo
import kotlinx.coroutines.flow.Flow

interface QueueRepository {
    fun getQueue(): Flow<NextUpQueue>

    fun getFullQueue(): Flow<FullQueue?>

    suspend fun saveQueue(nowPlayingInfo: NowPlayingInfo, queuedTracks: List<BasicQueuedTrack>)

    fun moveQueueItem(from: Int, to: Int)

    suspend fun addToQueue(trackId: Long)

    fun playQueueItem(index: Int)

    fun removeItemsFromQueue(queuePositions: List<Int>)
}
