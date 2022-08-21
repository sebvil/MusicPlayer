package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState

class ArtistsBottomSheetStatePreviewParameterProvider :
    PreviewParameterProvider<ArtistsBottomSheetState> {
    override val values = sequenceOf(
        ArtistsBottomSheetState(
            artistList = listOf(
                ModelListItemState.Basic(id = 0, headlineText = "Melendi"),
                ModelListItemState.Basic(id = 1, headlineText = "Carlos Vives")
            ),
            artistIds = listOf(0, 1)
        )
    )
}
