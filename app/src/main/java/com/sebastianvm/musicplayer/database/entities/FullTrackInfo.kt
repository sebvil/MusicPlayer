package com.sebastianvm.musicplayer.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class FullTrackInfo(
    @Embedded
    val track: Track,
    @Relation(
        parentColumn = "trackId",
        entityColumn = "artistId",
        associateBy = Junction(ArtistTrackCrossRef::class)
    )
    val artists: List<Artist>,
    @Relation(
        parentColumn = "trackId",
        entityColumn = "genreName",
        associateBy = Junction(GenreTrackCrossRef::class)
    )
    val genres: List<Genre>,
    @Relation(
        parentColumn = "albumId",
        entityColumn = "albumId",
    )
    val album: Album

)
