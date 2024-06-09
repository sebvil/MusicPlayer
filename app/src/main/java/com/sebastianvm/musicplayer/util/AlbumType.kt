package com.sebastianvm.musicplayer.util

import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.util.resources.RString

enum class AlbumType(@StringRes val sectionName: Int) {
    ALBUM(RString.albums),
    APPEARS_ON(RString.appears_on)
}
