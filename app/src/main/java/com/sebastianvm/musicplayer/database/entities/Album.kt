package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity
data class Album(
    @PrimaryKey
    val albumId: String,
    val albumName: String,
    val year: Long,
    val albumArtPath: String, // Uri to track in album to load thumbnail
    val numberOfTracks: Long,
)

@Fts4(contentEntity = Album::class)
@Entity
data class AlbumFts(val albumId: String, val albumName: String)