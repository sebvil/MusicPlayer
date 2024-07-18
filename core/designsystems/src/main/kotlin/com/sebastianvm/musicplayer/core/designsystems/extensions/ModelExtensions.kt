package com.sebastianvm.musicplayer.core.designsystems.extensions

import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.core.model.SortOptions
import com.sebastianvm.musicplayer.core.resources.RString

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
