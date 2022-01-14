package com.sebastianvm.musicplayer.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["artistName", "trackId"])
data class ArtistTrackCrossRef(
    val artistName: String,
    @ColumnInfo(index = true)
    val trackId: String,
    val trackName: String,
)
