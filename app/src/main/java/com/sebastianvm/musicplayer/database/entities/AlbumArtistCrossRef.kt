package com.sebastianvm.musicplayer.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["albumId", "artistId"])
data class AlbumsForArtist(
    val albumId: Long,
    @ColumnInfo(index = true)
    val artistId: Long,
    val artistName: String,
    val albumName: String,
)

@Entity(primaryKeys = ["albumId", "artistId"])
data class AppearsOnForArtist(
    val albumId: Long,
    @ColumnInfo(index = true)
    val artistId: Long,
)

data class ArtistWithAlbums(
    @Embedded val artist: Artist,
    @Relation(
        entity = Album::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            AlbumsForArtist::class,
            parentColumn = "artistId",
            entityColumn = "albumId"
        ),
    )
    val artistAlbums: List<Album>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = Album::class,
        associateBy = Junction(
            AppearsOnForArtist::class,
            parentColumn = "artistId",
            entityColumn = "albumId"
        )
    )
    val artistAppearsOn: List<Album>,
)

data class AlbumWithArtists(
    @Embedded val album: Album,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            AlbumsForArtist::class,
            parentColumn = "albumId",
            entityColumn = "artistId"
        ),
    )
    val artists: List<Artist>
)
