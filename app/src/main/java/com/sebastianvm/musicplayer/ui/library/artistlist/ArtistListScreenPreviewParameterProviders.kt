package com.sebastianvm.musicplayer.ui.library.artistlist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.ArtistRowState
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder

class ArtistListStatePreviewParameterProvider : PreviewParameterProvider<ArtistListState> {
    override val values = sequenceOf(
        ArtistListState(
            artistList = listOf(
                ArtistRowState(artistId = 0, artistName = "Melendi", shouldShowContextMenu = true),
                ArtistRowState(
                    artistId = 1,
                    artistName = "Carlos Vives",
                    shouldShowContextMenu = true
                ),
                ArtistRowState(artistId = 2, artistName = "Morat", shouldShowContextMenu = true),
                ArtistRowState(artistId = 3, "LongName".repeat(10), shouldShowContextMenu = true),
            ),
            sortOrder = MediaSortOrder.ASCENDING,
        )
    )
}