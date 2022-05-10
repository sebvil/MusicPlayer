package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions
import androidx.room.PrimaryKey

@Entity
data class Album(
    @PrimaryKey
    val albumId: Long,
    val albumName: String,
    val year: Long,
    val artists: String
)

@Fts4(contentEntity = AlbumsForArtist::class, notIndexed=["albumId"], tokenizer = FtsOptions.TOKENIZER_UNICODE61)
@Entity
data class AlbumFts(val albumId: String, val albumName: String, val artistName: String)