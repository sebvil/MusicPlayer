package com.sebastianvm.musicplayer.features.api.album.details

import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments

data class AlbumDetailsArguments(
    val albumId: Long,
    val albumName: String,
    val imageUri: String,
    val artists: String?,
) : Arguments
