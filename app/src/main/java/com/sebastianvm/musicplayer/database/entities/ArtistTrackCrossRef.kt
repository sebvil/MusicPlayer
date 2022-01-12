package com.sebastianvm.musicplayer.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["artistId", "trackId"])
data class ArtistTrackCrossRef(
    val artistId: String,
    @ColumnInfo(index = true)
    val trackId: String,
    val trackName: String,
    val artistName: String
)

