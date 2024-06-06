package com.sebastianvm.musicplayer.repository.queue

import com.sebastianvm.musicplayer.database.entities.QueuedTrack
import com.sebastianvm.musicplayer.model.FullQueue
import com.sebastianvm.musicplayer.model.NextUpQueue
import com.sebastianvm.musicplayer.model.NowPlayingInfo
import com.sebastianvm.musicplayer.player.MediaGroup
import kotlinx.coroutines.flow.Flow

class FakeQueueRepository : QueueRepository {
    override fun getQueue(): Flow<NextUpQueue?> {
        TODO("Not yet implemented")
    }

    override fun getFullQueue(): Flow<FullQueue?> {
        TODO("Not yet implemented")
    }

    override suspend fun saveQueue(
        nowPlayingInfo: NowPlayingInfo,
        queuedTracksIds: List<QueuedTrack>
    ) {
        TODO("Not yet implemented")
    }

    override fun moveQueueItem(from: Int, to: Int) {
        TODO("Not yet implemented")
    }

    private val _addToQueueInvocations: MutableList<AddToQueueArguments> = mutableListOf()

    data class AddToQueueArguments(val mediaGroup: MediaGroup)

    val addToQueueInvocations: List<AddToQueueArguments>
        get() = _addToQueueInvocations

    override suspend fun addToQueue(mediaGroup: MediaGroup) {
        _addToQueueInvocations.add(AddToQueueArguments(mediaGroup))
    }

    fun resetAddToQueueInvocations() {
        _addToQueueInvocations.clear()
    }
}
