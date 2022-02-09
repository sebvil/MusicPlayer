package com.sebastianvm.musicplayer.database.entities

fun track(init: Track.() -> Track): Track = newTrack().init()

private fun newTrack(): Track = Track(
    trackId = "0",
    artists = "",
    albumId = "0",
    trackDurationMs = 0,
    trackName = "",
    albumName = "",
    trackNumber = 0
)
