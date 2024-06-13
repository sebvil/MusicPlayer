package com.sebastianvm.musicplayer.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sebastianvm.musicplayer.model.Genre

data class GenreWithTracksEntity(
    @Embedded val genre: GenreEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = TrackEntity::class,
        associateBy =
            Junction(GenreTrackCrossRef::class, parentColumn = "genreId", entityColumn = "trackId"),
    )
    val tracks: List<DetailedTrack>,
)

fun GenreWithTracksEntity.asExternalModel(): Genre {
    return Genre(
        id = genre.id,
        name = genre.name,
        tracks = tracks.map { it.asExternalModel() },
    )
}
