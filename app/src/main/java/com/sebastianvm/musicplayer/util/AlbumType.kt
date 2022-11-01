package com.sebastianvm.musicplayer.util

import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.R

enum class AlbumType(@StringRes val sectionName: Int) {
    ALBUM(R.string.albums),
    APPEARS_ON(R.string.appears_on)
}