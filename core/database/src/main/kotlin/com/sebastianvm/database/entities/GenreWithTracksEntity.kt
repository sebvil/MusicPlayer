package com.sebastianvm.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class GenreWithTracksEntity(
    @Embedded val genre: GenreEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = TrackEntity::class,
        associateBy =
            Junction(GenreTrackCrossRef::class, parentColumn = "genreId", entityColumn = "trackId"),
    )
    val tracks: List<DetailedTrack>,
)
