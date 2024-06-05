package com.sebastianvm.musicplayer.repository.queue

import com.sebastianvm.musicplayer.database.entities.QueuedTrack
import com.sebastianvm.musicplayer.model.FullQueue
import com.sebastianvm.musicplayer.model.NextUpQueue
import com.sebastianvm.musicplayer.model.NowPlayingInfo
import com.sebastianvm.musicplayer.player.MediaGroup
import kotlinx.coroutines.flow.Flow

interface QueueRepository {
    fun getQueue(): Flow<NextUpQueue?>
    fun getFullQueue(): Flow<FullQueue?>
    suspend fun saveQueue(nowPlayingInfo: NowPlayingInfo, queuedTracksIds: List<QueuedTrack>)
    fun moveQueueItem(from: Int, to: Int)
    suspend fun addToQueue(mediaGroup: MediaGroup)
}
