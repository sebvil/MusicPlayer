package com.sebastianvm.musicplayer.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class FullAlbumInfo(
    @Embedded
    val album: Album,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = Artist::class,
        projection = ["id"],
        associateBy = Junction(AlbumsForArtist::class, parentColumn = "albumId", entityColumn = "artistId")
    )
    val artists: List<Long>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = Track::class,
        projection = ["id"],
    )
    val tracks: List<Long>,
)
