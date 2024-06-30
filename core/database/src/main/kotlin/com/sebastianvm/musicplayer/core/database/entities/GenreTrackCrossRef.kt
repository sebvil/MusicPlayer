package com.sebastianvm.musicplayer.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["genreId", "trackId"])
data class GenreTrackCrossRef(val genreId: Long, @ColumnInfo(index = true) val trackId: Long)
