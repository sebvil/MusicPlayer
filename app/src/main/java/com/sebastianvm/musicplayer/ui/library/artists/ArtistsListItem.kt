package com.sebastianvm.musicplayer.ui.library.artists

import com.sebastianvm.commons.util.ListItem

class ArtistsListItem(val artistGid: String, val artistName: String) : ListItem {
    override val gid = artistGid
}