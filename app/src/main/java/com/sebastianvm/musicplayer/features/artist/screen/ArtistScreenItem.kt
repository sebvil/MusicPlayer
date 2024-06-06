package com.sebastianvm.musicplayer.features.artist.screen

import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.util.AlbumType

sealed class ArtistScreenItem {
    abstract val id: Any

    data class AlbumRowItem(val state: ModelListItem.State) : ArtistScreenItem() {
        override val id = state.id
    }

    data class SectionHeaderItem(val sectionType: AlbumType) : ArtistScreenItem() {
        override val id = sectionType.name
    }
}
