package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions
import androidx.room.PrimaryKey

@Entity
data class Track(
    @PrimaryKey
    val trackId: String,
    val trackName: String,
    val trackNumber: Long,
    val trackDurationMs: Long,
    val albumName: String,
    val albumId: String,
    val artists: String,
) {
    companion object {
        fun fromId(trackId: String): Track = Track(
            trackId = trackId,
            trackName = "",
            trackNumber = 0,
            trackDurationMs = 0,
            albumName = "",
            albumId = "",
            artists = ""
        )

    }
}

@Fts4(
    contentEntity = ArtistTrackCrossRef::class,
    notIndexed = ["trackId", "artistId"],
    tokenizer = FtsOptions.TOKENIZER_UNICODE61
)
@Entity
data class TrackFts(val trackId: String, val trackName: String, val artistId: Int, val artistName: String)