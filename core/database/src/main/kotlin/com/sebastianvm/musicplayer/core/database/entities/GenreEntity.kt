package com.sebastianvm.musicplayer.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity data class GenreEntity(@PrimaryKey val id: Long, val name: String)
