package com.sebastianvm.musicplayer.ui.library.albums

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.commons.R
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.commons.util.MediaArt
import com.sebastianvm.commons.util.DisplayableString

class AlbumsListStatePreviewParameterProvider : PreviewParameterProvider<AlbumsListState> {
    override val values = sequenceOf(
        AlbumsListState(
            albumsList = listOf(
                AlbumsListItem(
                    albumGid = "0",
                    albumRowState = AlbumRowState(
                        albumName = "Ahora",
                        image = MediaArt(
                            uris = listOf(),
                            contentDescription = DisplayableString.ResourceValue(
                                value = R.string.album_art_for_album,
                                arrayOf("Ahora")
                            ),
                            backupResource = R.drawable.ic_album,
                            backupContentDescription = DisplayableString.ResourceValue(R.string.placeholder_album_art)

                        ),
                        artists = "Melendi",
                        year = 2017
                    )
                ),
                AlbumsListItem(
                    albumGid = "1",
                    albumRowState = AlbumRowState(
                        albumName = "VIVES",
                        image = MediaArt(
                            uris = listOf(),
                            contentDescription = DisplayableString.ResourceValue(
                                value = R.string.album_art_for_album,
                                arrayOf("VIVES")
                            ),
                            backupResource = R.drawable.ic_album,
                            backupContentDescription = DisplayableString.ResourceValue(R.string.placeholder_album_art)

                        ),
                        artists = "Carlos Vives",
                        year = 2018
                    )
                ),
                AlbumsListItem(
                    albumGid = "2",
                    albumRowState = AlbumRowState(
                        albumName = "Balas perdidas",
                        image = MediaArt(
                            uris = listOf(),
                            contentDescription = DisplayableString.ResourceValue(
                                value = R.string.album_art_for_album,
                                arrayOf("Balas perdidas")
                            ),
                            backupResource = R.drawable.ic_album,
                            backupContentDescription = DisplayableString.ResourceValue(R.string.placeholder_album_art)

                        ),
                        artists = "Morat",
                        year = 2019
                    )
                ),
                AlbumsListItem(
                    albumGid = "3",
                    albumRowState = AlbumRowState(
                        albumName = "LongName".repeat(10),
                        image = MediaArt(
                            uris = listOf(),
                            contentDescription = DisplayableString.ResourceValue(
                                value = R.string.album_art_for_album,
                                arrayOf("LongName".repeat(10))
                            ),
                            backupResource = R.drawable.ic_album,
                            backupContentDescription = DisplayableString.ResourceValue(R.string.placeholder_album_art)
                        ),
                        artists = "LongName".repeat(10),
                        year = 2014
                    )
                ),
            )
        )
    )
}