package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Artist(
    @PrimaryKey
    val artistGid: String,
    val artistName: String,
)
