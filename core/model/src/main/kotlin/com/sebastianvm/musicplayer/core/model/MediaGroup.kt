package com.sebastianvm.musicplayer.core.model

sealed interface MediaGroup {

    data object AllTracks : TrackList

    data class SingleTrack(val trackId: Long) : HasTracks, HasArtists

    data class Artist(val artistId: Long) : MediaGroup

    data class Album(val albumId: Long) : TrackList, HasArtists

    data class Genre(val genreId: Long) : TrackList

    data class Playlist(val playlistId: Long) : TrackList
}

sealed interface HasTracks : MediaGroup

sealed interface HasArtists : MediaGroup

sealed interface TrackList : HasTracks
