package com.sebastianvm.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class AlbumWithArtistsEntity(
    @Embedded val album: AlbumEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = ArtistEntity::class,
        associateBy =
            Junction(AlbumsForArtist::class, parentColumn = "albumId", entityColumn = "artistId"),
    )
    val artists: List<ArtistEntity>,
)
