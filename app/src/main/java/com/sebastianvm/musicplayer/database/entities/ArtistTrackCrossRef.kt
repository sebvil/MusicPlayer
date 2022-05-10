package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity

@Entity(primaryKeys = ["artistId", "trackId"])
data class ArtistTrackCrossRef(
    val artistId: Long,
    val artistName: String,
    val trackId: Long,
    val trackName: String,
)
