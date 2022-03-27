package com.sebastianvm.musicplayer.ui.artist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.util.AlbumType

class ArtistStatePreviewParameterProvider : PreviewParameterProvider<ArtistState> {
    override val values = sequenceOf(
        ArtistState(
            artistName = "Melendi",
            albumsForArtistItems = listOf(
                ArtistScreenItem.SectionHeaderItem(
                    AlbumType.ALBUM,
                    R.string.albums
                )
            ).plus(
                listOf(
                    ArtistScreenItem.AlbumRowItem(
                        AlbumRowState(
                            albumId = "0",
                            albumName = "Ahora",
                            imageUri = "",
                            year = 2017,
                            artists = "Melendi"
                        ),
                    ),
                    ArtistScreenItem.AlbumRowItem(
                        AlbumRowState(
                            albumId = "1",
                            albumName = "Ahora",
                            imageUri = "",
                            year = 2017,
                            artists = "Melendi"
                        ),
                    ),
                )
            ),
            appearsOnForArtistItems = listOf(
                ArtistScreenItem.SectionHeaderItem(
                    AlbumType.APPEARS_ON,
                    R.string.appears_on
                )
            ).plus(
                listOf(
                    ArtistScreenItem.AlbumRowItem(
                        AlbumRowState(
                            albumId = "2",
                            albumName = "Ahora",
                            imageUri = "",
                            year = 2017,
                            artists = "Melendi"
                        ),
                    ),
                    ArtistScreenItem.AlbumRowItem(
                        AlbumRowState(
                            albumId = "3",
                            albumName = "Ahora",
                            imageUri = "",
                            year = 2017,
                            artists = "Melendi"
                        ),
                    ),
                )
            ),
        )
    )
}

class ArtistViewItemProvider : PreviewParameterProvider<ArtistScreenItem> {
    override val values = sequenceOf(
        ArtistScreenItem.SectionHeaderItem(AlbumType.ALBUM, R.string.albums),
        ArtistScreenItem.AlbumRowItem(
            AlbumRowState(
                albumId = "0",
                albumName = "Ahora",
                imageUri = "",
                year = 2017,
                artists = "Melendi"
            ),
        )
    )
}
