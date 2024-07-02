package com.sebastianvm.musicplayer.core.playback.queue

import com.sebastianvm.musicplayer.core.model.BasicQueuedTrack
import com.sebastianvm.musicplayer.core.model.FullQueue
import com.sebastianvm.musicplayer.core.model.NextUpQueue
import com.sebastianvm.musicplayer.core.model.NowPlayingInfo
import kotlinx.coroutines.flow.Flow

interface QueueRepository {
    fun getQueue(): Flow<NextUpQueue>

    fun getFullQueue(): Flow<FullQueue?>

    suspend fun saveQueue(nowPlayingInfo: NowPlayingInfo, queuedTracks: List<BasicQueuedTrack>)
<<<<<<<< HEAD:core/data/src/main/kotlin/com/sebastianvm/musicplayer/core/data/queue/QueueRepository.kt
========

    fun moveQueueItem(from: Int, to: Int)

    suspend fun addToQueue(trackId: Long)

    fun playQueueItem(index: Int)

    fun removeItemsFromQueue(queuePositions: List<Int>)

    suspend fun initializeQueue()
>>>>>>>> 7be87a69 (progress):core/playback/src/main/java/com/sebastianvm/musicplayer/core/playback/queue/QueueRepository.kt
}
