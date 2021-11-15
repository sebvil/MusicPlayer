package com.sebastianvm.musicplayer.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class FullTrackInfo(
    @Embedded
    val track: Track,
    @Relation(
        parentColumn = "trackGid",
        entityColumn = "artistGid",
        associateBy = Junction(ArtistTrackCrossRef::class)
    )
    val artists: List<Artist>,
    @Relation(
        parentColumn = "trackGid",
        entityColumn = "genreName",
        associateBy = Junction(GenreTrackCrossRef::class)
    )
    val genres: List<Genre>,
    @Relation(
        parentColumn = "albumGid",
        entityColumn = "albumGid",
    )
    val album: Album

)
