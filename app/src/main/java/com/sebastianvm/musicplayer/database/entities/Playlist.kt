package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions
import androidx.room.PrimaryKey

// TODO use playlist name hash to generate id
@Entity
data class Playlist(@PrimaryKey(autoGenerate = true) val id: Long, val playlistName: String)


@Fts4(contentEntity = Playlist::class, tokenizer = FtsOptions.TOKENIZER_UNICODE61)
@Entity
data class PlaylistFts(val id: Long, val playlistName: String)
