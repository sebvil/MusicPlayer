package com.sebastianvm.model

data class Artist(
    val id: Long,
    val name: String,
    val albums: List<AlbumWithArtists>,
    val appearsOn: List<AlbumWithArtists>,
)
