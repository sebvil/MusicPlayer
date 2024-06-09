package com.sebastianvm.musicplayer.repository.fts

import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.util.resources.RString

enum class SearchMode(@StringRes val res: Int) {
    SONGS(RString.songs),
    ARTISTS(RString.artists),
    ALBUMS(RString.albums),
    GENRES(RString.genres),
    PLAYLISTS(RString.playlists)
}
