package com.sebastianvm.musicplayer.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["albumId", "artistId"])
data class AlbumsForArtist(
    val albumId: Long,
    val artistId: Long,
    val artistName: String,
    val albumName: String,
)

@Entity(primaryKeys = ["albumId", "artistId"])
data class AppearsOnForArtist(
    val albumId: Long,
    val artistId: Long,
)

data class ArtistWithAlbums(
    @Embedded val artist: Artist,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = Album::class,
        projection = ["id"],
        associateBy = Junction(
            AlbumsForArtist::class,
            parentColumn = "albumId",
            entityColumn = "artistId"
        ),
    )
    val artistAlbums: List<Long>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = Album::class,
        projection = ["id"],
        associateBy = Junction(
            AppearsOnForArtist::class,
            parentColumn = "albumId",
            entityColumn = "artistId"
        )
    )
    val artistAppearsOn: List<Long>,

    )

data class AlbumWithArtists(
    @Embedded val album: Album,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(AlbumsForArtist::class, parentColumn = "albumId", entityColumn = "artistId"),
    )
    val artists: List<Artist>
)
