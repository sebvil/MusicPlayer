package com.sebastianvm.musicplayer.ui.artist

import androidx.annotation.StringRes
import com.sebastianvm.commons.util.ListItem
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.util.AlbumType

sealed class ArtistScreenItem : ListItem {

    data class AlbumRowItem(
        val state: AlbumRowState,
    ) : ArtistScreenItem() {
        override val id = state.albumId
    }

    data class SectionHeaderItem(val sectionType: AlbumType, @StringRes val sectionName: Int) :
        ArtistScreenItem() {
        override val id = sectionType.name
    }
}