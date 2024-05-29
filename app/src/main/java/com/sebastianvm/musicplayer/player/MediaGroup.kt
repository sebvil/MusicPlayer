package com.sebastianvm.musicplayer.player

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
sealed interface MediaGroup : Parcelable {
    @Serializable
    @Parcelize
    data object AllTracks : TrackList

    @Serializable
    @Parcelize
    data class SingleTrack(val trackId: Long) : HasTracks

    @Serializable
    @Parcelize
    data class Artist(val artistId: Long) : MediaGroup

    @Serializable
    @Parcelize
    data class Album(val albumId: Long) : TrackList

    @Serializable
    @Parcelize
    data class Genre(val genreId: Long) : TrackList

    @Serializable
    @Parcelize
    data class Playlist(val playlistId: Long) : TrackList
}

@Serializable
@Parcelize
sealed interface HasTracks : MediaGroup

@Serializable
@Parcelize
sealed interface TrackList : HasTracks
