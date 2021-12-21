package com.sebastianvm.musicplayer.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["queueId", "trackId"])
data class MediaQueueTrackCrossRef(
    val queueId: Long,
    @ColumnInfo(index = true)
    val trackId: String,
)