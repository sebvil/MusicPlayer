package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState

class ArtistsBottomSheetStatePreviewParameterProvider :
    PreviewParameterProvider<ArtistsBottomSheetState> {
    override val values = sequenceOf(
        ArtistsBottomSheetState(
            artistList = listOf(
                ModelListItemState(id = 0, headlineText = "Melendi"),
                ModelListItemState(id = 1, headlineText = "Carlos Vives")
            ),
            mediaType = MediaType.TRACK,
            mediaId = 23,
        )
    )
}
