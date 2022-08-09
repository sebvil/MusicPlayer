package com.sebastianvm.musicplayer.ui.library.tracks

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions

class TrackListStatePreviewParameterProvider : PreviewParameterProvider<TrackListState> {
    override val values = sequenceOf(
        TrackListState(
            trackListId = 0,
            trackListName = "Pop",
            trackListType = TrackListType.GENRE,
            trackList = listOf(
                TrackRowState(0, 0, "La Promesa", "Melendi"),
                TrackRowState(1, 1, "La Promesa", "Melendi"),
                TrackRowState(2, 2, "La Promesa", "Melendi"),
                TrackRowState(3, 3, "La Promesa", "Melendi")
            ),
            sortPreferences = MediaSortPreferences(sortOption = SortOptions.TrackListSortOptions.TRACK)
        )
    )
}
