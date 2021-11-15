package com.sebastianvm.musicplayer.ui.library.albums

import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.commons.util.ListItem

class AlbumsListItem(
    val albumGid: String,
    val albumRowState: AlbumRowState,
) : ListItem {
    override val gid = albumGid
}