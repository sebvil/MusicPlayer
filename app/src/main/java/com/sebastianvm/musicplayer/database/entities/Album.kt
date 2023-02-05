package com.sebastianvm.musicplayer.database.entities

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sebastianvm.musicplayer.ArtworkProvider
import com.sebastianvm.musicplayer.player.buildMediaItem
import com.sebastianvm.musicplayer.util.uri.UriUtils

@Entity
data class Album(
    @PrimaryKey
    val id: Long,
    val albumName: String,
    val year: Long,
    val artists: String,
    val imageUri: String
) : MediaModel {
    override fun toMediaItem(): MediaItem {
        return buildMediaItem(
            title = albumName,
            mediaId = hashCode(),
            isPlayable = false,
            folderType = MediaMetadata.FOLDER_TYPE_TITLES,
            subtitle = artists,
            album = albumName,
            artist = artists,
            genre = null,
            sourceUri = UriUtils.getAlbumUri(albumId = id),
            artworkUri = ArtworkProvider.getUriForAlbum(albumId = id)
        )
    }
}

data class BasicAlbum(val id: Long, val albumName: String, val imageUri: String)