package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sebastianvm.musicplayer.database.entities.MediaQueue
import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef
import com.sebastianvm.musicplayer.player.MediaType
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaQueueDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQueue(queue: MediaQueue) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMediaQueueTrackCrossRefs(mediaQueueTrackCrossRefs: List<MediaQueueTrackCrossRef>)

    @Query("SELECT * FROM MediaQueue")
    fun getAllQueues(): Flow<List<MediaQueue>>

    @Query("SELECT * FROM MediaQueue WHERE MediaQueue.groupMediaId=:queueId AND MediaQueue.mediaType=:mediaType")
    fun getQueue(queueId: String, mediaType: MediaType): Flow<MediaQueue>
}