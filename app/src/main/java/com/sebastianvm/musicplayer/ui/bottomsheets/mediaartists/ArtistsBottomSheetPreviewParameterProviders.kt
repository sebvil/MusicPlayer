package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.components.ArtistRowState

class ArtistsBottomSheetStatePreviewParameterProvider :
    PreviewParameterProvider<ArtistsBottomSheetState> {
    override val values = sequenceOf(
        ArtistsBottomSheetState(
            artistList = listOf(
                ArtistRowState(artistId = 0, artistName = "Melendi", shouldShowContextMenu = false),
                ArtistRowState(artistId = 1, artistName = "Carlos Vives", shouldShowContextMenu = false)
            ),
            mediaType = MediaType.TRACK,
            mediaId = 23,
        )
    )
}
