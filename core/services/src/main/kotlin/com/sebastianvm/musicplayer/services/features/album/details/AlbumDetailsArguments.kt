package com.sebastianvm.musicplayer.services.features.album.details

import com.sebastianvm.musicplayer.services.features.mvvm.Arguments

data class AlbumDetailsArguments(
    val albumId: Long,
    val albumName: String,
    val imageUri: String,
    val artists: String?,
) : Arguments
