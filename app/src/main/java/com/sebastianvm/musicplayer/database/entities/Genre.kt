package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity
data class Genre(
    @PrimaryKey
    val genreName: String
)

@Fts4(contentEntity = Genre::class)
@Entity
data class GenreFts(val genreName: String)
