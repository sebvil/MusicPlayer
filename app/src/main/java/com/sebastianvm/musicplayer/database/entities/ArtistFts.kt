package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

@Fts4(contentEntity = ArtistEntity::class, tokenizer = FtsOptions.TOKENIZER_UNICODE61)
@Entity
data class ArtistFts(val name: String)
