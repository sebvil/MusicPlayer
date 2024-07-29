package com.sebastianvm.musicplayer.features.api.track.menu

import com.sebastianvm.musicplayer.core.model.HasTracks

data class TrackContextMenuArguments(
    val trackId: Long,
    val trackPositionInList: Int,
    val trackList: HasTracks,
)
