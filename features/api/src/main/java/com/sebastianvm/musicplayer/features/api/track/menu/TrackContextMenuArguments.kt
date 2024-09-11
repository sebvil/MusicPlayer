package com.sebastianvm.musicplayer.features.api.track.menu

import com.sebastianvm.musicplayer.core.model.HasTracks
import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackContextMenuArguments(
    val trackId: Long,
    val trackPositionInList: Int,
    val trackList: HasTracks,
) : Arguments
