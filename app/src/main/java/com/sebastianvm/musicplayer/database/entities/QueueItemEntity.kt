package com.sebastianvm.musicplayer.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    foreignKeys =
        [
            ForeignKey(
                parentColumns = ["id"],
                childColumns = ["trackId"],
                entity = TrackEntity::class,
                onDelete = CASCADE,
                onUpdate = CASCADE,
            )
        ]
)
data class QueueItemEntity(
    @ColumnInfo(index = true) val trackId: Long,
    @PrimaryKey val queuePosition: Int,
    val queueItemId: Long,
)

data class QueueItemWithTrack(
    @Embedded val queueItem: QueueItemEntity,
    @Relation(parentColumn = "trackId", entityColumn = "id") val track: TrackEntity,
)

fun QueueItemWithTrack.asExternalModel(): QueuedTrack {
    return QueuedTrack(
        id = track.id,
        trackName = track.trackName,
        artists = track.artists,
        queuePosition = queueItem.queuePosition,
        queueItemId = queueItem.queueItemId,
    )
}
