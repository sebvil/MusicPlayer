package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.sebastianvm.musicplayer.database.entities.MediaQueue
import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef

@Dao
interface MediaQueueDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQueue(queue: MediaQueue) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMediaQueueTrackCrossRefs(mediaQueueTrackCrossRefs: List<MediaQueueTrackCrossRef>)
}