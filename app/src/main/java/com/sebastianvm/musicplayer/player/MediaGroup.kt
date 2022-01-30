package com.sebastianvm.musicplayer.player

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class MediaGroupType {
    ALL_TRACKS,
    SINGLE_TRACK,
    ARTIST,
    ALBUM,
    GENRE,
    PLAYLIST,
    UNKNOWN,
}

@Parcelize
data class MediaGroup(val mediaGroupType: MediaGroupType, val mediaId: String) : Parcelable
