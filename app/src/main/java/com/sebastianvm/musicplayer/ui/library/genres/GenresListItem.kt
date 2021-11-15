package com.sebastianvm.musicplayer.ui.library.genres

import com.sebastianvm.commons.util.ListItem

data class GenresListItem(val genreName: String) : ListItem {
    override val gid = genreName
}

