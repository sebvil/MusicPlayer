package com.sebastianvm.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlaylistWithTracksEntity(
    @Embedded val playlist: PlaylistEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = TrackEntity::class,
        associateBy =
            Junction(
                PlaylistTrackCrossRef::class,
                parentColumn = "playlistId",
                entityColumn = "trackId",
            ),
    )
    val tracks: List<DetailedTrack>,
)
