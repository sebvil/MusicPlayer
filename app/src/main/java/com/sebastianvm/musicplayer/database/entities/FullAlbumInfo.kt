package com.sebastianvm.musicplayer.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class FullAlbumInfo(
    @Embedded
    val album: Album,
    @Relation(
        parentColumn = "albumId",
        entityColumn = "artistName",
        entity = Artist::class,
        projection = ["artistName"],
        associateBy = Junction(AlbumsForArtist::class)
    )
    val artists: List<String>,
    @Relation(
        parentColumn = "albumId",
        entityColumn = "albumId",
        entity = Track::class,
        projection = ["trackId"],
    )
    val tracks: List<String>,
)
