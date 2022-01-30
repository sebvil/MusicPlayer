package com.sebastianvm.musicplayer.database.entities

import androidx.room.Entity
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType

@Entity(primaryKeys = ["mediaGroupType", "groupMediaId"])
data class MediaQueue(
    val mediaGroupType: MediaGroupType,
    val groupMediaId: String,
    val queueName: String
) {
    fun toMediaGroup(): MediaGroup {
        return MediaGroup(mediaGroupType, groupMediaId)
    }
}

