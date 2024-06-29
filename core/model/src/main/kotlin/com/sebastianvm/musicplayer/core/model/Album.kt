package com.sebastianvm.musicplayer.core.model

data class Album(
    val id: Long,
    val title: String,
    val imageUri: String,
    val year: Long,
    val artists: List<BasicArtist>,
    val tracks: List<Track>,
)
