package com.sebastianvm.musicplayer.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlaylistEntity(@PrimaryKey(autoGenerate = true) val id: Long, val playlistName: String)
