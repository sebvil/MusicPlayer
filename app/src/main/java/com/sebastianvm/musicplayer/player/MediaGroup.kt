package com.sebastianvm.musicplayer.player

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

enum class MediaGroupType {
    ALL_TRACKS,
    SINGLE_TRACK,
    ARTIST,
    ALBUM,
    GENRE,
    PLAYLIST,
    UNKNOWN,
}

// used for MediaGroups that are TrackLists
enum class TrackListType {
    ALL_TRACKS,
    GENRE,
}


// TODO? use sealed class
@Serializable
@Parcelize
data class MediaGroup(val mediaGroupType: MediaGroupType, val mediaId: Long) : Parcelable
