package com.sebastianvm.musicplayer.player

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class MediaType : Parcelable {
    TRACK,
    ARTIST,
    ALBUM,
    GENRE,
    PLAYLIST,
}