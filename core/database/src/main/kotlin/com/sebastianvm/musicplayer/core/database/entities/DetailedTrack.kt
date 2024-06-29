package com.sebastianvm.musicplayer.core.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class DetailedTrack(
    @Embedded val track: TrackEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = ArtistEntity::class,
        associateBy =
            Junction(
                ArtistTrackCrossRef::class,
                parentColumn = "trackId",
                entityColumn = "artistId",
            ),
    )
    val artists: List<ArtistEntity>,
    @Relation(
        parentColumn = "albumId",
        entityColumn = "id",
        entity = com.sebastianvm.musicplayer.core.database.entities.AlbumEntity::class,
    )
    val album: com.sebastianvm.musicplayer.core.database.entities.AlbumEntity,
)
