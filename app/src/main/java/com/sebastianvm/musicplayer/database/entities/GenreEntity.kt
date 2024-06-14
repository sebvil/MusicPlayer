package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sebastianvm.musicplayer.model.BasicGenre

@Entity data class GenreEntity(@PrimaryKey val id: Long, val name: String)

fun GenreEntity.asExternalModel(): BasicGenre {
    return BasicGenre(id = id, name = name)
}
