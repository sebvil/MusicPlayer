package com.sebastianvm.musicplayer.model

data class Track(val id: Long, val name: String, val artists: List<BasicArtist>, val albumId: Long)
