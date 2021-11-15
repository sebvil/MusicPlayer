package com.sebastianvm.musicplayer.database.entities

import androidx.room.*

@Entity(primaryKeys = ["albumGid", "artistGid"])
data class AlbumsForArtist(
    val albumGid: String,
    @ColumnInfo(index = true)
    val artistGid: String,
)

@Entity(primaryKeys = ["albumGid", "artistGid"])
data class AppearsOnForArtist(
    val albumGid: String,
    @ColumnInfo(index = true)
    val artistGid: String,
)

data class ArtistWithAlbums(
    @Embedded val artist: Artist,
    @Relation(
        parentColumn = "artistGid",
        entityColumn = "albumGid",
        associateBy = Junction(AlbumsForArtist::class),
    )
    val artistAlbums: List<Album>,
    @Relation(
        parentColumn = "artistGid",
        entityColumn = "albumGid",
        associateBy = Junction(AppearsOnForArtist::class),
    )
    val artistAppearsOn: List<Album>,

)

data class AlbumWithArtists(
    @Embedded val album: Album,
    @Relation(
        parentColumn = "albumGid",
        entityColumn = "artistGid",
        associateBy = Junction(AlbumsForArtist::class),
    )
    val artists: List<Artist>
)

