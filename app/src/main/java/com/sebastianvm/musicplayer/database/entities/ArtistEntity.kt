package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sebastianvm.musicplayer.model.BasicArtist

@Entity data class ArtistEntity(@PrimaryKey val id: Long = 0, val name: String)

fun ArtistEntity.asExternalModel() = BasicArtist(id = id, name = name)
