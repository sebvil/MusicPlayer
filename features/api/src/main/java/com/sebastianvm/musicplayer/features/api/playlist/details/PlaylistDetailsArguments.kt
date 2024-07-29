package com.sebastianvm.musicplayer.features.api.playlist.details

import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments

data class PlaylistDetailsArguments(val playlistId: Long, val playlistName: String) : Arguments
