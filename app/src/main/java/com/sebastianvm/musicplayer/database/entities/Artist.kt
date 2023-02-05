package com.sebastianvm.musicplayer.database.entities

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sebastianvm.musicplayer.player.buildMediaItem

@Entity
data class Artist(
    @PrimaryKey
    val id: Long = 0,
    val artistName: String,
) : MediaModel {
    override fun toMediaItem(): MediaItem {
        return buildMediaItem(
            title = artistName,
            mediaId = hashCode(),
            isPlayable = false,
            folderType = MediaMetadata.FOLDER_TYPE_ALBUMS,
            subtitle = null,
            album = null,
            artist = artistName,
            genre = null,
            sourceUri = null,
        )
    }
}

