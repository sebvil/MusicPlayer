package com.sebastianvm.musicplayer.services.features.track.menu

import com.sebastianvm.musicplayer.core.model.HasTracks
import com.sebastianvm.musicplayer.services.features.mvvm.Arguments

data class TrackContextMenuArguments(
    val trackId: Long,
    val trackPositionInList: Int,
    val trackList: HasTracks,
) : Arguments
