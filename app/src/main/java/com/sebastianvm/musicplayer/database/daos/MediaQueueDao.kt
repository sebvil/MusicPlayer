package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.MediaQueue
import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef
import com.sebastianvm.musicplayer.player.MediaGroupType
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaQueueDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQueue(queue: MediaQueue): Long

    @Query("DELETE FROM MediaQueueTrackCrossRef WHERE MediaQueueTrackCrossRef.groupMediaId=:queueId AND MediaQueueTrackCrossRef.mediaGroupType=:mediaGroupType")
    suspend fun deleteMediaQueueTrackCrossRefs(queueId: String, mediaGroupType: MediaGroupType)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaQueueTrackCrossRefs(mediaQueueTrackCrossRefs: List<MediaQueueTrackCrossRef>)

    @Transaction
    suspend fun insertOrUpdateMediaQueueTrackCrossRefs(
        queueId: String,
        mediaGroupType: MediaGroupType,
        mediaQueueTrackCrossRefs: List<MediaQueueTrackCrossRef>
    ) {
        deleteMediaQueueTrackCrossRefs(queueId, mediaGroupType)
        insertMediaQueueTrackCrossRefs(mediaQueueTrackCrossRefs)
    }

    @Query("SELECT * FROM MediaQueue")
    fun getAllQueues(): Flow<List<MediaQueue>>

    @Query("SELECT * FROM MediaQueue WHERE MediaQueue.groupMediaId=:queueId AND MediaQueue.mediaGroupType=:mediaGroupType")
    fun getQueue(queueId: String, mediaGroupType: MediaGroupType): Flow<MediaQueue>

    @Query(
        "SELECT * FROM MediaQueueTrackCrossRef " +
                "WHERE MediaQueueTrackCrossRef.groupMediaId=:queueId " +
                "AND MediaQueueTrackCrossRef.mediaGroupType=:mediaGroupType " +
                "ORDER BY MediaQueueTrackCrossRef.trackIndex"
    )
    fun getMediaQueTrackCrossRefs(
        queueId: String,
        mediaGroupType: MediaGroupType
    ): Flow<List<MediaQueueTrackCrossRef>>
}