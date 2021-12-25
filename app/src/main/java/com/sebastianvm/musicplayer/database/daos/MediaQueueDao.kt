package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sebastianvm.musicplayer.database.entities.MediaQueue
import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaQueueDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQueue(queue: MediaQueue) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMediaQueueTrackCrossRefs(mediaQueueTrackCrossRefs: List<MediaQueueTrackCrossRef>)

    @Query("SELECT * FROM MediaQueue")
    fun getAllQueues(): Flow<List<MediaQueue>>
}