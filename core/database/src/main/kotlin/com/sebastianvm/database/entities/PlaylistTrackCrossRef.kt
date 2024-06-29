package com.sebastianvm.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["playlistId", "position"])
data class PlaylistTrackCrossRef(
    val playlistId: Long,
    @ColumnInfo(index = true) val trackId: Long,
    val position: Long,
)

data class PlaylistTrackCrossRefKeys(val playlistId: Long, val position: Long)
