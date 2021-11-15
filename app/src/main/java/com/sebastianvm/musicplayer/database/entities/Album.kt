package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Album(
    @PrimaryKey
    val albumGid: String,
    val albumName: String,
    val year: Long,
    val albumArtPath: String, // Uri to track in album to load thumbnail
    val numberOfTracks: Long,
)

