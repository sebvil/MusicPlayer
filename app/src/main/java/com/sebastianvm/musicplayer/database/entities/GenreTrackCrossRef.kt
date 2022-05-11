package com.sebastianvm.musicplayer.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["genreId", "trackId"])
data class GenreTrackCrossRef(
    val genreId: Long,
    @ColumnInfo(index = true)
    val trackId: Long,
)
