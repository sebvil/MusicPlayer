package com.sebastianvm.musicplayer.ui.playlist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlinx.coroutines.flow.emptyFlow

class TrackSearchStatePreviewParameterProvider : PreviewParameterProvider<TrackSearchState> {
    override val values = sequenceOf(
        TrackSearchState(
            trackSearchResults = emptyFlow(),
        ),
    )
}