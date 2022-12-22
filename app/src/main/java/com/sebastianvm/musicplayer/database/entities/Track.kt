package com.sebastianvm.musicplayer.database.entities

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sebastianvm.musicplayer.ArtworkProvider
import com.sebastianvm.musicplayer.player.buildMediaItem
import com.sebastianvm.musicplayer.util.uri.UriUtils

@Entity
data class Track(
    @PrimaryKey
    val id: Long,
    val trackName: String,
    val trackNumber: Long,
    val trackDurationMs: Long,
    val albumName: String,
    val albumId: Long,
    val artists: String,
    val path: String
) : MediaModel {

    override fun toMediaItem(): MediaItem {
        return buildMediaItem(
            title = trackName,
            mediaId = id.toInt(),
            isPlayable = true,
            folderType = MediaMetadata.FOLDER_TYPE_NONE,
            album = albumName,
            subtitle = artists,
            artist = artists,
            genre = "",
            sourceUri = UriUtils.getTrackUri(trackId = id),
            artworkUri = ArtworkProvider.getUriForTrack(albumId = albumId)
        )
    }
}

data class BasicTrack(
    val id: Long,
    val trackName: String,
    val artists: String,
    val trackNumber: Long
)