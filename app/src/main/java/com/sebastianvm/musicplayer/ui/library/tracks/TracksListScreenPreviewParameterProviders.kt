package com.sebastianvm.musicplayer.ui.library.tracks

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder

class TracksListStatePreviewParameterProvider : PreviewParameterProvider<TracksListState> {
    override val values = sequenceOf(
        TracksListState(
            tracksListTitle = "Pop",
            tracksListType = TracksListType.GENRE,
            tracksList = listOf(
                TrackRowState("0", "La Promesa", "Melendi", "Un alumno mas"),
                TrackRowState("1", "La Promesa", "Melendi", "Un alumno mas"),
                TrackRowState("2", "La Promesa", "Melendi", "Un alumno mas"),
                TrackRowState("3", "La Promesa", "Melendi", "Un alumno mas")
            ),
            currentSort = MediaSortOption.TRACK,
            sortOrder = MediaSortOrder.ASCENDING,
            events = null
        )
    )
}
