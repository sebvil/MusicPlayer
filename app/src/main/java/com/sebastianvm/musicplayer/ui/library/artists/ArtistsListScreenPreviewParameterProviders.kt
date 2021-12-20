package com.sebastianvm.musicplayer.ui.library.artists

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.ArtistRowState

class ArtistsListStatePreviewParameterProvider : PreviewParameterProvider<ArtistsListState> {
    override val values = sequenceOf(
        ArtistsListState(
            artistsList = listOf(
                ArtistRowState("Melendi", "Melendi"),
                ArtistRowState("Carlos Vives", "Carlos Vives"),
                ArtistRowState("Morat", "Morat"),
                ArtistRowState("LongName".repeat(10), "LongName".repeat(10)),
            )
        )
    )
}


