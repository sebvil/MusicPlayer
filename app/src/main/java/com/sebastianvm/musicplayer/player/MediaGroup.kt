package com.sebastianvm.musicplayer.player

import android.os.Parcelable
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
sealed interface MediaGroup : Parcelable {
    @Serializable
    @Parcelize
    object AllTracks : TrackList

    @Serializable
    @Parcelize
    data class SingleTrack(val trackId: Long) : MediaGroup

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
sealed interface TrackList : MediaGroup

fun TrackList.toSortableListType(): SortableListType {
    return when (this) {
        is MediaGroup.AllTracks -> SortableListType.Tracks(trackList = this)
        is MediaGroup.Genre -> SortableListType.Tracks(trackList = this)
        is MediaGroup.Playlist -> SortableListType.Playlist(playlistId = this.playlistId)
        is MediaGroup.Album -> throw IllegalStateException("Cannot sort album")
    }
}
