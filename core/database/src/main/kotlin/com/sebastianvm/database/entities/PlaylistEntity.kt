package com.sebastianvm.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity data class PlaylistEntity(@PrimaryKey val id: Long, val playlistName: String)
