package com.sebastianvm.musicplayer.ui.artist

import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.util.AlbumType

sealed class ArtistScreenItem {
    abstract val id: Any

    data class AlbumRowItem(
        val state: ModelListItemState,
    ) : ArtistScreenItem() {
        override val id = state.id
    }

    data class SectionHeaderItem(val sectionType: AlbumType, @StringRes val sectionName: Int) :
        ArtistScreenItem() {
        override val id = sectionType.name
    }
}