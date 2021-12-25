package com.sebastianvm.musicplayer.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["genreName", "trackId"])
data class GenreTrackCrossRef(
    val genreName: String,
    @ColumnInfo(index = true)
    val trackId: String,
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

