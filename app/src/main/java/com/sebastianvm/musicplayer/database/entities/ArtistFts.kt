package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

@Fts4(contentEntity = Artist::class, tokenizer = FtsOptions.TOKENIZER_UNICODE61)
@Entity
data class ArtistFts(val artistName: String)
