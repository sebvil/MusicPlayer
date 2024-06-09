package com.sebastianvm.musicplayer.repository.queue

import com.sebastianvm.musicplayer.model.BasicQueuedTrack
import com.sebastianvm.musicplayer.model.FullQueue
import com.sebastianvm.musicplayer.model.NextUpQueue
import com.sebastianvm.musicplayer.model.NowPlayingInfo
import kotlinx.coroutines.flow.Flow

interface QueueRepository {
    fun getQueue(): Flow<NextUpQueue?>

    fun getFullQueue(): Flow<FullQueue?>

    suspend fun saveQueue(nowPlayingInfo: NowPlayingInfo, queuedTracks: List<BasicQueuedTrack>)

    fun moveQueueItem(from: Int, to: Int)

    suspend fun addToQueue(trackId: Long)

    fun playQueueItem(index: Int)
}
