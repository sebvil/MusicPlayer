package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MediaQueue(@PrimaryKey(autoGenerate = true) val queueId: Long)


