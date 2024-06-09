package com.sebastianvm.musicplayer.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sebastianvm.musicplayer.model.Track

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
    @Relation(parentColumn = "albumId", entityColumn = "id", entity = AlbumEntity::class)
    val album: AlbumEntity,
)

fun DetailedTrack.asExternalModel() =
    Track(
        id = track.id,
        name = track.trackName,
        artists = artists.map { it.asExternalModel() },
        albumId = album.id,
    )
