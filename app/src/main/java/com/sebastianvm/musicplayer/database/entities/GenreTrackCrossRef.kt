package com.sebastianvm.musicplayer.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["genreId", "trackId"])
data class GenreTrackCrossRef(
    val genreId: Long,
    val trackId: Long,
)

data class GenreWithTracks(
    @Embedded
    val genre: Genre,
    @Relation(
        parentColumn = "genreName",
        entityColumn = "trackId",
        associateBy = Junction(GenreTrackCrossRef::class)
    )
    val tracks: List<Track>
)

