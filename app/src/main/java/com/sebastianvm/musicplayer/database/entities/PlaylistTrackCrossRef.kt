package com.sebastianvm.musicplayer.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["playlistId", "position"])
data class PlaylistTrackCrossRef(
    val playlistId: Long,
    @ColumnInfo(index = true) val trackId: Long,
    val position: Long,
)

data class PlaylistWithTracks(
    @Embedded val playlist: PlaylistEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy =
            Junction(
                PlaylistTrackCrossRef::class,
                parentColumn = "playlistId",
                entityColumn = "trackId",
            ),
    )
    val tracks: List<TrackEntity>,
)

data class PlaylistTrackCrossRefKeys(val playlistId: Long, val position: Long)
