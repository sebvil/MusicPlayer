package com.sebastianvm.musicplayer.ui.library.albums

import android.net.Uri
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder

class AlbumsListStatePreviewParameterProvider : PreviewParameterProvider<AlbumsListState> {
    override val values = sequenceOf(
        AlbumsListState(
            albumsList = listOf(
                AlbumRowState(
                    albumId = "0",
                    albumName = "Ahora",
                    imageUri = Uri.EMPTY,
                    artists = "Melendi",
                    year = 2017
                ),
                AlbumRowState(
                    albumId = "1",
                    albumName = "VIVES",
                    imageUri = Uri.EMPTY,
                    artists = "Carlos Vives",
                    year = 2018
                ),
                AlbumRowState(
                    albumId = "2",
                    albumName = "Balas perdidas",
                    imageUri = Uri.EMPTY,
                    artists = "Morat",
                    year = 2019

                ),
                AlbumRowState(
                    albumId = "3",
                    albumName = "LongName".repeat(10),
                    imageUri = Uri.EMPTY,
                    artists = "LongName".repeat(10),
                    year = 2014
                ),
            ),
            currentSort = MediaSortOption.ALBUM,
            sortOrder = MediaSortOrder.ASCENDING,
        )
    )
}
