package com.sebastianvm.musicplayer.features.api.album.details

import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlbumDetailsArguments(
    val albumId: Long,
    val albumName: String,
    val imageUri: String,
    val artists: String?,
) : Arguments
