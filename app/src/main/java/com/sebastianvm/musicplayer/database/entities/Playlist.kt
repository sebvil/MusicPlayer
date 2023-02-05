package com.sebastianvm.musicplayer.database.entities

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions
import androidx.room.PrimaryKey
import com.sebastianvm.musicplayer.player.buildMediaItem

@Entity
data class Playlist(@PrimaryKey val id: Long, val playlistName: String) : MediaModel {
    override fun toMediaItem(): MediaItem {
        return buildMediaItem(
            title = playlistName,
            mediaId = hashCode(),
            isPlayable = false,
            folderType = MediaMetadata.FOLDER_TYPE_PLAYLISTS,
            subtitle = null,
            album = null,
            artist = null,
            genre = null,
            sourceUri = null,
        )
    }
}


@Fts4(contentEntity = Playlist::class, tokenizer = FtsOptions.TOKENIZER_UNICODE61)
@Entity
data class PlaylistFts(val id: Long, val playlistName: String)
