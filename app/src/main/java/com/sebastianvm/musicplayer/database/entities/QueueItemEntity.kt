package com.sebastianvm.musicplayer.database.entities

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.sebastianvm.musicplayer.model.QueuedTrack

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

@DatabaseView(
    "SELECT QueueItemEntity.* , TrackEntity.* FROM QueueItemEntity JOIN TrackEntity ON QueueItemEntity.trackId = TrackEntity.id"
)
data class QueueItemWithTrack(
    @Embedded val queueItem: QueueItemEntity,
    @Embedded val track: DetailedTrack,
)

fun QueueItemWithTrack.asExternalModel(): QueuedTrack {
    return QueuedTrack(
        track = track.asExternalModel(),
        queuePosition = queueItem.queuePosition,
        queueItemId = queueItem.queueItemId,
    )
}
