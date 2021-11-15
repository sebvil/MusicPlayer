package com.sebastianvm.musicplayer.ui.artist

import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.commons.util.ListItem
import com.sebastianvm.musicplayer.util.AlbumType

sealed class ArtistScreenItem : ListItem {

    data class AlbumRowItem(
        val albumGid: String,
        val albumType: AlbumType,
        val state: AlbumRowState,
    ) : ArtistScreenItem() {
        override val gid = albumGid
    }

    data class SectionHeaderItem(val sectionType: String, @StringRes val sectionName: Int) :
        ArtistScreenItem() {
        override val gid = sectionType
    }
}