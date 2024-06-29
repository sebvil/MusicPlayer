package com.sebastianvm.musicplayer.util.extensions

import androidx.annotation.StringRes
import com.sebastianvm.model.SortOptions
import com.sebastianvm.resources.RString

@get:StringRes
val SortOptions.stringId: Int
    get() =
        when (this) {
            SortOptions.Album -> RString.album_name
            SortOptions.Artist -> RString.artist_name
            SortOptions.Year -> RString.year
            SortOptions.Custom -> RString.custom
            SortOptions.Track -> RString.track_name
        }
