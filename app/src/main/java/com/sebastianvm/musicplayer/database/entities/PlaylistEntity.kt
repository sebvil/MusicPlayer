package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sebastianvm.model.BasicPlaylist

@Entity data class PlaylistEntity(@PrimaryKey val id: Long, val playlistName: String)

fun PlaylistEntity.asExternalModel(): BasicPlaylist {
    return BasicPlaylist(id = id, name = playlistName)
}
