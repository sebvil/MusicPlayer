package com.sebastianvm.musicplayer.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.sebastianvm.musicplayer.player.MediaGroupType

@Entity(primaryKeys = ["mediaGroupType", "groupMediaId", "trackId"])
data class MediaQueueTrackCrossRef(
    val mediaGroupType: MediaGroupType,
    val groupMediaId: String,
    @ColumnInfo(index = true)
    val trackId: String,
    val trackIndex: Int
)