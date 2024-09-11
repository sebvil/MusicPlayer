package com.sebastianvm.musicplayer.features.api.playlist.tracksearch

import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments
import kotlinx.parcelize.Parcelize

@Parcelize data class TrackSearchArguments(val playlistId: Long) : Arguments
