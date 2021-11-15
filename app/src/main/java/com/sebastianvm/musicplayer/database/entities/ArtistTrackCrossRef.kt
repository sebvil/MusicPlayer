package com.sebastianvm.musicplayer.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["artistGid", "trackGid"])
data class ArtistTrackCrossRef(
    val artistGid: String,
    @ColumnInfo(index = true)
    val trackGid: String,
)

