package com.sebastianvm.musicplayer.features.api.playlist.menu

import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments
import kotlinx.parcelize.Parcelize

@Parcelize data class PlaylistContextMenuArguments(val playlistId: Long) : Arguments
