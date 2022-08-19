package com.sebastianvm.musicplayer.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class AlbumWithTracks(
    @Embedded
    val album: Album,
    @Relation(
        parentColumn = "id",
        entityColumn = "albumId",
        entity = Track::class,
    )
    val tracks: List<BasicTrack>,
)