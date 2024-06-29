package com.sebastianvm.musicplayer.core.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class FullAlbumEntity(
    @Embedded val album: com.sebastianvm.musicplayer.core.database.entities.AlbumEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = ArtistEntity::class,
        associateBy =
            Junction(AlbumsForArtist::class, parentColumn = "albumId", entityColumn = "artistId"),
    )
    val artists: List<ArtistEntity>,
    @Relation(parentColumn = "id", entityColumn = "albumId", entity = TrackEntity::class)
    val tracks: List<DetailedTrack>,
)
