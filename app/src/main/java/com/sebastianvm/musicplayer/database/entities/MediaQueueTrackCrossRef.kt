package com.sebastianvm.musicplayer.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["queueId", "trackId"])
data class MediaQueueTrackCrossRef(
    val queueId: String,
    @ColumnInfo(index = true)
    val trackId: String,
)

data class MediaQueueWithTracks(
    @Embedded
    val queue: MediaQueue,
    @Relation(
        parentColumn = "queueId",
        entityColumn = "trackId",
        associateBy = Junction(MediaQueueTrackCrossRef::class)
    )
    val tracks: List<Track>
)