package com.sebastianvm.musicplayer.ui.library.artistlist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState

class ArtistListStatePreviewParameterProvider : PreviewParameterProvider<ArtistListState> {
    override val values = sequenceOf(
        ArtistListState(
            artistList = listOf(
                ModelListItemState(id = 0, headlineText = "Melendi"),
                ModelListItemState(id = 1, headlineText = "Carlos Vives")
            ),
        )
    )
}
