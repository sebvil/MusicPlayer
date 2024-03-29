package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Track(
    @PrimaryKey
    val id: Long,
    val trackName: String,
    val trackNumber: Long,
    val trackDurationMs: Long,
    val albumName: String,
    val albumId: Long,
    val artists: String,
    val path: String
)

data class BasicTrack(
    val id: Long,
    val trackName: String,
    val artists: String,
    val trackNumber: Long
)
