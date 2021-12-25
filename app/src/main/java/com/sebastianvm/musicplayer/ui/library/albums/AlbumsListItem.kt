package com.sebastianvm.musicplayer.ui.library.albums

import com.sebastianvm.commons.util.ListItem
import com.sebastianvm.musicplayer.ui.components.AlbumRowState


// TODO combine with albumRowItem
class AlbumsListItem(
    val albumId: String,
    val albumRowState: AlbumRowState,
) : ListItem {
    override val id = albumId
}