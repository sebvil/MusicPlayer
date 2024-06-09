package com.sebastianvm.musicplayer.model

data class Artist(
    val id: Long,
    val name: String,
    val albums: List<Album>,
    val appearsOn: List<Album>,
)
