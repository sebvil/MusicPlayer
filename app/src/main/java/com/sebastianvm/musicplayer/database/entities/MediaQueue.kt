package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType

@Entity(primaryKeys = ["mediaType", "groupMediaId"])
data class MediaQueue(
    val mediaType: MediaType,
    val groupMediaId: String,
    val queueName: String
) {
    fun toMediaGroup(): MediaGroup {
        return MediaGroup(mediaType, groupMediaId)
    }
}

