package com.sebastianvm.musicplayer.ui.library.albumlist

import android.net.Uri
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.util.sort.SortOptions
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences

class AlbumListStatePreviewParameterProvider : PreviewParameterProvider<AlbumListState> {
    override val values = sequenceOf(
        AlbumListState(
            albumList = listOf(
                AlbumRowState(
                    albumId = 0,
                    albumName = "Ahora",
                    imageUri = Uri.EMPTY,
                    artists = "Melendi",
                    year = 2017
                ),
                AlbumRowState(
                    albumId = 1,
                    albumName = "VIVES",
                    imageUri = Uri.EMPTY,
                    artists = "Carlos Vives",
                    year = 2018
                ),
                AlbumRowState(
                    albumId = 2,
                    albumName = "Balas perdidas",
                    imageUri = Uri.EMPTY,
                    artists = "Morat",
                    year = 2019

                ),
                AlbumRowState(
                    albumId = 3,
                    albumName = "LongName".repeat(10),
                    imageUri = Uri.EMPTY,
                    artists = "LongName".repeat(10),
                    year = 2014
                ),
            ),
            sortPreferences = MediaSortPreferences(sortOption = SortOptions.AlbumListSortOptions.ALBUM)
        )
    )
}
