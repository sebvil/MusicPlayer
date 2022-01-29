package com.sebastianvm.musicplayer.ui.library.albums

import android.net.Uri
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder

class AlbumsListStatePreviewParameterProvider : PreviewParameterProvider<AlbumsListState> {
    override val values = sequenceOf(
        AlbumsListState(
            albumsList = listOf(
                AlbumRowState(
                    albumId = "0",
                    albumName = "Ahora",
                    imageUri = "",
                    artists = "Melendi",
                    year = 2017
                ),
                AlbumRowState(
                    albumId = "1",
                    albumName = "VIVES",
                    imageUri = "",
                    artists = "Carlos Vives",
                    year = 2018
                ),
                AlbumRowState(
                    albumId = "2",
                    albumName = "Balas perdidas",
                    imageUri = "",
                    artists = "Morat",
                    year = 2019

                ),
                AlbumRowState(
                    albumId = "3",
                    albumName = "LongName".repeat(10),
                    imageUri = "",
                    artists = "LongName".repeat(10),
                    year = 2014
                ),
            ),
            currentSort = SortOption.ALBUM_NAME,
            sortOrder = SortOrder.ASCENDING
        )
    )
}
