package com.sebastianvm.musicplayer.ui.artist

import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.util.AlbumType

sealed class ArtistScreenItem {
    abstract val id: Any

    data class AlbumRowItem(
        val state: ModelListItemState,
    ) : ArtistScreenItem() {
        override val id = state.id
    }

    data class SectionHeaderItem(val sectionType: AlbumType) :
        ArtistScreenItem() {
        override val id = sectionType.name
    }
}