package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions
import androidx.room.PrimaryKey

@Entity
data class Playlist(@PrimaryKey val playlistName: String)


@Fts4(contentEntity = Playlist::class, tokenizer = FtsOptions.TOKENIZER_UNICODE61)
@Entity
data class PlaylistFts(val playlistName: String)
