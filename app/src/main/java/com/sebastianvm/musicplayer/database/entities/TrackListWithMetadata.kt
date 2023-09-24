package com.sebastianvm.musicplayer.database.entities

data class TrackListWithMetadata(
    val metaData: TrackListMetadata?,
    val trackList: List<Track>
)
