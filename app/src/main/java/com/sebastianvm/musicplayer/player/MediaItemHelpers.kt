package com.sebastianvm.musicplayer.player

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

fun buildMediaItem(
    title: String,
    mediaId: Int,
    isPlayable: Boolean,
    @MediaMetadata.FolderType folderType: Int,
    subtitle: String? = null,
    album: String? = null,
    artist: String? = null,
    genre: String? = null,
    sourceUri: Uri? = null,
    artworkUri: Uri? = null
): MediaItem {
    val metadata =
        MediaMetadata.Builder()
            .setAlbumTitle(album)
            .setTitle(title)
            .setSubtitle(subtitle)
            .setArtist(artist)
            .setGenre(genre)
            .setFolderType(folderType)
            .setIsPlayable(isPlayable)
            .setArtworkUri(artworkUri)
            .build()
    return MediaItem.Builder()
        .setMediaId(mediaId.toString())
        .setMediaMetadata(metadata)
        .setUri(sourceUri)
        .build()
}