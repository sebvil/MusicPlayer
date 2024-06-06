package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

@Fts4(
    contentEntity = ArtistTrackCrossRef::class,
    notIndexed = ["trackId", "artistId"],
    tokenizer = FtsOptions.TOKENIZER_UNICODE61,
)
@Entity
data class TrackFts(
    val trackId: Long,
    val trackName: String,
    val artistId: Long,
    val artistName: String,
)
