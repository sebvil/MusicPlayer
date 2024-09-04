package com.sebastianvm.musicplayer.features.api.album.menu

import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments
import kotlinx.parcelize.Parcelize

@Parcelize data class AlbumContextMenuArguments(val albumId: Long) : Arguments
