package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            parentColumns = ["trackId"],
            childColumns = ["trackId"],
            entity = Track::class,
            onDelete = CASCADE,
            onUpdate = CASCADE
        )]
)
data class MediaQueueItem(val trackId: String, @PrimaryKey val position: Int, val uniqueQueueItemId: String)
