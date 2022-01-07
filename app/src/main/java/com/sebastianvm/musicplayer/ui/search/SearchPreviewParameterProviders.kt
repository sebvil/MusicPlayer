package com.sebastianvm.musicplayer.ui.search

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.TrackRowState

class SearchStatePreviewParameterProvider : PreviewParameterProvider<SearchState> {
    override val values = sequenceOf(
        SearchState(
            selectedOption = R.string.songs,
            trackSearchResults = listOf(),
            artistSearchResults = listOf(),
            albumSearchResults = listOf(),
            genreSearchResults = listOf(),
        ),
        SearchState(
            selectedOption = R.string.songs,
            trackSearchResults = listOf(
                TrackRowState(
                    trackId = "1",
                    trackName = "Test song",
                    artists = "Artist",
                    albumName = "Album",
                    trackNumber = null
                )
            ),
            artistSearchResults = listOf(),
            albumSearchResults = listOf(),
            genreSearchResults = listOf(),
        )
    )
}