package com.sebastianvm.musicplayer.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["mediaGroupType", "groupMediaId", "trackId", "trackIndex"])
data class MediaQueueTrackCrossRef(
    val mediaGroupType: String,
    val groupMediaId: String,
    @ColumnInfo(index = true)
    val trackId: String,
    val trackIndex: Int
)