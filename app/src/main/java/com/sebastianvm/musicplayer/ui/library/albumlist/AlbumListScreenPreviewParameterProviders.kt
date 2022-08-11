package com.sebastianvm.musicplayer.ui.library.albumlist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions

class AlbumListStatePreviewParameterProvider : PreviewParameterProvider<AlbumListState> {
    override val values = sequenceOf(
        AlbumListState(
            albumList = listOf(
                AlbumRowState(
                    albumId = 0,
                    albumName = "Ahora",
                    imageUri = "",
                    artists = "Melendi",
                    year = 2017
                ),
                AlbumRowState(
                    albumId = 1,
                    albumName = "VIVES",
                    imageUri = "",
                    artists = "Carlos Vives",
                    year = 2018
                ),
                AlbumRowState(
                    albumId = 2,
                    albumName = "Balas perdidas",
                    imageUri = "",
                    artists = "Morat",
                    year = 2019

                ),
                AlbumRowState(
                    albumId = 3,
                    albumName = "LongName".repeat(10),
                    imageUri = "",
                    artists = "LongName".repeat(10),
                    year = 2014
                ),
            ),
            sortPreferences = MediaSortPreferences(sortOption = SortOptions.AlbumListSortOptions.ALBUM)
        )
    )
}
