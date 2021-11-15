package com.sebastianvm.musicplayer.database.entities

import androidx.room.*

@Entity(primaryKeys = ["genreName", "trackGid"])
data class GenreTrackCrossRef(
    val genreName: String,
    @ColumnInfo(index = true)
    val trackGid: String,
)

data class GenreWithTracks(
    @Embedded
    val genre: Genre,
    @Relation(
        parentColumn = "genreName",
        entityColumn = "trackGid",
        associateBy = Junction(GenreTrackCrossRef::class)
    )
    val tracks: List<Track>
)

