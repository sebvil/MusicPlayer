package com.sebastianvm.musicplayer.database.entities

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["albumId", "artistId"])
data class AlbumsForArtist(
    val albumId: Long,
    @ColumnInfo(index = true) val artistId: Long,
    val artistName: String,
    val albumName: String,
    val year: Long,
)

@Entity(primaryKeys = ["albumId", "artistId"])
data class AppearsOnForArtist(
    val albumId: Long,
    @ColumnInfo(index = true) val artistId: Long,
    val year: Long,
)

@DatabaseView("SELECT * FROM AlbumsForArtist ORDER BY year DESC")
data class AlbumsForArtistByYear(
    val albumId: Long,
    @ColumnInfo(index = true) val artistId: Long,
    val year: Long,
)

@DatabaseView("SELECT * FROM AppearsOnForArtist ORDER BY year DESC")
data class AppearsOnForArtistByYear(
    val albumId: Long,
    @ColumnInfo(index = true) val artistId: Long,
    val year: Long,
)

data class ArtistWithAlbums(
    @Embedded val artist: Artist,
    @Relation(
        entity = Album::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy =
            Junction(
                AlbumsForArtistByYear::class,
                parentColumn = "artistId",
                entityColumn = "albumId",
            ),
    )
    val artistAlbums: List<Album>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = Album::class,
        associateBy =
            Junction(
                AppearsOnForArtistByYear::class,
                parentColumn = "artistId",
                entityColumn = "albumId",
            ),
    )
    val artistAppearsOn: List<Album>,
)
