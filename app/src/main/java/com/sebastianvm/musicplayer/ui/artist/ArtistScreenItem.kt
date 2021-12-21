package com.sebastianvm.musicplayer.ui.artist

import androidx.annotation.StringRes
import com.sebastianvm.commons.util.ListItem
import com.sebastianvm.musicplayer.ui.components.AlbumRowState

sealed class ArtistScreenItem : ListItem {

    data class AlbumRowItem(
        val albumId: String,
        val state: AlbumRowState,
    ) : ArtistScreenItem() {
        override val id = albumId
    }

    data class SectionHeaderItem(val sectionType: String, @StringRes val sectionName: Int) :
        ArtistScreenItem() {
        override val id = sectionType
    }
}