package com.sebastianvm.musicplayer.database.entities

import com.sebastianvm.musicplayer.ui.components.MediaArtImageState

data class TrackListMetadata(
    val trackListName: String? = null,
    val mediaArtImageState: MediaArtImageState? = null
)
