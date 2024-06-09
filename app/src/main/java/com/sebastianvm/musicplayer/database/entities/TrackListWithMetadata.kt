package com.sebastianvm.musicplayer.database.entities

import com.sebastianvm.musicplayer.model.Track

data class TrackListWithMetadata(val metaData: TrackListMetadata?, val trackList: List<Track>)
