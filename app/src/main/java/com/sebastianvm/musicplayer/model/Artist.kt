package com.sebastianvm.musicplayer.model

data class Artist(
    val id: Long,
    val name: String,
    val albums: List<AlbumWithArtists>,
    val appearsOn: List<AlbumWithArtists>,
)
