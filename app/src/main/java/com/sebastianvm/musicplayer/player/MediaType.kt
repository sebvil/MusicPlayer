package com.sebastianvm.musicplayer.player

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class MediaType : Parcelable {
    ALL_TRACKS,
    ARTIST,
    ALBUM,
    GENRE,
    SINGLE_TRACK,
    PLAYLIST,
    UNKNOWN
}