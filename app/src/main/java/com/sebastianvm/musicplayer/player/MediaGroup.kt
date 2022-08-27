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
}

// used for MediaGroups that are TrackLists
enum class TrackListType {
    ALL_TRACKS,
    GENRE,
    PLAYLIST,
    ALBUM;

    fun toMediaGroupType(): MediaGroupType {
        return when (this) {
            ALL_TRACKS -> MediaGroupType.ALL_TRACKS
            GENRE -> MediaGroupType.GENRE
            PLAYLIST -> MediaGroupType.PLAYLIST
            ALBUM -> MediaGroupType.ALBUM
        }
    }
}


// TODO? use sealed class
@Serializable
@Parcelize
data class MediaGroup(val mediaGroupType: MediaGroupType, val mediaId: Long) : Parcelable
