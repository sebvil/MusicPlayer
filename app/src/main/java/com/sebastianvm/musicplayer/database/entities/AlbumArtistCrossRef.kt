package com.sebastianvm.musicplayer.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["albumId", "artistName"])
data class AlbumsForArtist(
    val albumId: Long,
    @ColumnInfo(index = true)
    val artistName: String,
    val albumName: String,
)

@Entity(primaryKeys = ["albumId", "artistName"])
data class AppearsOnForArtist(
    val albumId: Long,
    @ColumnInfo(index = true)
    val artistName: String,
)

data class ArtistWithAlbums(
    @Embedded val artist: Artist,
    @Relation(
        parentColumn = "artistName",
        entityColumn = "albumId",
        entity = Album::class,
        projection = ["albumId"],
        associateBy = Junction(AlbumsForArtist::class),
    )
    val artistAlbums: List<Long>,
    @Relation(
        parentColumn = "artistName",
        entityColumn = "albumId",
        entity = Album::class,
        projection = ["albumId"],
        associateBy = Junction(AppearsOnForArtist::class),
    )
    val artistAppearsOn: List<Long>,

)

data class AlbumWithArtists(
    @Embedded val album: Album,
    @Relation(
        parentColumn = "albumId",
        entityColumn = "artistName",
        associateBy = Junction(AlbumsForArtist::class),
    )
    val artists: List<Artist>
)
