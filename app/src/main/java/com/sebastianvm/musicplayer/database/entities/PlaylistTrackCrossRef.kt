package com.sebastianvm.musicplayer.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["playlistName", "trackId"])
data class PlaylistTrackCrossRef(
    val playlistName: String,
    @ColumnInfo(index = true)
    val trackId: String,
)

data class PlaylistWithTracks(
    @Embedded
    val genre: Playlist,
    @Relation(
        parentColumn = "playlistName",
        entityColumn = "trackId",
        associateBy = Junction(PlaylistTrackCrossRef::class)
    )
    val tracks: List<Track>
)
