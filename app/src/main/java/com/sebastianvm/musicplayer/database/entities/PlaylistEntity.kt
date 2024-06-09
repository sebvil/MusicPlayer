package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sebastianvm.musicplayer.model.Playlist

@Entity data class PlaylistEntity(@PrimaryKey val id: Long, val playlistName: String)

fun PlaylistEntity.asExternalModel(): Playlist {
    return Playlist(id = id, name = playlistName)
}
