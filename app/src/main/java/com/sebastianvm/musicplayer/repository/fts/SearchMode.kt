package com.sebastianvm.musicplayer.repository.fts

import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.R


enum class SearchMode(@StringRes val res: Int) {
    SONGS(R.string.songs),
    ARTISTS(R.string.artists),
    ALBUMS(R.string.albums),
    GENRES(R.string.genres),
    PLAYLISTS(R.string.playlists)
}