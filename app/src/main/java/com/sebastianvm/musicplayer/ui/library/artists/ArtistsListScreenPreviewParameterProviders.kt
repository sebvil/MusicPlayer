package com.sebastianvm.musicplayer.ui.library.artists

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.ArtistRowState
import com.sebastianvm.musicplayer.util.SortOrder

class ArtistsListStatePreviewParameterProvider : PreviewParameterProvider<ArtistsListState> {
    override val values = sequenceOf(
        ArtistsListState(
            artistsList = listOf(
                ArtistRowState("Melendi", "Melendi", shouldShowContextMenu = true),
                ArtistRowState("Carlos Vives", "Carlos Vives", shouldShowContextMenu = true),
                ArtistRowState("Morat", "Morat", shouldShowContextMenu = true),
                ArtistRowState("LongName".repeat(10), "LongName".repeat(10), shouldShowContextMenu = true),
            ),
            sortOrder = SortOrder.ASCENDING
        )
    )
}


