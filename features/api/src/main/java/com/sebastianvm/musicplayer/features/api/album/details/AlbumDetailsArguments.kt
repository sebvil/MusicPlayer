package com.sebastianvm.musicplayer.features.api.album.details

data class AlbumDetailsArguments(
    val albumId: Long,
    val albumName: String,
    val imageUri: String,
    val artists: String?,
)
