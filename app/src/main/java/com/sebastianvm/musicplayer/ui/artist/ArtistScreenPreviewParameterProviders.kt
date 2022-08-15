package com.sebastianvm.musicplayer.ui.artist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.util.AlbumType

class ArtistStatePreviewParameterProvider : PreviewParameterProvider<ArtistState> {
    override val values = sequenceOf(
        ArtistState(
            artistId = 0,
            artistName = "Melendi",
            albumsForArtistItems = listOf(
                ArtistScreenItem.SectionHeaderItem(
                    AlbumType.ALBUM,
                    R.string.albums
                )
            ).plus(listOf()),
            appearsOnForArtistItems = listOf(
                ArtistScreenItem.SectionHeaderItem(
                    AlbumType.APPEARS_ON,
                    R.string.appears_on
                )
            ).plus(
                listOf()
            ),
        )
    )
}

class ArtistViewItemProvider : PreviewParameterProvider<ArtistScreenItem> {
    override val values = sequenceOf(
        ArtistScreenItem.SectionHeaderItem(AlbumType.ALBUM, R.string.albums),

        )
}
