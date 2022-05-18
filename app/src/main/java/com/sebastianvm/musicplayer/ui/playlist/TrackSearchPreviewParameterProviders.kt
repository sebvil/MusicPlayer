package com.sebastianvm.musicplayer.ui.playlist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class TrackSearchStatePreviewParameterProvider : PreviewParameterProvider<TrackSearchState> {
    override val values = sequenceOf(
        TrackSearchState(
            trackSearchResults = listOf(),
        ),
    )
}