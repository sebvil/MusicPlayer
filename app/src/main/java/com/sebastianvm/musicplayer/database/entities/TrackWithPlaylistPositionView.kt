package com.sebastianvm.musicplayer.database.entities

import androidx.room.DatabaseView

@DatabaseView(
    "SELECT TrackEntity.id, TrackEntity.trackName, TrackEntity.artists, TrackEntity.albumName, PlaylistTrackCrossRef.position, PlaylistTrackCrossRef.playlistId " +
        "FROM TrackEntity JOIN PlaylistTrackCrossRef ON TrackEntity.id=PlaylistTrackCrossRef.trackId"
)
data class TrackWithPlaylistPositionView(
    val id: Long,
    val trackName: String,
    val artists: String,
    val albumName: String,
    val position: Long,
    val playlistId: Long,
)
