package com.sebastianvm.musicplayer.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.sebastianvm.musicplayer.player.MediaType

@Entity(primaryKeys = ["mediaType", "groupMediaId", "trackId"])
data class MediaQueueTrackCrossRef(
    val mediaType: MediaType,
    val groupMediaId: String,
    @ColumnInfo(index = true)
    val trackId: String,
    val trackIndex: Int
)