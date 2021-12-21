package com.sebastianvm.musicplayer.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class FullAlbumInfo(
    @Embedded
    val album: Album,
    @Relation(
        parentColumn = "albumId",
        entityColumn = "artistId",
        associateBy = Junction(AlbumsForArtist::class)
    )
    val artists: List<Artist>,
    @Relation(
        parentColumn = "albumId",
        entityColumn = "albumId",
    )
    val tracks: List<Track>,
)
