package com.sebastianvm.musicplayer.ui.library.tracks

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions

class TrackListStatePreviewParameterProvider : PreviewParameterProvider<TrackListState> {
    override val values = sequenceOf(
        TrackListState(
            trackListId = 0,
            trackListName = "Pop",
            trackListType = TrackListType.GENRE,
            trackList = listOf(
                ModelListItemState(id = 0, headlineText = "La Promesa", supportingText = "Melendi"),
                ModelListItemState(id = 1, headlineText = "La Promesa", supportingText = "Melendi"),
                ModelListItemState(id = 2, headlineText = "La Promesa", supportingText = "Melendi"),
                ModelListItemState(id = 3, headlineText = "La Promesa", supportingText = "Melendi")
            ),
            sortPreferences = MediaSortPreferences(sortOption = SortOptions.TrackListSortOptions.TRACK)
        )
    )
}
