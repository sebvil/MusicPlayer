package com.sebastianvm.musicplayer.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity data class ArtistEntity(@PrimaryKey val id: Long = 0, val name: String)
