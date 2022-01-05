package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity
data class Artist(
    @PrimaryKey
    val artistId: String,
    val artistName: String,
)

@Fts4(contentEntity = Artist::class)
@Entity
data class ArtistFts(val artistId: String, val artistName: String)
