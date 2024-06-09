package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sebastianvm.musicplayer.model.Genre

@Entity data class GenreEntity(@PrimaryKey val id: Long, val name: String)

fun GenreEntity.asExternalModel(): Genre {
    return Genre(id = id, name = name)
}
