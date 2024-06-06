package com.sebastianvm.musicplayer.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TrackWithArtists(
    @Embedded val track: Track,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = Artist::class,
        projection = ["id"],
        associateBy =
            Junction(
                ArtistTrackCrossRef::class,
                parentColumn = "trackId",
                entityColumn = "artistId",
            ),
    )
    val artists: List<Long>,
)
