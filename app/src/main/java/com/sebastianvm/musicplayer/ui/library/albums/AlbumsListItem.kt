package com.sebastianvm.musicplayer.ui.library.albums

import com.sebastianvm.commons.util.ListItem
import com.sebastianvm.musicplayer.ui.components.AlbumRowState

class AlbumsListItem(
    val albumGid: String,
    val albumRowState: AlbumRowState,
) : ListItem {
    override val gid = albumGid
}