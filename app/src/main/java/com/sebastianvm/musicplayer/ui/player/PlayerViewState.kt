package com.sebastianvm.musicplayer.ui.player

import com.sebastianvm.musicplayer.ui.components.MediaArtImageState

data class PlayerViewState(
    val mediaArtImageState: MediaArtImageState,
    val trackInfoState: TrackInfoState,
    val playbackControlsState: PlaybackControlsState
)
