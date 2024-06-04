package com.sebastianvm.musicplayer.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["trackId"],
            entity = Track::class,
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class MediaQueueItem(
    @ColumnInfo(index = true) val trackId: Long,
    @PrimaryKey val queuePosition: Int,
)
