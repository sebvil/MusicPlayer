package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity
data class Track(
    @PrimaryKey
    val trackGid: String,
    val trackName: String,
    val trackNumber: Long,
    val trackDurationMs: Long,
    val albumGid: String,
)

@Fts4(contentEntity = Track::class)
@Entity
data class TrackFts(val trackGid: String, val trackName: String)