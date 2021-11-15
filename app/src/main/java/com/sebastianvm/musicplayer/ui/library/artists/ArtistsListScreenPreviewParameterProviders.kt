package com.sebastianvm.musicplayer.ui.library.artists

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class ArtistsListStatePreviewParameterProvider : PreviewParameterProvider<ArtistsListState> {
    override val values = sequenceOf(
        ArtistsListState(
            artistsList = listOf(
                ArtistsListItem("Melendi", "Melendi"),
                ArtistsListItem("Carlos Vives", "Carlos Vives"),
                ArtistsListItem("Morat", "Morat"),
                ArtistsListItem("LongName".repeat(10), "LongName".repeat(10)),
            )
        )
    )
}

class ArtistListItemProvider :
    PreviewParameterProvider<ArtistsListItem> {
    override val values = sequenceOf(
        ArtistsListItem("Melendi", "Melendi")
    )
}
