package com.sebastianvm.musicplayer.model

data class Album(
    val id: Long,
    val title: String,
    val artists: List<BasicArtist>,
    val imageUri: String,
    val year: Long,
)
