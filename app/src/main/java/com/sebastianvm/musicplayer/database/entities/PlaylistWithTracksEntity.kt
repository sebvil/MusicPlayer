package com.sebastianvm.musicplayer.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sebastianvm.model.Playlist

data class PlaylistWithTracksEntity(
    @Embedded val playlist: PlaylistEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = TrackEntity::class,
        associateBy =
            Junction(
                PlaylistTrackCrossRef::class,
                parentColumn = "playlistId",
                entityColumn = "trackId",
            ),
    )
    val tracks: List<DetailedTrack>,
)

fun PlaylistWithTracksEntity.asExternalModel(): Playlist {
    return Playlist(
        id = playlist.id,
        name = playlist.playlistName,
        tracks = tracks.map { it.asExternalModel() },
    )
}
