package com.sebastianvm.musicplayer.features.api.playlist.details

import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaylistDetailsArguments(val playlistId: Long, val playlistName: String) : Arguments
