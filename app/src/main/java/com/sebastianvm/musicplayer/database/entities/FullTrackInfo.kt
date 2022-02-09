package com.sebastianvm.musicplayer.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class FullTrackInfo(
    @Embedded
    val track: Track,
    @Relation(
        parentColumn = "trackId",
        entityColumn = "artistName",
        entity = Artist::class,
        projection = ["artistName"],
        associateBy = Junction(ArtistTrackCrossRef::class)
    )
    val artists: List<String>,
    @Relation(
        parentColumn = "trackId",
        entityColumn = "genreName",
        entity = Genre::class,
        projection = ["genreName"],
        associateBy = Junction(GenreTrackCrossRef::class)
    )
    val genres: List<String>,
)
