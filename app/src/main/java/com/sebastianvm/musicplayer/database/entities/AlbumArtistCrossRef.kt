package com.sebastianvm.musicplayer.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["albumId", "artistId"])
data class AlbumsForArtist(
    val albumId: String,
    @ColumnInfo(index = true)
    val artistId: String,
    val albumName: String,
    val artistName: String,
)

@Entity(primaryKeys = ["albumId", "artistId"])
data class AppearsOnForArtist(
    val albumId: String,
    @ColumnInfo(index = true)
    val artistId: String,
)

data class ArtistWithAlbums(
    @Embedded val artist: Artist,
    @Relation(
        parentColumn = "artistId",
        entityColumn = "albumId",
        associateBy = Junction(AlbumsForArtist::class),
    )
    val artistAlbums: List<Album>,
    @Relation(
        parentColumn = "artistId",
        entityColumn = "albumId",
        associateBy = Junction(AppearsOnForArtist::class),
    )
    val artistAppearsOn: List<Album>,

)

data class AlbumWithArtists(
    @Embedded val album: Album,
    @Relation(
        parentColumn = "albumId",
        entityColumn = "artistId",
        associateBy = Junction(AlbumsForArtist::class),
    )
    val artists: List<Artist>
)

