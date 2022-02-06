package com.sebastianvm.musicplayer.ui.library.tracks

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder

class TracksListStatePreviewParameterProvider : PreviewParameterProvider<TracksListState> {
    override val values = sequenceOf(
        TracksListState(
            tracksListTitle = "Pop",
            listGroupType = MediaGroupType.GENRE,
            tracksList = listOf(
                TrackRowState("0", "La Promesa", "Melendi", "Un alumno mas"),
                TrackRowState("1", "La Promesa", "Melendi", "Un alumno mas"),
                TrackRowState("2", "La Promesa", "Melendi", "Un alumno mas"),
                TrackRowState("3", "La Promesa", "Melendi", "Un alumno mas")
            ),
            currentSort = SortOption.TRACK_NAME,
            sortOrder = SortOrder.ASCENDING,
        )
    )
}
