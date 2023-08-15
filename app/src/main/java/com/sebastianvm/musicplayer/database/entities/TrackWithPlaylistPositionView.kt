package com.sebastianvm.musicplayer.database.entities

import androidx.room.DatabaseView

@DatabaseView(
    "SELECT Track.id, Track.trackName, Track.artists, Track.albumName, PlaylistTrackCrossRef.position, PlaylistTrackCrossRef.playlistId " +
        "FROM Track JOIN PlaylistTrackCrossRef ON Track.id=PlaylistTrackCrossRef.trackId"
)
data class TrackWithPlaylistPositionView(
    val id: Long,
    val trackName: String,
    val artists: String,
    val albumName: String,
    val position: Long,
    val playlistId: Long
)
