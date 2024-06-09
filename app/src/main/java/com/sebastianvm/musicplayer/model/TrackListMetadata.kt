package com.sebastianvm.musicplayer.model

import com.sebastianvm.musicplayer.ui.components.MediaArtImageState

data class TrackListMetadata(
    val trackListName: String,
    val mediaArtImageState: MediaArtImageState? = null,
)
