package com.sebastianvm.musicplayer.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class FullTrackInfo(
    @Embedded
    val track: Track,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = Artist::class,
        projection = ["artistName"],
        associateBy = Junction(
            ArtistTrackCrossRef::class,
            parentColumn = "trackId",
            entityColumn = "artistId"
        )
    )
    val artists: List<String>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = Genre::class,
        projection = ["genreName"],
        associateBy = Junction(
            GenreTrackCrossRef::class,
            parentColumn = "trackId",
            entityColumn = "genreId"
        )
    )
    val genres: List<String>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = Playlist::class,
        projection = ["playlistName"],
        associateBy = Junction(
            PlaylistTrackCrossRef::class,
            parentColumn = "trackId",
            entityColumn = "playlistId"
        )
    )
    val playlists: List<String>,
)
