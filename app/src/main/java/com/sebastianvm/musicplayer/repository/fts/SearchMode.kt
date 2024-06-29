package com.sebastianvm.musicplayer.repository.fts

import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.core.resources.RString

enum class SearchMode(@StringRes val res: Int) {
    TRACKS(RString.songs),
    ARTISTS(RString.artists),
    ALBUMS(RString.albums),
    GENRES(RString.genres),
    PLAYLISTS(RString.playlists)
}
