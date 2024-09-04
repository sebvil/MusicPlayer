package com.sebastianvm.musicplayer.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface MediaGroup {

    @Parcelize data object AllTracks : TrackList

    @Parcelize data class SingleTrack(val trackId: Long) : HasTracks, HasArtists

    data class Artist(val artistId: Long) : MediaGroup

    @Parcelize data class Album(val albumId: Long) : TrackList, HasArtists

    @Parcelize data class Genre(val genreId: Long) : TrackList

    @Parcelize data class Playlist(val playlistId: Long) : TrackList
}

sealed interface HasTracks : MediaGroup, Parcelable

sealed interface HasArtists : MediaGroup, Parcelable

sealed interface TrackList : HasTracks
