package com.sebastianvm.musicplayer.ui.artist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.commons.util.MediaArt
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.AlbumRowState

class ArtistStatePreviewParameterProvider : PreviewParameterProvider<ArtistState> {
    override val values = sequenceOf(
        ArtistState(
            artistName = "Melendi",
            albumsForArtistItems = listOf(
                ArtistScreenItem.SectionHeaderItem(
                    ArtistViewModel.ALBUMS,
                    R.string.albums
                )
            ).plus(
                listOf(
                    ArtistScreenItem.AlbumRowItem(
                        AlbumRowState(
                            albumId = "0",
                            albumName = "Ahora",
                            image = MediaArt(
                                uris = listOf(),
                                contentDescription = DisplayableString.StringValue(""),
                                backupResource = com.sebastianvm.commons.R.drawable.ic_album,
                                backupContentDescription = DisplayableString.StringValue("Album art placeholder")
                            ),
                            year = 2017,
                            artists = "Melendi"
                        ),
                    ),
                    ArtistScreenItem.AlbumRowItem(
                        AlbumRowState(
                            albumId = "1",
                            albumName = "Ahora",
                            image = MediaArt(
                                uris = listOf(),
                                contentDescription = DisplayableString.StringValue(""),
                                backupResource = com.sebastianvm.commons.R.drawable.ic_album,
                                backupContentDescription = DisplayableString.StringValue("Album art placeholder")
                            ),
                            year = 2017,
                            artists = "Melendi"
                        ),
                    ),
                )
            ),
            appearsOnForArtistItems = listOf(
                ArtistScreenItem.SectionHeaderItem(
                    ArtistViewModel.APPEARS_ON,
                    R.string.appears_on
                )
            ).plus(
                listOf(
                    ArtistScreenItem.AlbumRowItem(
                        AlbumRowState(
                            albumId = "2",
                            albumName = "Ahora",
                            image = MediaArt(
                                uris = listOf(),
                                contentDescription = DisplayableString.StringValue(""),
                                backupResource = com.sebastianvm.commons.R.drawable.ic_album,
                                backupContentDescription = DisplayableString.StringValue("Album art placeholder")
                            ),
                            year = 2017,
                            artists = "Melendi"
                        ),
                    ),
                    ArtistScreenItem.AlbumRowItem(
                        AlbumRowState(
                            albumId = "3",
                            albumName = "Ahora",
                            image = MediaArt(
                                uris = listOf(),
                                contentDescription = DisplayableString.StringValue(""),
                                backupResource = com.sebastianvm.commons.R.drawable.ic_album,
                                backupContentDescription = DisplayableString.StringValue("Album art placeholder")
                            ),
                            year = 2017,
                            artists = "Melendi"
                        ),
                    ),
                )
            )
        )
    )
}


class SectionHeaderItemProvider : PreviewParameterProvider<ArtistScreenItem.SectionHeaderItem> {
    override val values = sequenceOf(ArtistScreenItem.SectionHeaderItem("ALBUMS", R.string.albums))
}

class ArtistViewItemProvider : PreviewParameterProvider<ArtistScreenItem> {
    override val values = sequenceOf(
        ArtistScreenItem.SectionHeaderItem("ALBUMS", R.string.albums),
        ArtistScreenItem.AlbumRowItem(
            AlbumRowState(
                albumId = "0",
                albumName = "Ahora",
                image = MediaArt(
                    uris = listOf(),
                    contentDescription = DisplayableString.StringValue(""),
                    backupResource = com.sebastianvm.commons.R.drawable.ic_album,
                    backupContentDescription = DisplayableString.StringValue("Album art placeholder")
                ),
                year = 2017,
                artists = "Melendi"
            ),
        )
    )
}
