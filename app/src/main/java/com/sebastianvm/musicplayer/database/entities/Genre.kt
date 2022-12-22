package com.sebastianvm.musicplayer.database.entities

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sebastianvm.musicplayer.player.buildMediaItem

@Entity
data class Genre(
    @PrimaryKey
    val id: Long,
    val genreName: String
) : MediaModel {
    override fun toMediaItem(): MediaItem {
        return buildMediaItem(
            title = genreName,
            mediaId = hashCode(),
            isPlayable = false,
            folderType = MediaMetadata.FOLDER_TYPE_GENRES,
            subtitle = null,
            album = null,
            artist = null,
            genre = genreName,
            sourceUri = null,
        )
    }
}

