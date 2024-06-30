package com.sebastianvm.musicplayer.core.database.entities

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

@Fts4(contentEntity = PlaylistEntity::class, tokenizer = FtsOptions.TOKENIZER_UNICODE61)
@Entity
data class PlaylistFts(val id: Long, val playlistName: String)
