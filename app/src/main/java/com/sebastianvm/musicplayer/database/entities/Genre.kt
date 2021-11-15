package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Genre(
    @PrimaryKey
    val genreName: String
)
