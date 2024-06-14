package com.sebastianvm.musicplayer.database.entities

import com.sebastianvm.musicplayer.model.BasicTrack

data class BasicTrackEntity(val id: Long, val trackName: String, val artists: String)

fun BasicTrackEntity.asExternalModel() = BasicTrack(id, trackName, artists)
