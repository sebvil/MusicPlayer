package com.sebastianvm.musicplayer.ui.library.tracks

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.util.SortOrder

class TracksListStatePreviewParameterProvider : PreviewParameterProvider<TracksListState> {
    override val values = sequenceOf(
        TracksListState(
            "Pop",
            DisplayableString.StringValue("Pop"),
            listOf(
                TrackRowState("0", "La Promesa", "Melendi"),
                TrackRowState("1", "La Promesa", "Melendi"),
                TrackRowState("2", "La Promesa", "Melendi"),
                TrackRowState("3", "La Promesa", "Melendi")
            ),
            SortOption.TRACK_NAME,
            SortOrder.ASCENDING,
        )
    )
}