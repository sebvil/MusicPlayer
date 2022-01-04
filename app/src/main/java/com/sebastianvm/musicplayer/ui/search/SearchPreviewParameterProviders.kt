package com.sebastianvm.musicplayer.ui.search

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.TrackRowState

class SearchStatePreviewParameterProvider : PreviewParameterProvider<SearchState> {
    override val values = sequenceOf(
        SearchState(
            searchTerm = "",
            trackSearchResults = listOf(),
            selectedOption = R.string.songs
        ),
        SearchState(
            searchTerm = "Test",
            trackSearchResults = listOf(
                TrackRowState(
                    trackId = "1",
                    trackName = "Test song",
                    artists = "Artist",
                    albumName = "Album",
                    trackNumber = null
                )
            ),
            selectedOption = R.string.songs
        )
    )
}