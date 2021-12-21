package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity
data class Track(
    @PrimaryKey
    val trackId: String,
    val trackName: String,
    val trackNumber: Long,
    val trackDurationMs: Long,
    val albumId: String,
)

@Fts4(contentEntity = Track::class)
@Entity
data class TrackFts(val trackId: String, val trackName: String)