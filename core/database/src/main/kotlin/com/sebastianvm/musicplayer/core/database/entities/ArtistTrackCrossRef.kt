package com.sebastianvm.musicplayer.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["artistId", "trackId"])
data class ArtistTrackCrossRef(
    val artistId: Long,
    val artistName: String,
    @ColumnInfo(index = true) val trackId: Long,
    val trackName: String,
)
