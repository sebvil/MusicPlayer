package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.QueueItemEntity
import com.sebastianvm.musicplayer.database.entities.QueueItemWithTrack
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaQueueDao {
    @Transaction
    @Query(
        "SELECT QueueItemEntity.*, QueueItemEntity.queueItemId FROM QueueItemEntity " +
            "ORDER BY QueueItemEntity.queuePosition ASC"
    )
    fun getQueuedTracks(): Flow<List<QueueItemWithTrack>>

    @Insert fun insertQueue(mediaQueueItems: List<QueueItemEntity>)

    @Query("DELETE FROM QueueItemEntity") fun clearQueue()

    @Transaction
    fun saveQueue(mediaQueueItems: List<QueueItemEntity>) {
        clearQueue()
        insertQueue(mediaQueueItems)
    }
}
