package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Album(
    @PrimaryKey
    val id: Long,
    val albumName: String,
    val year: Long,
    val artists: String,
    val imageUri: String
)

data class BasicAlbum(val id: Long, val albumName: String, val imageUri: String)
