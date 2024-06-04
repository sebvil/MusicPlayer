package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.MediaQueueItem
import com.sebastianvm.musicplayer.database.entities.QueuedTrack
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaQueueDao {
    @Transaction
    @Query(
        "SELECT Track.*, MediaQueueItem.queuePosition, MediaQueueItem.queueItemId FROM Track " +
            "INNER JOIN MediaQueueItem " +
            "ON Track.id=MediaQueueItem.trackId " +
            "ORDER BY MediaQueueItem.queuePosition ASC"
    )
    fun getQueuedTracks(): Flow<List<QueuedTrack>>

    @Insert
    fun insertQueue(mediaQueueItems: List<MediaQueueItem>)

    @Query("DELETE FROM MediaQueueItem")
    fun clearQueue()

    @Transaction
    fun saveQueue(mediaQueueItems: List<MediaQueueItem>) {
        clearQueue()
        insertQueue(mediaQueueItems)
    }
}
