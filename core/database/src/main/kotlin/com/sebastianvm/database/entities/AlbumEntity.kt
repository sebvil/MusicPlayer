package com.sebastianvm.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlbumEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val year: Long,
    val artists: String,
    val imageUri: String,
)
