package com.sebastianvm.musicplayer.features.api.sort

sealed interface SortableListType {
    data object AllTracks : SortableListType

    data class Genre(val genreId: Long) : SortableListType

    data object Albums : SortableListType

    data class Playlist(val playlistId: Long) : SortableListType
}
