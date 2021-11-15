package com.sebastianvm.musicplayer.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class FullAlbumInfo(
    @Embedded
    val album: Album,
    @Relation(
        parentColumn = "albumGid",
        entityColumn = "artistGid",
        associateBy = Junction(AlbumsForArtist::class)
    )
    val artists: List<Artist>,
    @Relation(
        parentColumn = "albumGid",
        entityColumn = "albumGid",
    )
    val tracks: List<Track>,
)
