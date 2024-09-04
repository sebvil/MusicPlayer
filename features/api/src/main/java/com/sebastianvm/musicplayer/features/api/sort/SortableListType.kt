package com.sebastianvm.musicplayer.features.api.sort

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface SortableListType : Parcelable {
    @Parcelize data object AllTracks : SortableListType

    @Parcelize data class Genre(val genreId: Long) : SortableListType

    @Parcelize data object Albums : SortableListType

    @Parcelize data class Playlist(val playlistId: Long) : SortableListType
}
