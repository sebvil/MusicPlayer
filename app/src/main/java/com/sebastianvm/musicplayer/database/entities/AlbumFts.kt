package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

@Fts4(
    contentEntity = AlbumsForArtist::class,
    notIndexed = ["albumId"],
    tokenizer = FtsOptions.TOKENIZER_UNICODE61,
)
@Entity
data class AlbumFts(val albumId: String, val albumName: String, val artistName: String)
